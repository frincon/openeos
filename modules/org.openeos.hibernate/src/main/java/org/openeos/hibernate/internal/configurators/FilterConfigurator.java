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
package org.openeos.hibernate.internal.configurators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.hibernate.BasicFilterDefinition;
import org.openeos.hibernate.FilterProvider;
import org.openeos.hibernate.internal.ConfigurationProvider;

public class FilterConfigurator implements Configurator {

	private static final Logger LOG = LoggerFactory.getLogger(FilterConfigurator.class);

	private BundleContext bundleContext;

	private ServiceTracker<FilterProvider, FilterProvider> tracker;

	private Map<String, FilterProvider> filterProviderMap = new HashMap<String, FilterProvider>();

	public FilterConfigurator(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	@Override
	public void init(final ConfigurationProvider configurationProvider) {
		LOG.debug("Initializing filter configuration");
		tracker = new ServiceTracker<FilterProvider, FilterProvider>(bundleContext, FilterProvider.class, null) {

			@Override
			public FilterProvider addingService(ServiceReference<FilterProvider> reference) {
				LOG.debug("Filter provider added as a service");
				FilterProvider provider = super.addingService(reference);
				addFilterDefinitions(provider, configurationProvider);
				return provider;
			}

			@Override
			public void removedService(ServiceReference<FilterProvider> reference, FilterProvider service) {
				LOG.debug("Filter provider removed");
				removeFilterDefinitions(service, configurationProvider);
				super.removedService(reference, service);
			}

		};
		tracker.open();
		bundleContext.addBundleListener(new SynchronousBundleListener() {

			@Override
			public void bundleChanged(BundleEvent event) {
				if (event.getBundle().equals(bundleContext.getBundle()) && event.getType() == BundleEvent.STOPPING) {
					tracker.close();
					tracker = null;
				}
			}
		});
	}

	private void removeFilterDefinitions(FilterProvider service, ConfigurationProvider configurationProvider) {
		throw new UnsupportedOperationException();
	}

	private void addFilterDefinitions(FilterProvider provider, ConfigurationProvider configurationProvider) {
		Configuration conf = configurationProvider.getConfiguration();
		conf.buildMappings();
		Map<?, ?> filterDefinitions = conf.getFilterDefinitions();
		TypeResolver resolver = conf.getTypeResolver();
		for (BasicFilterDefinition filterDef : provider.getFilterDefinitions()) {
			Map<String, Type> paramMapConverted = new HashMap<String, Type>();
			for (Entry<String, String> paramEntry : filterDef.getParameterTypeMap().entrySet()) {
				Type type = resolver.heuristicType(paramEntry.getValue());
				paramMapConverted.put(paramEntry.getKey(), type);
			}
			FilterDefinition definition = new FilterDefinition(filterDef.getName(), filterDef.getDefaultCondition(),
					paramMapConverted);
			LOG.debug("Registering new filter definition with name '{}'", definition.getFilterName());
			if (filterDefinitions.containsKey(definition.getFilterName())) {
				LOG.warn("The configuration already has filter definition with name '{}',  overwriting.",
						definition.getFilterName());
			}
			conf.addFilterDefinition(definition);
			filterProviderMap.put(definition.getFilterName(), provider);
			Iterator<PersistentClass> classIterator = conf.getClassMappings();
			while (classIterator.hasNext()) {
				PersistentClass persistentClass = classIterator.next();
				provider.addFilterToClassIfNecesary(persistentClass, filterDef);
			}
		}
		configurationProvider.invalidate();
	}
}
