/**
 * Copyright 2014 Fernando Rincon Martin <frm.rincon@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openeos.hibernate.internal;

import java.lang.reflect.Proxy;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;

public class SessionFactoryManagerOsgiService implements SessionFactoryManager, ParentListener, SessionFactoryBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryManagerOsgiService.class);

	private BundleContext bundleContext;
	private ServiceRegistryBuilder serviceRegistryBuilder;
	private ConfigurationProvider configurationProvider;

	private SessionFactoryProxyHandler proxyHandler;
	private ServiceRegistration<SessionFactory> sessionFactoryRegistration;
	private SessionFactory proxySessionFactory;

	public SessionFactoryManagerOsgiService(BundleContext bundleContext, ServiceRegistryBuilder serviceRegistryBuilder,
			ConfigurationProvider configurationProvider) {
		this.bundleContext = bundleContext;
		this.serviceRegistryBuilder = serviceRegistryBuilder;
		this.configurationProvider = configurationProvider;
	}

	@Override
	public void registerOnChanges(ParentNotificator parentNotificator) {
		parentNotificator.addParentListener(this);
	}

	@Override
	public void unregisterOnChanges(ParentNotificator parentNotificator) {
		parentNotificator.removeParentListener(this);
	}

	@Override
	public void init() {
		LOG.debug("Initializing session factory proxy and registering it as OSGi Service");
		proxyHandler = new SessionFactoryProxyHandler(this);
		proxySessionFactory = (SessionFactory) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class<?>[] { SessionFactory.class }, proxyHandler);
		sessionFactoryRegistration = bundleContext.registerService(SessionFactory.class, proxySessionFactory, null);

		LOG.debug("Retrieving new session to establish initial session factory");
		proxySessionFactory.openSession().close();
	}

	@Override
	public void destroy() {
		proxySessionFactory = null;
		sessionFactoryRegistration.unregister();
		sessionFactoryRegistration = null;
		proxyHandler.invalidateSessionFactory();
		proxyHandler = null;
	}

	@Override
	public void invalidate() {
		LOG.debug("Invalidating session factory");
		proxyHandler.invalidateSessionFactory();
	}

	@Override
	public synchronized SessionFactory buildSessionFactory() {
		try {
			LOG.debug("Building new session factory");
			SessionFactory real = configurationProvider.getConfiguration().buildSessionFactory(
					this.serviceRegistryBuilder.buildServiceRegistry());

			LOG.debug("Session factory created, creating new Transaction Manager to handle transactions");
			HibernateTransactionManager txManager = new HibernateTransactionManager();

			txManager.setDataSource(((DatasourceConnectionProviderImpl) ((SessionFactoryImplementor) real).getServiceRegistry()
					.getService(ConnectionProvider.class)).getDataSource());
			txManager.setSessionFactory(proxySessionFactory);
			txManager.setValidateExistingTransaction(true);
			txManager.afterPropertiesSet();

			// The anotations at this point uses the new transaction manager
			AnnotationTransactionAspect txAspect = AnnotationTransactionAspect.aspectOf();
			txAspect.setTransactionManager(txManager);
			LOG.debug("New transaction manager established to AnnotationTransactionAspect, now the annotations used the new Transaction Manager");

			return real;
		} catch (RuntimeException ex) {
			LOG.error("Runtime exception when trying to create session factory.", ex);
			throw ex;
		}
	}
}
