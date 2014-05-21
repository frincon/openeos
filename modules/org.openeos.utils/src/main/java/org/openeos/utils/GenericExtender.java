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
package org.openeos.utils;

import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericExtender<T> {

	private static final Logger LOG = LoggerFactory.getLogger(GenericExtender.class);

	private ExtenderHandler<T> extenderHandler;
	private BundleContext bundleContext;
	private BundleFilter<T> bundleFilter;
	private int stateMask;
	private BundleTracker<Bundle> bundleTracker;

	private Set<Bundle> bundleRegistered = new HashSet<Bundle>();

	//Compatibility with extender handler header based implementation
	@SuppressWarnings("unchecked")
	public GenericExtender(ExtenderHeaderHandler extenderHandler, BundleContext bundleContext) {
		this((ExtenderHandler<T>) extenderHandler, bundleContext, Bundle.ACTIVE,
				(BundleFilter<T>) new HeaderBundleFilter(extenderHandler.getHeaderName()));
	}

	public GenericExtender(ExtenderHandler<T> extenderHandler, BundleContext bundleContext, int stateMask,
			BundleFilter<T> bundleFilter) {
		this.extenderHandler = extenderHandler;
		this.bundleContext = bundleContext;
		this.stateMask = stateMask;
		this.bundleFilter = bundleFilter;
	}

	public void init() {
		LOG.debug("Starting Generic extender width handler of class: " + extenderHandler.getClass());
		bundleTracker = new BundleTracker<Bundle>(bundleContext, stateMask, null) {

			@Override
			public Bundle addingBundle(Bundle bundle, BundleEvent event) {
				T object = bundleFilter.filterBundle(bundle);
				if (object != null) {
					LOG.debug("Found bundle whith object required\n\tBundle: " + bundle.getSymbolicName() + "\n\t\t"
							+ object.getClass().getName() + ": " + object.toString());
					extenderHandler.onBundleArrival(bundle, object);
					bundleRegistered.add(bundle);
				}
				return bundle;
			}

			@Override
			public void removedBundle(Bundle bundle, BundleEvent event, Bundle object) {
				if (bundleRegistered.contains(bundle)) {
					LOG.debug("Removing bundle previously extended\n\tBundle: " + bundle.getSymbolicName());
					extenderHandler.onBundleDeparture(bundle);
					bundleRegistered.remove(bundle);
				}
			}

		};
		extenderHandler.starting();
		bundleTracker.open();
	}

	public void destroy() {
		bundleTracker.close();
		bundleTracker = null;
		extenderHandler.stopping();

	}

}
