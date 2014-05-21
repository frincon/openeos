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

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.jfree.util.Log;

import org.openeos.reporting.ReportingEngine;
import org.openeos.reporting.ReportingException;

public class JasperReportsEngine implements ReportingEngine<JasperReport> {

	private static final String THREAD_NAME = "Reporting Jasper Report Exporter";

	public static final String MIME_PDF = "application/pdf";
	public static final String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String MIME_HTML = "text/html";
	public static final String MIME_XLS = "application/vnd.ms-excel";

	private static final String DEFAULT_MIME_TYPE = MIME_PDF;

	private Map<String, ExporterFactory> exporterFactoryMap;
	private JasperReportsEngineLifecycle jasperReportsEngineLifecycle;

	public JasperReportsEngine(Map<String, ExporterFactory> exporterFactoryMap,
			JasperReportsEngineLifecycle jasperReportsEngineLifecycle) {
		this.exporterFactoryMap = exporterFactoryMap;
		this.jasperReportsEngineLifecycle = jasperReportsEngineLifecycle;
	}

	@Override
	public Class<JasperReport> getReportClass() {
		return JasperReport.class;
	}

	@Override
	public InputStream generateReport(JasperReport report, Map<String, Object> parameters, String mimeType)
			throws ReportingException {
		if (mimeType == null) {
			mimeType = DEFAULT_MIME_TYPE;
		}
		if (!exporterFactoryMap.keySet().contains(mimeType)) {
			throw new ReportingException(String.format("This report engine can't make reports of mime type provided: '%s'",
					mimeType));
		}
		JasperPrint jp = null;
		boolean postCalled = false;
		try {
			jasperReportsEngineLifecycle.callPreProcessors(report, parameters);
			jp = JasperFillManager.fillReport(report, parameters);
			jasperReportsEngineLifecycle.callPostProcessors(report, parameters, jp);
			postCalled = true;
			return exportJasperPrint(jp, parameters, mimeType);
		} catch (JRException | IOException e) {
			if (!postCalled) {
				jasperReportsEngineLifecycle.callPostProcessors(report, parameters, jp);
			}
			throw new ReportingException("An error ocurred while trying to fill report", e);
		}
	}

	private InputStream exportJasperPrint(JasperPrint jp, Map<String, Object> parameters, String mimeType) throws IOException {
		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(pis);
		JRExporter exporter = createExporter(mimeType, parameters);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, pos);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		createExporterThread(exporter, pos);
		return pis;
	}

	private void createExporterThread(final JRExporter exporter, final PipedOutputStream pos) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					exporter.exportReport();
					pos.close();
				} catch (JRException | IOException e) {
					Log.error("An error has occured while exporting report", e);
				}
			}
		}, THREAD_NAME);
		thread.start();
	}

	private JRExporter createExporter(String mimeType, Map<String, Object> parameters) {
		ExporterFactory factory = exporterFactoryMap.get(mimeType);
		if (factory == null) {
			throw new UnsupportedOperationException();
		}
		return factory.createExporter(parameters);
	}

}
