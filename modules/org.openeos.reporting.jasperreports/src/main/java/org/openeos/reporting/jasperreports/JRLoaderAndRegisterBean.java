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
package org.openeos.reporting.jasperreports;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

public class JRLoaderAndRegisterBean {

	private String id;
	private int ranking;
	private BundleContext bundleContext;
	private String reportBundlePath;
	private JasperReport report;

	private ServiceRegistration<JasperReport> registration;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public String getReportBundlePath() {
		return reportBundlePath;
	}

	public void setReportBundlePath(String reportBundlePath) {
		this.reportBundlePath = reportBundlePath;
	}

	public JasperReport getReport() {
		if (report == null) {
			loadReport();
		}
		return report;
	}

	public void setReport(JasperReport report) {
		this.report = report;
	}

	public void loadReport() {
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		if (bundleContext == null || reportBundlePath == null) {
			throw new IllegalStateException();
		}
		URL url = bundleContext.getBundle().getResource(reportBundlePath);
		try (InputStream is = url.openStream()) {
			report = JasperCompileManager.compileReport(is);
		} catch (JRException | IOException e) {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
			throw new RuntimeException(e);
		}
		Thread.currentThread().setContextClassLoader(oldClassLoader);
	}

	public void register() {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_RANKING, getRanking());
		properties.put(org.openeos.reporting.jasperreports.Constants.SERVICE_REPORT_ID, getId());
		registration = bundleContext.registerService(JasperReport.class, getReport(), properties);
	}

	public void unregister() {
		registration.unregister();
	}
}
