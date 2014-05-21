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
package org.openeos.reporting.jasperreports.internal;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JasperReport;

import org.openeos.reporting.jasperreports.Constants;
import org.openeos.reporting.jasperreports.JRReportsRegistry;

public class JRServiceAutoRegister extends ServiceTracker<JasperReport, JasperReport> {

	private static final Logger LOG = LoggerFactory.getLogger(JRServiceAutoRegister.class);

	private JRReportsRegistry registry;

	public JRServiceAutoRegister(BundleContext bundleContext, JRReportsRegistry registry) {
		super(bundleContext, JasperReport.class, null);
		this.registry = registry;
	}

	public void bindService(JasperReport jasperReport, Map<String, ?> properties) {
		String reportId = (String) properties.get(Constants.SERVICE_REPORT_ID);
		if (reportId == null) {
			LOG.warn("Found JasperReport service bun dont register because cant find report id property");
			return;
		}
		Integer ranking = (Integer) properties.get(org.osgi.framework.Constants.SERVICE_RANKING);
		if (ranking == null) {
			ranking = 0;
		}
		registry.registerJasperReport(reportId, ranking, jasperReport);
	}

	public void unbindService(JasperReport jasperReport, Map<String, ?> properties) {
		if (jasperReport == null) {
			return;
		}
		String reportId = (String) properties.get(Constants.SERVICE_REPORT_ID);
		if (reportId == null) {
			LOG.warn("Trying to unregister Jasper Report whithout report id... ignoring");
			return;
		}
		registry.unregisterJasperReport(reportId, jasperReport);
	}

	public void init() {
		open();
	}

	public void destroy() {
		close();
	}

	@Override
	public JasperReport addingService(ServiceReference<JasperReport> reference) {
		JasperReport report = super.addingService(reference);
		bindService(report, convertProperties(reference));
		return report;
	}

	private Map<String, ?> convertProperties(ServiceReference<JasperReport> reference) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : reference.getPropertyKeys()) {
			result.put(key, reference.getProperty(key));
		}
		return result;
	}

	@Override
	public void removedService(ServiceReference<JasperReport> reference, JasperReport service) {
		unbindService(service, convertProperties(reference));
		super.removedService(reference, service);
	}

}
