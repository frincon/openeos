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

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationProviderImpl implements ConfigurationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationProviderImpl.class);

	private ParentListenerSupport parentListenerSupport = new ParentListenerSupport();
	private Configuration configuration = new Configuration();

	@Override
	public void addParentListener(ParentListener listener) {
		parentListenerSupport.addParentListener(listener);
	}

	@Override
	public void removeParentListener(ParentListener listener) {
		parentListenerSupport.removeParentListener(listener);
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public void invalidate() {
		LOG.debug("Invalidating configuration");
		parentListenerSupport.fireInvalidate();
	}

}
