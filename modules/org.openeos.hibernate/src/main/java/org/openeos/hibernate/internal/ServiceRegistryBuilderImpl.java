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

import org.hibernate.service.BootstrapServiceRegistry;
import org.hibernate.service.BootstrapServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.hibernate.SessionObserver;

public class ServiceRegistryBuilderImpl implements ServiceRegistryBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistryBuilderImpl.class);

	private ConnectionProviderBuilder connectionProviderBuilder;
	private ConfigurationProvider configurationPovider;
	private SessionObserver sessionObserver;

	private BootstrapServiceRegistry bootstrapServiceRegistry;

	@Override
	public ServiceRegistryBuilder with(ConnectionProviderBuilder connectionProviderBuilder) {
		this.connectionProviderBuilder = connectionProviderBuilder;
		return this;
	}

	@Override
	public ServiceRegistryBuilder with(ConfigurationProvider configurationProvider) {
		this.configurationPovider = configurationProvider;
		return this;
	}

	@Override
	public ServiceRegistryBuilder with(SessionObserver sessionObserver) {
		this.sessionObserver = sessionObserver;
		return this;
	}

	@Override
	public ServiceRegistry buildServiceRegistry() {
		if (connectionProviderBuilder == null || configurationPovider == null) {
			throw new IllegalArgumentException();
		}
		LOG.debug("Creating service registry");
		BootstrapServiceRegistry bootstrapServiceRegistry = buildBootstrapServiceRegistry();
		org.hibernate.service.ServiceRegistryBuilder builder = new org.hibernate.service.ServiceRegistryBuilder(
				bootstrapServiceRegistry).addService(ConnectionProvider.class, connectionProviderBuilder.buildConnectionProvider());
		if (sessionObserver != null) {
			builder.addService(SessionObserver.class, sessionObserver);
		}
		builder.applySettings(configurationPovider.getConfiguration().getProperties());
		return builder.buildServiceRegistry();

	}

	private BootstrapServiceRegistry buildBootstrapServiceRegistry() {
		if (bootstrapServiceRegistry == null) {
			bootstrapServiceRegistry = new BootstrapServiceRegistryBuilder().with(this.getClass().getClassLoader()).build();
		}
		return bootstrapServiceRegistry;
	}

}
