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

import org.hibernate.Session;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.hibernate.SessionObserver;

public class SessionObserverManager implements SessionObserver {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SessionObserverManager.class);

	private BundleContext bundleContext;

	private ServiceTracker<SessionObserver, SessionObserver> tracker;

	public SessionObserverManager(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void init() {
		LOG.debug("Initializing session observer manager");
		tracker = new ServiceTracker<SessionObserver, SessionObserver>(bundleContext, SessionObserver.class, null);
		tracker.open();
	}

	public void destroy() {
		tracker.close();
		tracker = null;
	}

	@Override
	public void sessionCreated(Session session) {
		LOG.debug("Session has been created, calling all SessionObserver services");
		for (SessionObserver observer : tracker.getTracked().values()) {
			observer.sessionCreated(session);
		}

	}

	@Override
	public void sessionClosed(Session session) {
		LOG.debug("Session has been closed, calling all SessionObserver services");
		for (SessionObserver observer : tracker.getTracked().values()) {
			observer.sessionClosed(session);
		}
	}

}
