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
package org.openeos.hibernate.internal.configurators;

import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.hibernate.internal.ConfigurationProvider;

public class ObserverConfigurator implements Configurator, SessionFactoryObserver {

	private static final Logger LOG = LoggerFactory.getLogger(ObserverConfigurator.class);

	private static final long serialVersionUID = 1L;

	private BundleContext bundleContext;

	private ServiceTracker<SessionFactoryObserver, SessionFactoryObserver> tracker;

	public ObserverConfigurator(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	@Override
	public void init(ConfigurationProvider configurationProvider) {
		LOG.debug("Initializing observer configuration");
		tracker = new ServiceTracker<SessionFactoryObserver, SessionFactoryObserver>(bundleContext, SessionFactoryObserver.class,
				null);
		tracker.open();
		bundleContext.addBundleListener(new SynchronousBundleListener() {

			@Override
			public void bundleChanged(BundleEvent event) {
				if (event.getBundle().equals(bundleContext.getBundle()) && event.getType() == BundleEvent.STOPPING) {
					tracker.close();
					tracker = null;
				}
			}
		});
		configurationProvider.getConfiguration().setSessionFactoryObserver(this);
		configurationProvider.invalidate();
	}

	@Override
	public void sessionFactoryCreated(SessionFactory factory) {
		LOG.debug("Session factory has been created, calling all SessionFactoryObserver services");
		for (SessionFactoryObserver observer : tracker.getTracked().values()) {
			observer.sessionFactoryCreated(factory);
		}
	}

	@Override
	public void sessionFactoryClosed(SessionFactory factory) {
		LOG.debug("Session factory has been closed, calling all SessionFactoryObserver services");
		for (SessionFactoryObserver observer : tracker.getTracked().values()) {
			observer.sessionFactoryClosed(factory);
		}

	}
}
