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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class DAOListenerManagerTracker<S> extends ServiceTracker<S, S> {

	private DAOListenerManager listenerManager;

	public DAOListenerManagerTracker(BundleContext context, DAOListenerManager listenerManager, Class<S> listenerClass) {
		super(context, listenerClass, null);
		this.listenerManager = listenerManager;
	}

	@Override
	public S addingService(ServiceReference<S> reference) {
		S listener = context.getService(reference);
		listenerManager.bindListener(listener, convertProperties(reference));
		return listener;
	}

	private Map<String, Object> convertProperties(ServiceReference<S> reference) {
		String[] keys = reference.getPropertyKeys();
		Map<String, Object> mapResult = new HashMap<String, Object>();
		for (int i = 0; i < keys.length; i++) {
			mapResult.put(keys[i], reference.getProperty(keys[i]));
		}
		return Collections.unmodifiableMap(mapResult);
	}

	@Override
	public void removedService(ServiceReference<S> reference, S service) {
		listenerManager.unbindListener(service, convertProperties(reference));
		context.ungetService(reference);
	}

}
