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

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.liquibase.extender.LiquibaseExtenderException;

public class LiquibaseBundleTracker extends BundleTracker<Object> {

	public static final String LIQUIBASE_DATA_SOURCE_FILTER_HEADER = "Liquibase-FilterDataSource";
	public static final String LIQUIBASE_PATH = "/OSGI-INF/liquibase/";

	private static final Logger LOG = LoggerFactory.getLogger(LiquibaseBundleTracker.class);

	private BundleContext context;
	//private ServiceHideManager serviceHideManager;

	private HashSet<String> bundlesProcessed = new HashSet<String>();

	public LiquibaseBundleTracker(BundleContext context) {
		super(context, Bundle.ACTIVE | Bundle.STARTING | Bundle.RESOLVED, null);
		this.context = context;
		// TODO Do this whith injection blueprint
		//this.serviceHideManager = new ServiceHideManager(context);
	}

	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		if (LOG.isDebugEnabled())
			LOG.debug(MessageFormat.format("Checking bundle {0} for liuibase header", bundle.getSymbolicName()));
		try {
			checkAndRunLiquibase(bundle);
		} catch (Exception e) {
			LOG.error(
					MessageFormat.format("An error occured while processing bundle {0} for liquibase extender.",
							bundle.getSymbolicName()), e);
		}
		return null;
	}

	private void checkAndRunLiquibase(Bundle bundle) throws LiquibaseExtenderException, InvalidSyntaxException {
		if (bundlesProcessed.contains(bundle.getSymbolicName())) {
			if (LOG.isDebugEnabled())
				LOG.debug(MessageFormat.format("The bundle {0} already updated,  ignored", bundle.getSymbolicName()));
			return;
		}
		String filterDataSource = getLiquibaseExtenderFilterDataSource(bundle);
		if (filterDataSource != null && !bundlesProcessed.contains(bundle.getSymbolicName())) {
			if (LOG.isDebugEnabled())
				LOG.debug(MessageFormat.format("The bundle {0} has liquibase header with filter \"{1}\"", bundle.getSymbolicName(),
						filterDataSource));
			ServiceReference<DataSource> dataSourceSR = getDataSource(filterDataSource);
			if (dataSourceSR == null) {
				if (LOG.isDebugEnabled())
					LOG.debug("The data source is not available, proceed until the datasource is available");
				registerForServiceNotAvailable(bundle, filterDataSource);
			} else {
				runLiquibase(bundle, dataSourceSR);
			}
		}
	}

	/**
	 * Execute liquibase for bundle, checking dependand bundles to run before
	 * 
	 * @param bundle
	 * @param dataSourceSR
	 * @throws LiquibaseExtenderException
	 * @throws InvalidSyntaxException
	 */
	private void runLiquibase(Bundle bundle, ServiceReference<DataSource> dataSourceSR) throws LiquibaseExtenderException,
			InvalidSyntaxException {
		//serviceHideManager.hideService(dataSourceSR);
		Collection<Bundle> dependantBundles = getDependantBundles(bundle);
		if (LOG.isDebugEnabled())
			LOG.debug("Check {0} dependencies of the bundle {1} to run liquibase before this", bundle.getSymbolicName(),
					dependantBundles.size());
		for (Bundle dependantBundle : dependantBundles) {
			if (dependantBundle != bundle)
				checkAndRunLiquibase(dependantBundle);
		}
		runLiquibase(bundle, context.getService(dataSourceSR));
		context.ungetService(dataSourceSR);
		//serviceHideManager.showService(dataSourceSR);
	}

	private void registerForServiceNotAvailable(final Bundle bundle, final String filterDataSource) throws InvalidSyntaxException {
		//serviceHideManager.hideService(filterDataSource);
		context.addServiceListener(new ServiceListener() {

			@Override
			public void serviceChanged(ServiceEvent event) {
				if (event.getType() == ServiceEvent.REGISTERED) {

					try {
						checkAndRunLiquibase(bundle);
					} catch (Exception e) {
						LOG.error(
								MessageFormat.format("An error occured while processing bundle {0} for liquibase extender.",
										bundle.getSymbolicName()), e);
					}
					context.removeServiceListener(this);
					//serviceHideManager.showService(filterDataSource);
				}
			}
		}, filterDataSource);
	}

	private synchronized void runLiquibase(Bundle bundle, DataSource dataSource) throws LiquibaseExtenderException {
		LOG.debug("Executing update database.");
		List<String> liquibasePaths = new ArrayList<String>();
		for (Enumeration<String> paths = bundle.getEntryPaths(LIQUIBASE_PATH); paths.hasMoreElements();) {
			String path = paths.nextElement();
			// TODO this is a restriction which is necessary drop off
			if (path.endsWith(".xml")) {
				liquibasePaths.add(path);
			}
		}
		Connection conn = null;
		try {
			Collections.sort(liquibasePaths, String.CASE_INSENSITIVE_ORDER);
			conn = dataSource.getConnection();
			ResourceAccessor resourceAccessor = new BundleResourceAccesor(bundle);
			Database database = createDatabase(conn);
			for (String path : liquibasePaths) {
				Liquibase liquibase = new Liquibase(path, resourceAccessor, database);
				LOG.debug("Updating database with path: {}", path);
				liquibase.update(null);
			}
		} catch (SQLException ex) {
			throw new LiquibaseExtenderException("An error occured while try to get connection from datasource", ex);
		} catch (LiquibaseException ex) {
			throw new LiquibaseExtenderException("An error occured while try run liquibase updates", ex);
		} finally {
			if (conn != null) {

				try {
					conn.close();
				} catch (SQLException ex) {
					LOG.error("An error occured while trying to close connection", ex);
					// do nothing
				}
			}
		}

		bundlesProcessed.add(bundle.getSymbolicName());
	}

	private Collection<Bundle> getDependantBundles(Bundle bundle) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		List<BundleWire> bundleWires = bundleWiring.getRequiredWires(null);
		HashSet<Bundle> bundles = new HashSet<Bundle>();
		for (BundleWire bundleWire : bundleWires) {
			bundles.add(bundleWire.getProvider().getBundle());
		}
		return bundles;
	}

	/**
	 * Get data source from service filter. Return null if datasource not found
	 * 
	 * @param filterDataSource
	 * @return
	 * @throws LiquibaseExtenderException
	 */
	private ServiceReference<DataSource> getDataSource(String filterDataSource) throws LiquibaseExtenderException {
		try {
			Collection<ServiceReference<DataSource>> dataSources = context.getServiceReferences(DataSource.class, filterDataSource);
			if (dataSources.size() == 0) {
				return null;
			} else if (dataSources.size() > 1) {
				// TODO Get first data source with this filter, WARNING
				LOG.warn("Found more than one datasource for the filter: {0}", filterDataSource);
			}
			return dataSources.iterator().next();
		} catch (InvalidSyntaxException e) {
			throw new LiquibaseExtenderException(MessageFormat.format(
					"Error occured while trying to get data source with filter {0}: {1}", filterDataSource, e.getMessage()), e);

		}
	}

	private String getLiquibaseExtenderFilterDataSource(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders();
		String filterDataSource = headers.get(LIQUIBASE_DATA_SOURCE_FILTER_HEADER);
		return filterDataSource;
	}

	protected Database createDatabase(Connection c) throws DatabaseException {
		Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
		return database;
	}

}
