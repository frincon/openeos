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
package org.openeos.hibernate.internal.observers;

import java.util.Hashtable;

import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.exception.spi.ConversionContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DialectPublisherObserver implements SessionFactoryObserver {

	private static final Logger LOG = LoggerFactory.getLogger(DialectPublisherObserver.class);

	private static final long serialVersionUID = 1L;

	public static final String DIALECT_PID = "unoDialect";

	private BundleContext bundleContext;

	private ServiceRegistration<?> dialectService;

	public DialectPublisherObserver(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void destroy() {
		if (dialectService != null) {
			dialectService.unregister();
			dialectService = null;
		}
	}

	@Override
	public void sessionFactoryCreated(SessionFactory factory) {
		LOG.debug("Session factory created, registering its dialect as a service");
		if (dialectService != null) {
			LOG.debug("Unregistering old dialect");
			dialectService.unregister();
			dialectService = null;
		}
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, DIALECT_PID);
		dialectService = bundleContext.registerService(new String[] { Dialect.class.getName(), ConversionContext.class.getName() },
				((SessionFactoryImplementor) factory).getDialect(), properties);

		LOG.debug("Dialect registered as a service with service.pid='{}'", DIALECT_PID);
	}

	@Override
	public void sessionFactoryClosed(SessionFactory factory) {
		LOG.debug("Session factory Closed, unregistering dialect service");
		dialectService.unregister();
		dialectService = null;
	}

}
