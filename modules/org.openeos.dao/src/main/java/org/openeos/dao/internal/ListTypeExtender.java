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
package org.openeos.dao.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.dao.ListType;
import org.openeos.dao.ListTypeService;

public class ListTypeExtender {

	public static final String LIST_TYPE_ADDITIONS_HEADER = "Uno-ListTypeAdditions-Classes";

	private static final Logger LOG = LoggerFactory.getLogger(ListTypeExtender.class);

	private BundleContext context;
	private ListTypeService listTypeService;
	
	private BundleTracker<Bundle> bundleTracker;

	private Map<Long, List<Object[]>> registeredTypes = new HashMap<Long, List<Object[]>>();

	public void setContext(BundleContext context) {
		this.context = context;
	}

	public void setListTypeService(ListTypeService listTypeService) {
		this.listTypeService = listTypeService;
	}

	public void init() {
		bundleTracker = new BundleTracker<Bundle>(context, Bundle.ACTIVE, null) {

			@Override
			public Bundle addingBundle(Bundle bundle, BundleEvent event) {
				onBundleAdded(bundle);
				return bundle;
			}

			@Override
			public void removedBundle(Bundle bundle, BundleEvent event, Bundle object) {
				onBundleRemoved(bundle);
			}

		};
		bundleTracker.open();
	}

	private void onBundleAdded(Bundle bundle) {
		String listTypeHeader = bundle.getHeaders().get(LIST_TYPE_ADDITIONS_HEADER);
		if (listTypeHeader != null) {
			LOG.debug("Found bundle with header {}:{}", new Object[] { LIST_TYPE_ADDITIONS_HEADER, listTypeHeader });
			String[] classes = listTypeHeader.split(",");
			for (int i = 0; i < classes.length; i++) {
				inspectListClass(bundle, classes[i]);
			}
		}
	}

	private void onBundleRemoved(Bundle bundle) {
		LOG.debug("Unregistering bundle {}", bundle.getSymbolicName());
		unregisterBundle(bundle.getBundleId());
	}

	private void inspectListClass(Bundle bundle, String className) {
		LOG.debug("Trying to inspect class {} in bundle {} contain static fields of ListType",
				new Object[] { className, bundle.getSymbolicName() });
		try {
			Class<?> loadedClass = bundle.loadClass(className);
			Field[] fields = loadedClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				LOG.debug("Checking field {} for list type objects", field.getName());
				if (Modifier.isStatic(field.getModifiers())) {
					if (ListType.class.isAssignableFrom(field.getType())) {
						registerField(bundle, field);
					}
				}
			}
		} catch (ClassNotFoundException ex) {
			LOG.warn("A ClassNotFoundException occured while loading class {} from bundle {}",
					new Object[] { className, bundle.getSymbolicName() });
		}
	}

	private void registerField(Bundle bundle, Field field) {
		try {
			Object typeList = field.get(null);
			Class<?> fieldType = field.getType();
			LOG.debug("Registering static field for ListType. Field: {} Type: {} Object: {}", new Object[] { field.getName(),
					fieldType, typeList });
			listTypeService.registerElement((Class<ListType>) fieldType, (ListType) typeList);
			Object[] register = new Object[] { field.getType(), typeList };
			getList(bundle.getBundleId()).add(register);
		} catch (Exception ex) {
			LOG.warn("An error ocurred while trying to register ListType static field {} from class {}", new Object[] { field,
					field.getDeclaringClass() });
		}
	}

	private List<Object[]> getList(Long bundleId) {
		List<Object[]> list = registeredTypes.get(bundleId);
		if (list == null) {
			list = new ArrayList<Object[]>();
			registeredTypes.put(bundleId, list);
		}
		return list;
	}

	public void destroy() {
		for (Long bundleId : registeredTypes.keySet()) {
			unregisterBundle(bundleId);
		}
		bundleTracker.close();
		registeredTypes.clear();
	}

	private void unregisterBundle(long bundleId) {
		List<Object[]> registered = registeredTypes.get(bundleId);
		if (registered != null) {
			for (Object[] register : registered) {
				listTypeService.unregisterElement((Class<ListType>) register[0], (ListType) register[1]);
			}
		}
	}

}
