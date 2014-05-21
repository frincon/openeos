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

import java.util.Stack;

import org.hibernate.SessionFactoryObserver;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.hibernate.internal.configurators.BundleModelClassConfigurator;
import org.openeos.hibernate.internal.configurators.FilterConfigurator;
import org.openeos.hibernate.internal.configurators.FixedConfigurator;
import org.openeos.hibernate.internal.configurators.ObserverConfigurator;
import org.openeos.hibernate.internal.observers.DialectPublisherObserver;
import org.openeos.hibernate.internal.observers.ListenerObserver;

public class Activator implements BundleActivator {

	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

	// TODO Make this whith configuration
	private static final String DATA_SOURCE_FILTER = "(&(objectclass=javax.sql.DataSource)(osgi.jndi.service.name=unoDataSource))";

	private BundleContext context;

	private Stack<ServiceRegistration<?>> serviceRegistrationStack = new Stack<ServiceRegistration<?>>();

	private SessionFactoryManager sessionFactoryManager;

	private DialectPublisherObserver dialectPublisherObserver;
	private ListenerObserver listenerObserver;
	private SessionObserverManager sessionObserver;

	@Override
	public void start(BundleContext context) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String
					.format("Starting uno hibernate bundle whith symbilic name '%s'", context.getBundle().getSymbolicName()));
		}
		this.context = context;

		registerObserverServices();

		LOG.debug("Creating configuration provider");
		ConfigurationProvider configurationProvider = new ConfigurationProviderImpl();
		createConfigurators(configurationProvider);

		LOG.debug("Creating DataSourceProvider");
		DataSourceProvider dataSourceProvider = createDataSourceProvider();

		LOG.debug("Creating ConnectionProviderBuilder");
		ConnectionProviderBuilder connectionProviderBuilder = new ConnectionProviderBuilderImpl();
		connectionProviderBuilder.with(dataSourceProvider);

		LOG.debug("Creating SessionObserver");
		sessionObserver = new SessionObserverManager(context);
		sessionObserver.init();

		LOG.debug("Creating ServiceRegistryBuilder");
		ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilderImpl();
		serviceRegistryBuilder.with(configurationProvider);
		serviceRegistryBuilder.with(connectionProviderBuilder);
		serviceRegistryBuilder.with(sessionObserver);

		LOG.debug("Creating SessionFactoryManager");
		sessionFactoryManager = new SessionFactoryManagerOsgiService(context, serviceRegistryBuilder, configurationProvider);
		sessionFactoryManager.registerOnChanges(dataSourceProvider);
		sessionFactoryManager.registerOnChanges(configurationProvider);
		sessionFactoryManager.init();

	}

	private DataSourceProvider createDataSourceProvider() throws InvalidSyntaxException {
		DataSourceProviderOsgiService dataSourceProvider = new DataSourceProviderOsgiService(context,
				context.createFilter(DATA_SOURCE_FILTER));
		dataSourceProvider.init();
		return dataSourceProvider;
	}

	private void registerObserverServices() {
		LOG.debug("Registering observer services");
		dialectPublisherObserver = new DialectPublisherObserver(context);
		listenerObserver = new ListenerObserver(context);
		listenerObserver.init();

		serviceRegistrationStack.push(context.registerService(SessionFactoryObserver.class, dialectPublisherObserver, null));
		LOG.debug("DialectPublisherObserver registered");
		serviceRegistrationStack.push(context.registerService(SessionFactoryObserver.class, listenerObserver, null));
		LOG.debug("ListenerObserver registered");
	}

	private void createConfigurators(ConfigurationProvider configurationProvider) {
		LOG.debug("Creating FixedConfigurator");
		new FixedConfigurator().init(configurationProvider);
		LOG.debug("Creating BundleModelClassConfigurator");
		new BundleModelClassConfigurator(context).init(configurationProvider);
		LOG.debug("Creating ObserverConfigurator");
		new ObserverConfigurator(context).init(configurationProvider);
		LOG.debug("Creating FilterConfigurator");
		new FilterConfigurator(context).init(configurationProvider);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		sessionFactoryManager.destroy();
		while (!serviceRegistrationStack.isEmpty()) {
			serviceRegistrationStack.pop().unregister();
		}
		sessionObserver.destroy();
		listenerObserver.destroy();
		dialectPublisherObserver.destroy();
	}

}
