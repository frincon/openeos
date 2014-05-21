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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.DeleteEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.dao.Constants;
import org.openeos.dao.DeleteListener;
import org.openeos.dao.SaveOrUpdateListener;
import org.openeos.dao.UserException;

public class DAOListenerManager {

	private static final Logger LOG = LoggerFactory.getLogger(DAOListenerManager.class);

	private Map<Class<?>, Map<Class<?>, Set<Object>>> mapAllListener = new HashMap<Class<?>, Map<Class<?>, Set<Object>>>();

	private SaveOrUpdateEventListener beforeSaveOrUpdateListener = new SaveOrUpdateEventListener() {

		private static final long serialVersionUID = -3970398110394540050L;

		@Override
		public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException {
			fireSaveOrUpdateEvent(event, true);
		}
	};

	private SaveOrUpdateEventListener afterSaveOrUpdateListener = new SaveOrUpdateEventListener() {

		private static final long serialVersionUID = 1788299348159006633L;

		@Override
		public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException {
			fireSaveOrUpdateEvent(event, false);
		}
	};

	private DeleteEventListener beforeDeleteListener = new DeleteEventListener() {

		@Override
		public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
			fireDeleteEvent(event, true);
		}

		@Override
		public void onDelete(DeleteEvent event) throws HibernateException {
			fireDeleteEvent(event, true);
		}
	};

	private DeleteEventListener afterDeleteListener = new DeleteEventListener() {

		@Override
		public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
			fireDeleteEvent(event, false);
		}

		@Override
		public void onDelete(DeleteEvent event) throws HibernateException {
			fireDeleteEvent(event, false);
		}
	};

	public void bindListener(Object listener, Map<String, Object> properties) {
		Class<?> classExpected = (Class<?>) properties.get(Constants.SERVICE_CLASS_EXPECTED);
		if (listener instanceof SaveOrUpdateListener<?>) {
			SaveOrUpdateListener<?> specificListener = (SaveOrUpdateListener<?>) listener;
			addListener(SaveOrUpdateListener.class, classExpected, specificListener);
		} else if (listener instanceof DeleteListener<?>) {
			DeleteListener<?> specificListener = (DeleteListener<?>) listener;
			addListener(DeleteListener.class, classExpected, specificListener);
		} else {
			LOG.warn("Trying to bind listener of unknown type '{}'", listener.getClass().getInterfaces());
		}
	}

	public void unbindListener(Object listener, Map<String, Object> properties) {
		Class<?> classExpected = (Class<?>) properties.get(Constants.SERVICE_CLASS_EXPECTED);
		if (listener instanceof SaveOrUpdateListener<?>) {
			SaveOrUpdateListener<?> specificListener = (SaveOrUpdateListener<?>) listener;
			removeListener(SaveOrUpdateListener.class, classExpected, specificListener);
		} else if (listener instanceof DeleteListener<?>) {
			DeleteListener<?> specificListener = (DeleteListener<?>) listener;
			removeListener(DeleteListener.class, classExpected, specificListener);
		} else {
			LOG.warn("Trying to unbind listener of unknown type '{}'", listener.getClass().getInterfaces());
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Collection<T> getListenerByObject(Class<T> listenerClass, Object object) {
		Map<Class<?>, Set<Object>> mapListener = mapAllListener.get(listenerClass);
		List<T> listenerList = new ArrayList<T>();
		if (mapListener != null) {
			for (Class<?> classExpected : mapListener.keySet()) {
				if (classExpected.isInstance(object)) {
					listenerList.addAll((Collection<? extends T>) mapListener.get(classExpected));
				}
			}
		}
		return listenerList;

	}

	public SaveOrUpdateEventListener getPreSaveOrUpdateListener() {
		return beforeSaveOrUpdateListener;
	}

	public SaveOrUpdateEventListener getPostSaveOrUpdateListener() {
		return afterSaveOrUpdateListener;
	}

	public DeleteEventListener getBeforeDeleteListener() {
		return beforeDeleteListener;
	}

	public DeleteEventListener getAfterDeleteListener() {
		return afterDeleteListener;
	}

	@SuppressWarnings("unchecked")
	private void fireSaveOrUpdateEvent(SaveOrUpdateEvent event, boolean before) {
		org.openeos.dao.SaveOrUpdateEvent<Object> ourEvent = new org.openeos.dao.SaveOrUpdateEvent<Object>(
				event.getSession(), event.getObject(), event.getResultId());
		try {
			for (SaveOrUpdateListener<Object> listener : getListenerByObject(SaveOrUpdateListener.class, event.getObject())) {
				LOG.debug(MessageFormat.format("Calling SaveOrUpdateListener. Object: {0} Before: {1} Listener: {2}", event
						.getObject().toString(), Boolean.toString(before), listener.toString()));
				if (before) {
					listener.beforeSaveOrUpdate(ourEvent);
				} else {
					listener.afterSaveOrUpdate(ourEvent);
				}
			}
		} catch (UserException ex) {
			// TODO
			throw new HibernateException(ex);
		}

	}

	@SuppressWarnings("unchecked")
	private void fireDeleteEvent(DeleteEvent event, boolean before) {
		org.openeos.dao.DeleteEvent<Object> ourEvent = new org.openeos.dao.DeleteEvent<Object>(event.getSession(),
				event.getObject());
		try {
			for (DeleteListener<Object> listener : getListenerByObject(DeleteListener.class, event.getObject())) {
				LOG.debug("Calling DeleteListener");
				if (before) {
					listener.beforeDelete(ourEvent);
				} else {
					listener.afterDelete(ourEvent);
				}
			}
		} catch (UserException ex) {
			// TODO
			throw new HibernateException(ex);
		}
	}

	private synchronized <T> void addListener(Class<T> listenerClass, Class<?> expectedClass, T listener) {
		LOG.debug("Adding listener of class '{}' to listener list", listenerClass);
		Set<T> listenerSet = getListenerSet(listenerClass, expectedClass);
		listenerSet.add(listener);
	}

	private synchronized <T> void removeListener(Class<T> listenerClass, Class<?> expectedClass, T listener) {
		LOG.debug("Removing listener of class '{}' to listener list", listenerClass);
		Set<T> listenerSet = getListenerSet(listenerClass, expectedClass);
		listenerSet.remove(listener);
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> getListenerSet(Class<T> listenerClass, Class<?> expectedClass) {
		Map<Class<?>, Set<Object>> mapListener = mapAllListener.get(listenerClass);
		if (mapListener == null) {
			mapListener = new HashMap<Class<?>, Set<Object>>();
			mapAllListener.put(listenerClass, mapListener);
		}
		Set<Object> listenerSet = mapListener.get(expectedClass);
		if (listenerSet == null) {
			listenerSet = new HashSet<Object>();
			mapListener.put(expectedClass, listenerSet);
		}
		return (Set<T>) listenerSet;
	}

}
