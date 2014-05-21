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

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import org.openeos.services.ui.ThrowableToMessageResolver;
import org.openeos.services.ui.ThrowableToMessageService;

public class ThrowableToMessageServiceImpl implements ThrowableToMessageService {

	private BundleContext bundleContext;
	private ServiceTracker<ThrowableToMessageResolver, ThrowableToMessageResolver> tracker;

	public ThrowableToMessageServiceImpl(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void init() {
		tracker = new ServiceTracker<ThrowableToMessageResolver, ThrowableToMessageResolver>(bundleContext,
				ThrowableToMessageResolver.class, null);
		tracker.open();
	}

	public void destroy() {
		tracker.close();
		tracker = null;
	}

	@Override
	public String resolveMessage(Throwable t) {
		String result = null;
		for (ThrowableToMessageResolver resolver : tracker.getTracked().values()) {
			result = resolver.resolveMessage(t);
			if (result != null) {
				return result;
			}
		}

		return t.getLocalizedMessage();
	}

}
