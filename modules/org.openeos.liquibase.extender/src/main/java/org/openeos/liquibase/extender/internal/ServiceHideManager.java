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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook.ListenerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.liquibase.extender.LiquibaseExtenderException;

public class ServiceHideManager implements EventListenerHook, FindHook {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceHideManager.class);

	private BundleContext context;

	private HashMap<ServiceReference<?>, List<Bundle>> serviceReferenceMap = new HashMap<ServiceReference<?>, List<Bundle>>();
	private HashMap<ServiceReference<?>, Integer> serviceReferenceMapCount = new HashMap<ServiceReference<?>, Integer>();

	private HashMap<String, Integer> hiddenServices = new HashMap<String, Integer>();

	private ServiceRegistration<?> reg;

	public ServiceHideManager(BundleContext context) {
		this.context = context;
		registerHideHook();
	}

	public void hideService(ServiceReference<?> dataSourceSR) throws LiquibaseExtenderException {
		if (LOG.isDebugEnabled())
			LOG.debug(MessageFormat.format("Trtying to hide service {0}", dataSourceSR));
		if (!isAlreadyHide(dataSourceSR)) {
			List<Bundle> bundles = stopAllBundlesDependant(dataSourceSR);
			serviceReferenceMap.put(dataSourceSR, bundles);
			serviceReferenceMapCount.put(dataSourceSR, 1);
		} else {
			if (LOG.isDebugEnabled())
				LOG.debug(MessageFormat.format("The service {1} already hided, incrementing counter", dataSourceSR));
			serviceReferenceMapCount.put(dataSourceSR, serviceReferenceMapCount.get(dataSourceSR) + 1);
		}
	}

	public void showService(ServiceReference<?> dataSourceSR) throws LiquibaseExtenderException {
		int count = serviceReferenceMapCount.get(dataSourceSR);
		if (count == 1) {
			List<Bundle> bundles = serviceReferenceMap.get(dataSourceSR);
			serviceReferenceMap.remove(dataSourceSR);
			serviceReferenceMapCount.remove(dataSourceSR);

			//First stop/start 
			boolean stopStart = true;
			try {
				for (String filter : hiddenServices.keySet()) {
					Collection<ServiceReference<DataSource>> refs = context.getServiceReferences(DataSource.class, filter);
					if (refs.contains(dataSourceSR))
						stopStart = false;
				}
				if (stopStart) {
					Bundle bundle = dataSourceSR.getBundle();
					bundle.stop(Bundle.STOP_TRANSIENT);
					bundle.start(Bundle.START_TRANSIENT);
				}
			} catch (Exception ex) {
				LOG.error("Error occured while tryin to stop/start bundle of the datasource", ex);
			}

			for (Bundle bundle : bundles) {
				try {
					bundle.start(Bundle.START_TRANSIENT);
				} catch (BundleException e) {
					throw new LiquibaseExtenderException("An error occured while start bundle", e);
				}
			}
		} else {
			serviceReferenceMapCount.put(dataSourceSR, count - 1);
		}

	}

	private void registerHideHook() {
		if (reg == null) {
			reg = context.registerService(new String[] { FindHook.class.getName(), EventListenerHook.class.getName() }, this, null);
		}
	}

	private List<Bundle> stopAllBundlesDependant(ServiceReference<?> dataSourceSR) throws LiquibaseExtenderException {
		LOG.debug("Trying to stop all dependant bundles of service {}", dataSourceSR.toString());
		List<Bundle> stoppeds = new ArrayList<Bundle>();
		Bundle[] bundles = dataSourceSR.getUsingBundles();
		if (bundles == null) {
			return stoppeds;
		}
		for (int i = 0; i < bundles.length; i++) {
			try {
				if (bundles[i].getState() == Bundle.ACTIVE || bundles[i].getState() == Bundle.STARTING) {
					LOG.debug("Trying to stop bundle:_{}", bundles[i].getSymbolicName());
					bundles[i].stop(Bundle.STOP_TRANSIENT);
					stoppeds.add(bundles[i]);
				}
			} catch (BundleException e) {
				LOG.error("An error occured while stoping bundle for hide service", e);
				for (Bundle bundle : stoppeds) {
					try {
						bundle.start(Bundle.START_TRANSIENT);
					} catch (BundleException e2) {
						LOG.error("An error occured while starting bundle due a previous error", e2);
					}
				}
				throw new LiquibaseExtenderException("An error occured while stoping bundle for hide service", e);
			}
		}
		return stoppeds;
	}

	private boolean isAlreadyHide(ServiceReference<?> dataSourceSR) {
		return serviceReferenceMap.containsKey(dataSourceSR);
	}

	@Override
	public void find(BundleContext context, String name, String filter, boolean allServices,
			Collection<ServiceReference<?>> references) {
		//Not hide anything for this bundle
		if (context.equals(this.context))
			return;
		
		LOG.debug("Hide services by find call from bundle {}", context.getBundle().getSymbolicName());

		//Hide references hides
		references.removeAll(serviceReferenceMap.keySet());

		//Hide references than not getting yet
		for (String searchFilter : hiddenServices.keySet()) {
			try {
				Collection<ServiceReference<DataSource>> referencesToRemove = this.context.getServiceReferences(DataSource.class,
						searchFilter);
				references.removeAll(referencesToRemove);
			} catch (InvalidSyntaxException ex) {
				LOG.error("Error while retrieving filters to hide", ex);
			}
		}
	}

	@Override
	public void event(ServiceEvent event, Map<BundleContext, Collection<ListenerInfo>> listeners) {
		//Only filter registered services
		if (event.getType() != ServiceEvent.REGISTERED) {
			return;
		}
		ArrayList<ServiceReference<?>> serviceReferences = new ArrayList<ServiceReference<?>>();

		serviceReferences.addAll(serviceReferenceMap.keySet());

		for (String searchFilter : hiddenServices.keySet()) {
			try {
				serviceReferences.addAll(this.context.getServiceReferences(DataSource.class, searchFilter));
			} catch (InvalidSyntaxException ex) {
				LOG.error("Error while retrieving filters to hide", ex);
			}
		}

		if (serviceReferences.contains(event.getServiceReference())) {
			//Remove all bundles except this
			ArrayList<BundleContext> bundles = new ArrayList<BundleContext>();
			for (BundleContext bundle : listeners.keySet()) {
				if (!bundle.equals(this.context)) {
					bundles.add(bundle);
				}
			}
			for (BundleContext bundle : bundles) {
				listeners.remove(bundle);
			}
		}
	}

	public void hideService(String filterDataSource) {
		if (hiddenServices.containsKey(filterDataSource)) {
			hiddenServices.put(filterDataSource, hiddenServices.get(filterDataSource) + 1);
		} else {
			hiddenServices.put(filterDataSource, 1);
		}

	}

	public void showService(String filterDataSource) {
		int contador = hiddenServices.get(filterDataSource);
		contador--;
		if (LOG.isDebugEnabled())
			LOG.debug(MessageFormat.format("Showing service counter={0}", contador));
		if (contador == 0) {
			hiddenServices.remove(filterDataSource);
			try {
				Collection<ServiceReference<DataSource>> servRefs = context
						.getServiceReferences(DataSource.class, filterDataSource);
				for (ServiceReference<DataSource> servRef : servRefs) {
					if (!serviceReferenceMap.containsKey(servRefs)) {
						//Stop and start bundle for register any
						Bundle bundle = servRef.getBundle();
						bundle.stop(Bundle.STOP_TRANSIENT);
						bundle.start(Bundle.START_TRANSIENT);
					}
				}
			} catch (Exception ex) {
				LOG.error("An error occured while trying showing service", ex);
			}
		} else {
			hiddenServices.put(filterDataSource, contador);
		}
	}

}
