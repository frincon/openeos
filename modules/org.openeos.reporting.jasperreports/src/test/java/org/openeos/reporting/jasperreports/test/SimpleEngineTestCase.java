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
package org.openeos.reporting.jasperreports.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.junit.Test;

import org.openeos.reporting.jasperreports.internal.DefaultExporterFactory;
import org.openeos.reporting.jasperreports.internal.ExporterFactory;
import org.openeos.reporting.jasperreports.internal.JRReportsProvider;
import org.openeos.reporting.jasperreports.internal.JasperReportsEngine;
import org.openeos.reporting.jasperreports.internal.JasperReportsEngineLifecycle;
import com.lowagie.text.pdf.PdfReader;

public class SimpleEngineTestCase {

	private static final String TEST_REPORT_1 = "test_report_1.jasper";

	public static class BeanSampleClass {
		private String testField;

		public BeanSampleClass(String testField) {
			this.testField = testField;
		}

		public String getTestField() {
			return testField;
		}

		public void setTestField(String testField) {
			this.testField = testField;
		}

	}

	private Map<String, ExporterFactory> createMapExporterFactory() {
		Map<String, ExporterFactory> exporterFactoryMap = new HashMap<String, ExporterFactory>();
		exporterFactoryMap.put(JasperReportsEngine.MIME_PDF, new DefaultExporterFactory(JRPdfExporter.class));
		return exporterFactoryMap;
	}

	@Test
	public void testGenerateReportPDF() throws Exception {
		JasperReportsEngineLifecycle lifeCycle = mock(JasperReportsEngineLifecycle.class);
		JasperReportsEngine engine = new JasperReportsEngine(createMapExporterFactory(), lifeCycle);
		JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResource(TEST_REPORT_1));
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(JRParameter.REPORT_DATA_SOURCE, new JRBeanCollectionDataSource(createSampleBeans()));
		InputStream result = engine.generateReport(report, parameters, JasperReportsEngine.MIME_PDF);
		verify(lifeCycle).callPreProcessors(eq(report), anyMap());
		verify(lifeCycle).callPostProcessors(eq(report), anyMap(), any(JasperPrint.class));
		PdfReader reader = new PdfReader(result);
		assertEquals(1, reader.getNumberOfPages());
		reader.close();
	}

	@Test
	public void testReportProviderRegistry() throws Exception {
		JasperReport report1 = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResource(TEST_REPORT_1));
		JasperReport report2 = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResource(TEST_REPORT_1));

		assertFalse(report1.equals(report2));

		JRReportsProvider provider = new JRReportsProvider();
		provider.registerJasperReport(TEST_REPORT_1, 0, report1);
		assertEquals(report1, provider.findReportDefinition(TEST_REPORT_1).getReport());

		provider.registerJasperReport(TEST_REPORT_1, 10, report2);
		assertEquals(report2, provider.findReportDefinition(TEST_REPORT_1).getReport());

		provider.unregisterJasperReport(TEST_REPORT_1, report2);
		assertEquals(report1, provider.findReportDefinition(TEST_REPORT_1).getReport());
	}

	private Collection<?> createSampleBeans() {
		return Arrays.asList(new BeanSampleClass("Bean 1"), new BeanSampleClass("Bean 2"));
	}

}
