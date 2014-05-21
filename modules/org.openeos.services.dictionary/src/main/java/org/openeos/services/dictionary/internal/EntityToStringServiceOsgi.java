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
package org.openeos.services.dictionary.internal;

import java.util.Map.Entry;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import org.openeos.services.dictionary.EntityToStringResolver;
import org.openeos.services.dictionary.EntityToStringService;

public class EntityToStringServiceOsgi implements EntityToStringService {

	private ServiceTracker<EntityToStringResolver, EntityToStringResolver> entityToStringTracker;

	private BundleContext bundleContext;

	public EntityToStringServiceOsgi(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void init() {
		entityToStringTracker = new ServiceTracker<EntityToStringResolver, EntityToStringResolver>(bundleContext,
				EntityToStringResolver.class, null);
		entityToStringTracker.open();
	}

	public void destroy() {
		entityToStringTracker.close();
		entityToStringTracker = null;
	}

	@Override
	public String resolveStringRepresentation(Object entity) {
		String result = null;
		for (Entry<ServiceReference<EntityToStringResolver>, EntityToStringResolver> entry : entityToStringTracker.getTracked()
				.entrySet()) {
			result = entry.getValue().tryResolveString(entity);
			if (result != null) {
				break;
			}
		}
		if (result == null) {
			result = entity.toString();
		}
		return result;
	}
}
