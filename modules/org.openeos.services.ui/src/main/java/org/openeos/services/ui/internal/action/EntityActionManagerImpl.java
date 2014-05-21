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
package org.openeos.services.ui.internal.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import org.openeos.services.ui.action.EntityAction;
import org.openeos.services.ui.action.EntityActionManager;

@SuppressWarnings("rawtypes")
public class EntityActionManagerImpl implements EntityActionManager {

	private BundleContext bundleContext;

	private ServiceTracker<EntityAction, EntityAction> serviceTracker;

	public EntityActionManagerImpl(BundleContext bundleContext) {
		if (bundleContext == null) {
			throw new IllegalArgumentException();
		}
		this.bundleContext = bundleContext;
	}

	public void init() {
		serviceTracker = new ServiceTracker<EntityAction, EntityAction>(bundleContext, EntityAction.class, null);
		serviceTracker.open();
	}

	public void destroy() {
		serviceTracker.close();
		serviceTracker = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<EntityAction<T>> getEnabledActionsOrdered(Class<T> entityClass, List<T> selectedObjects) {
		TreeSet<EntityAction<T>> result = new TreeSet<EntityAction<T>>(new Comparator<EntityAction<T>>() {

			@Override
			public int compare(EntityAction o1, EntityAction o2) {
				if (o1.getGroup() == null) {
					if (o2.getGroup() == null) {
						return o1.getName().compareTo(o2.getName());
					} else {
						return -1;
					}
				} else {
					if (o2.getGroup() == null) {
						return 1;
					} else {
						int result = o1.getGroup().compareTo(o2.getGroup());
						if (result == 0) {
							result = o1.getName().compareTo(o2.getName());
						}
						return result;
					}
				}
			}

		});
		Object[] entityActions = serviceTracker.getServices();
		if (entityActions != null) {
			for (Object obj : entityActions) {
				if (obj instanceof EntityAction) {
					EntityAction entityAction = (EntityAction) obj;
					if (entityAction.getEntityClass().isAssignableFrom(entityClass) && entityAction.canExecute(selectedObjects)) {
						result.add(entityAction);
					}
				}
			}
		}
		return Collections.unmodifiableList(new ArrayList<EntityAction<T>>(result));
	}
}
