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
package org.openeos.wf.jbpm.internal;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.StatefulKnowledgeSession;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkItemHandlerAutoRegister extends ServiceTracker<WorkItemHandler, WorkItemHandler> {

	private static final Logger LOG = LoggerFactory.getLogger(WorkItemHandlerAutoRegister.class);

	private Map<String, ServiceReference<WorkItemHandler>> mapActualRegister = new HashMap<String, ServiceReference<WorkItemHandler>>();
	private Map<String, SortedSet<ServiceReference<WorkItemHandler>>> mapNotRegistered = new HashMap<String, SortedSet<ServiceReference<WorkItemHandler>>>();

	private StatefulKnowledgeSession ksession;
	private BundleContext bundleContext;

	private Comparator<ServiceReference<?>> serviceRankingComparator = new Comparator<ServiceReference<?>>() {

		@Override
		public int compare(ServiceReference<?> o1, ServiceReference<?> o2) {
			int ranking1 = getRanking(o1);
			int ranking2 = getRanking(o2);
			int result = Integer.compare(ranking1, ranking2);
			if (result == 0) {
				result = ((Long) o1.getProperty(Constants.SERVICE_ID)).compareTo((Long) o2
						.getProperty(Constants.SERVICE_ID));
			}
			return result;
		}
	};

	public WorkItemHandlerAutoRegister(BundleContext bundleContext, StatefulKnowledgeSession ksession) {
		super(bundleContext, WorkItemHandler.class, null);
		this.bundleContext = bundleContext;
		this.ksession = ksession;
	}

	private int getRanking(ServiceReference<?> reference) {
		Integer ranking = (Integer) reference.getProperty(Constants.SERVICE_RANKING);
		if (ranking == null) {
			return Integer.MIN_VALUE;
		} else {
			return ranking;
		}
	}

	@Override
	public synchronized WorkItemHandler addingService(ServiceReference<WorkItemHandler> reference) {
		WorkItemHandler handler = super.addingService(reference);
		int ranking = getRanking(reference);
		String name = (String) reference.getProperty(org.openeos.wf.jbpm.Constants.SERVICE_WORKITEM_HANDLER_NAME);
		if (name == null) {
			LOG.debug("Found WorkItemHandler service but hasn't property {}. Ignoring it.",
					org.openeos.wf.jbpm.Constants.SERVICE_WORKITEM_HANDLER_NAME);
			return handler;
		}
		LOG.debug("New work item handler found, name: '{}', ranking: {}", name, ranking);
		if (mapActualRegister.containsKey(name)) {
			int actualRanking = getRanking(mapActualRegister.get(name));
			if (actualRanking < ranking) {
				LOG.debug("Actual service has ranking {}, replacing it with new handler", actualRanking);
				addNotRegisterService(name, mapActualRegister.get(name));
				registerActualHandler(name, reference, handler);
			} else {
				LOG.debug("Actual service has ranking {}, keep it", actualRanking);
				addNotRegisterService(name, reference);
			}

		} else {
			LOG.debug("No previous work item found, register it");
			registerActualHandler(name, reference, handler);
		}
		return handler;
	}

	private void registerActualHandler(String name, ServiceReference<WorkItemHandler> reference, WorkItemHandler handler) {
		mapActualRegister.put(name, reference);
		ksession.getWorkItemManager().registerWorkItemHandler(name, handler);
	}

	private void addNotRegisterService(String name, ServiceReference<WorkItemHandler> serviceReference) {

		SortedSet<ServiceReference<WorkItemHandler>> setNotRegistered = getNotRegisteredSet(name);
		setNotRegistered.add(serviceReference);
	}

	private SortedSet<ServiceReference<WorkItemHandler>> getNotRegisteredSet(String name) {
		SortedSet<ServiceReference<WorkItemHandler>> setNotRegistered = mapNotRegistered.get(name);
		if (setNotRegistered == null) {
			setNotRegistered = new TreeSet<ServiceReference<WorkItemHandler>>(serviceRankingComparator);
			mapNotRegistered.put(name, setNotRegistered);
		}
		return setNotRegistered;
	}

	@Override
	public synchronized void removedService(ServiceReference<WorkItemHandler> reference, WorkItemHandler service) {
		String name = (String) reference.getProperty(org.openeos.wf.jbpm.Constants.SERVICE_WORKITEM_HANDLER_NAME);
		if (name != null) {
			if (mapActualRegister.get(name).equals(reference)) {
				LOG.debug("Unregistered actual handler for '{}'", name);
				if (getNotRegisteredSet(name).size() == 0) {
					LOG.debug("Not found substitute for handler, registerin null handler for '{}'", name);
					ksession.getWorkItemManager().registerWorkItemHandler(name, null);
					mapActualRegister.remove(name);
				} else {
					ServiceReference<WorkItemHandler> newReference = getNotRegisteredSet(name).iterator().next();
					getNotRegisteredSet(name).remove(newReference);
					registerActualHandler(name, newReference, bundleContext.getService(newReference));
				}
			} else {
				LOG.debug("Unregistered not used handler for '{}'", name);
				getNotRegisteredSet(name).remove(reference);
			}
		}

		super.removedService(reference, service);
	}
}
