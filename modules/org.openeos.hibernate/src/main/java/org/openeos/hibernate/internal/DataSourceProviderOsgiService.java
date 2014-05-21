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
package org.openeos.hibernate.internal;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceProviderOsgiService implements DataSourceProvider {

	private static final Logger LOG = LoggerFactory.getLogger(DataSourceProviderOsgiService.class);

	private ParentListenerSupport parentListenerSupport = new ParentListenerSupport();

	private BundleContext bundleContext;
	private Filter filter;

	private ServiceTracker<DataSource, DataSource> dataSourceServiceTracker;

	public DataSourceProviderOsgiService(BundleContext bundleContext, Filter filter) {
		this.bundleContext = bundleContext;
		this.filter = filter;
	}

	@Override
	public void addParentListener(ParentListener listener) {
		parentListenerSupport.addParentListener(listener);
	}

	@Override
	public void removeParentListener(ParentListener listener) {
		parentListenerSupport.removeParentListener(listener);
	}

	@Override
	public DataSource getDataSource() {
		LOG.debug("Retreiving data source as osgi service");
		return dataSourceServiceTracker.getService();
	}

	public void init() {
		dataSourceServiceTracker = new ServiceTracker<DataSource, DataSource>(bundleContext, filter, null) {

			@Override
			public DataSource addingService(ServiceReference<DataSource> reference) {
				LOG.debug("The data source service has been added, firing invalidate()");
				DataSource ret = super.addingService(reference);
				fireInvalidate();
				return ret;
			}

			@Override
			public void modifiedService(ServiceReference<DataSource> reference, DataSource service) {
				LOG.debug("The data source service has been modified, firing invalidate()");
				super.modifiedService(reference, service);
				fireInvalidate();
			}

			@Override
			public void removedService(ServiceReference<DataSource> reference, DataSource service) {
				LOG.debug("The data source service has been removed, firing invalidate()");
				super.removedService(reference, service);
				fireInvalidate();
			}

		};
		dataSourceServiceTracker.open();
		LOG.debug("Tracker initialized");
	}

	public void destroy() {
		dataSourceServiceTracker.close();
		dataSourceServiceTracker = null;
	}

	private void fireInvalidate() {
		LOG.debug("Invalidating parents");
		parentListenerSupport.fireInvalidate();
	}
}
