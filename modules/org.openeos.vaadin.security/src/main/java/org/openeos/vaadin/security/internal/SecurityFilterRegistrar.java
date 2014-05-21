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
package org.openeos.vaadin.security.internal;

import javax.servlet.Filter;

import org.eclipse.equinox.http.servlet.ExtendedHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.vaadin.main.IUnoVaadinApplication;

public class SecurityFilterRegistrar {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityFilterRegistrar.class);

	private Filter securityFilter;
	private ExtendedHttpService httpService;
	private boolean registered = false;

	public void setHttpService(ExtendedHttpService httpService) {
		this.httpService = httpService;
	}

	public void setSecurityFilter(Filter securityFilter) {
		this.securityFilter = securityFilter;
	}

	public void register() {
		if (httpService == null || securityFilter == null) {
			throw new IllegalStateException();
		}
		try {
			httpService.registerFilter("/" + IUnoVaadinApplication.VAADIN_APPLICATION_NAME, securityFilter, null, null);
			//httpService.registerFilter("/" + IUnoVaadinApplication.VAADIN_APPLICATION_NAME + "/*", securityFilter, null, null);
			registered = true;
		} catch (Exception e) {
			LOG.error("Error has been thrown while trying to register security filter to vaadin application", e);
			throw new RuntimeException(e);
		}
	}

	public void unregister() {
		if (registered) {
			httpService.unregisterFilter(securityFilter);
		}
	}
}
