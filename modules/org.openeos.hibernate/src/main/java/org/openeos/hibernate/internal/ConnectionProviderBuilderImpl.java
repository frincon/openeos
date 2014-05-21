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

import java.util.Collections;

import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionProviderBuilderImpl implements ConnectionProviderBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectionProviderBuilderImpl.class);

	private DataSourceProvider dataSourceProvider;

	@Override
	public ConnectionProviderBuilder with(DataSourceProvider dataSourceProvider) {
		this.dataSourceProvider = dataSourceProvider;
		return this;
	}

	@Override
	public ConnectionProvider buildConnectionProvider() {
		if (this.dataSourceProvider == null) {
			throw new IllegalStateException();
		}
		LOG.debug("Building new Connection Provider using datasourceProvider");
		DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
		connectionProvider.setDataSource(dataSourceProvider.getDataSource());
		connectionProvider.configure(Collections.emptyMap());
		return connectionProvider;
	}
}
