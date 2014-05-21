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
package org.openeos.liquibase.extender.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	public static final String LIQUIBASE_PATH = "/OSGI-INF/liquibase/";

	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	private LiquibaseBundleTracker tracker;

	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		LOG.debug("Starting liquibase extender activator");
		registerBundleTracker(bundleContext);
	}

	private void registerBundleTracker(BundleContext bundleContext) {
		tracker = new LiquibaseBundleTracker(bundleContext);
		LOG.debug("Open Liquibase Bundle Tracker");
		tracker.open();
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		tracker.close();
	}

}
