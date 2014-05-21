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
package org.openeos.services.ui.internal;

import java.util.SortedMap;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import org.openeos.services.ui.CustomActionManagerService;
import org.openeos.services.ui.action.CustomAction;

public class CustomActionManagerOsgiService implements CustomActionManagerService {

	private BundleContext bundleContext;

	private SortedMap<ServiceReference<CustomAction>, CustomAction> customActionMap = new TreeMap<ServiceReference<CustomAction>, CustomAction>();

	public CustomActionManagerOsgiService(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void bindCustomAction(ServiceReference<CustomAction> serviceRef) throws InvalidSyntaxException {
		ServiceReference<CustomAction> otherServiceRef = bundleContext
				.getServiceReferences(CustomAction.class,
						"(" + Constants.SERVICE_ID + "=" + serviceRef.getProperty(Constants.SERVICE_ID) + ")").iterator().next();
		CustomAction customAction = bundleContext.getService(otherServiceRef);
		customActionMap.put(otherServiceRef, customAction);
	}

	public void unbindCustomAction(ServiceReference<CustomAction> serviceRef) throws InvalidSyntaxException {
		if (serviceRef != null) {
			ServiceReference<CustomAction> otherServiceRef = bundleContext
					.getServiceReferences(CustomAction.class,
							"(" + Constants.SERVICE_ID + "=" + serviceRef.getProperty(Constants.SERVICE_ID) + ")").iterator()
					.next();
			bundleContext.ungetService(otherServiceRef);
			customActionMap.remove(otherServiceRef);
		}
	}

	@Override
	public CustomAction getCustomAction(String customActionId) {
		// TODO Make better implementation
		for (CustomAction customAction : customActionMap.values()) {
			if (customAction.getId().equals(customActionId)) {
				return customAction;
			}
		}
		return null;
	}

}
