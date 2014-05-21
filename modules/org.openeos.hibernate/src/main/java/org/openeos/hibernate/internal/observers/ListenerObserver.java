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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.service.ServiceRegistry;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.dao.DeleteListener;
import org.openeos.dao.SaveOrUpdateListener;

public class ListenerObserver implements SessionFactoryObserver {

	private static final Logger LOG = LoggerFactory.getLogger(ListenerObserver.class);

	private static final long serialVersionUID = 1L;

	private List<DAOListenerManagerTracker<?>> daoListenerManagerTrackerList = new LinkedList<DAOListenerManagerTracker<?>>();

	@SuppressWarnings("rawtypes")
	private Map<EventType, Object> mapPreListeners = new HashMap<EventType, Object>();
	@SuppressWarnings("rawtypes")
	private Map<EventType, Object> mapPostListeners = new HashMap<EventType, Object>();

	private BundleContext bundleContext;

	public ListenerObserver(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void init() {
		LOG.debug("Initializing listener for pre and post events for hibernate");
		DAOListenerManager listenerManager = new DAOListenerManager();

		registerListener(bundleContext, listenerManager, SaveOrUpdateListener.class, EventType.SAVE_UPDATE,
				listenerManager.getPreSaveOrUpdateListener(), listenerManager.getPostSaveOrUpdateListener());
		registerListener(bundleContext, listenerManager, DeleteListener.class, EventType.DELETE,
				listenerManager.getBeforeDeleteListener(), listenerManager.getAfterDeleteListener());

	}

	public void destroy() {
		for (DAOListenerManagerTracker<?> tracker : daoListenerManagerTrackerList) {
			tracker.close();
		}
		daoListenerManagerTrackerList.clear();
	}

	private <T, S> void registerListener(BundleContext context, DAOListenerManager listenerManager, Class<S> listenerClass,
			EventType<T> eventType, T preListener, T postListener) {
		DAOListenerManagerTracker<S> tracker;

		// The SaveOrUpdate Event Listeners
		tracker = new DAOListenerManagerTracker<S>(context, listenerManager, listenerClass);
		tracker.open();
		mapPreListeners.put(eventType, preListener);
		mapPostListeners.put(eventType, postListener);
		daoListenerManagerTrackerList.add(tracker);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void sessionFactoryCreated(SessionFactory factory) {
		LOG.debug("Session factory created... registering listeners in service registry");
		ServiceRegistry serviceRegistry = ((SessionFactoryImplementor) factory).getServiceRegistry();
		for (Entry<EventType, Object> entry : mapPreListeners.entrySet()) {
			EventListenerGroup group = serviceRegistry.getService(EventListenerRegistry.class)
					.getEventListenerGroup(entry.getKey());
			group.prependListener(entry.getValue());
		}
		for (Entry<EventType, Object> entry : mapPostListeners.entrySet()) {
			EventListenerGroup group = serviceRegistry.getService(EventListenerRegistry.class)
					.getEventListenerGroup(entry.getKey());
			group.appendListener(entry.getValue());
		}

	}

	@Override
	public void sessionFactoryClosed(SessionFactory factory) {
		// Nothing to do
	}

}
