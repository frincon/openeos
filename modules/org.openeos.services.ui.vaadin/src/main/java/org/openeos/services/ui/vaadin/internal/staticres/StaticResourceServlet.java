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
package org.openeos.services.ui.vaadin.internal.staticres;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceServlet extends HttpServlet {

	private static final long serialVersionUID = -1639840316758738218L;

	private static final Logger LOG = LoggerFactory.getLogger(StaticResourceServlet.class);

	private Bundle vaadinBundle;

	private BundleContext bundleContext;
	private HttpService httpService;
	private String alias;

	public StaticResourceServlet(BundleContext bundleContext, HttpService httpService, String alias) {
		this.bundleContext = bundleContext;
		this.httpService = httpService;
		this.alias = alias;
	}

	public void start() throws Exception {
		LOG.info("Registering static resource servlet to dispatch {} requests", alias);
		// find the vaadin bundle:
		for (Bundle bundle : bundleContext.getBundles()) {
			if ("com.vaadin".equals(bundle.getSymbolicName())) {
				vaadinBundle = bundle;
				break;
			}
		}
		if (vaadinBundle == null) {
			throw new RuntimeException("Can't find vaadin bundle");
		}
		httpService.registerServlet(alias, this, null, null);
	}

	public void stop() {
		httpService.unregister(alias);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		String resourcePath = alias + path;

		LOG.debug("Request to resource {}", resourcePath);

		//Trying this module
		URL u = bundleContext.getBundle().getResource(resourcePath);
		if (u == null) {
			u = vaadinBundle.getResource(resourcePath);
		}

		if (null == u) {
			LOG.warn("Resource {} not found neither in this bundle nor vaadin bundle", resourcePath);
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		InputStream in = u.openStream();
		OutputStream out = resp.getOutputStream();

		byte[] buffer = new byte[1024];
		int read = 0;
		while (-1 != (read = in.read(buffer))) {
			out.write(buffer, 0, read);
		}
	}

}
