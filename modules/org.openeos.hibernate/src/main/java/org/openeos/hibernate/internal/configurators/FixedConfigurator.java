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

import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.hibernate.internal.ConfigurationProvider;

public class FixedConfigurator implements Configurator {

	private static final Logger LOG = LoggerFactory.getLogger(FixedConfigurator.class);

	@Override
	public void init(ConfigurationProvider configurationProvider) {
		LOG.debug("Initializing static fixed configuration");
		Configuration conf = configurationProvider.getConfiguration();
		Properties props = new Properties();
		props.put("hibernate.bytecode.provider", "null");
		props.put("hibernate.current_session_context_class", "org.springframework.orm.hibernate4.SpringSessionContext");
		props.put("hibernate.jdbc.batch_size", "30");
		props.put("hibernate.enable_lazy_load_no_trans", "true");
		props.put("javax.persistence.validation.mode", "none");
		props.put("show_sql", "true");
		conf.addProperties(props);
		configurationProvider.invalidate();
	}

}
