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

import static org.openeos.test.UnoConfiguration.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.openeos.reporting.jasperreports.test.JRConfiguration.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;

import javax.inject.Inject;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.reporting.ReportingService;
import org.openeos.reporting.ReportsProvider;
import org.openeos.reporting.jasperreports.JRLoaderAndRegisterBean;
import org.openeos.reporting.jasperreports.JRPostProcessor;
import org.openeos.reporting.jasperreports.JRPreProcessor;
import org.openeos.reporting.jasperreports.JRReportsRegistry;
import org.openeos.reporting.jasperreports.test.SimpleEngineTestCase.BeanSampleClass;
import com.lowagie.text.pdf.PdfReader;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OsgiTestCase implements Constants {

	private static final Logger LOG = LoggerFactory.getLogger(OsgiTestCase.class);

	private static final String TEST_REPORT_1 = "test_report_1.jasper";
	private static final String TEST_REPORT_SRC_1 = "test_report_1.jrxml";

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 10000)
	private JRReportsRegistry jrReportsRegistry;

	@Inject
	@Filter(timeout = 10000)
	private ReportsProvider<JasperReport> provider;

	@Inject
	@Filter(timeout = 10000)
	private ReportingService reportingService;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options(
				unoMinimalConfiguration(), 
				blueprintBundles(),
				junitBundles(),
				jrDependencyBundles(),
				mavenBundle("org.ops4j.pax.tinybundles", "tinybundles").versionAsInProject(),
				mavenBundle("biz.aQute.bnd", "bndlib").versionAsInProject(),
				mavenBundle("org.mockito", "mockito-all").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4) 
		);
		// @formatter:on
	}

	@Test
	public void testServiceIsUp() {
		assertNotNull(jrReportsRegistry);
	}

	@Test
	public void testAutoRegisterJasperReports() throws Exception {
		JasperReport report1 = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResource(TEST_REPORT_1));
		JasperReport report2 = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResource(TEST_REPORT_1));

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_RANKING, 0);
		properties.put(org.openeos.reporting.jasperreports.Constants.SERVICE_REPORT_ID, TEST_REPORT_1);
		ServiceRegistration<JasperReport> register1 = bc.registerService(JasperReport.class, report1, properties);
		assertEquals(report1, provider.findReportDefinition(TEST_REPORT_1).getReport());

		properties.put(Constants.SERVICE_RANKING, 10);
		ServiceRegistration<JasperReport> register2 = bc.registerService(JasperReport.class, report2, properties);
		assertEquals(report2, provider.findReportDefinition(TEST_REPORT_1).getReport());

		register2.unregister();
		assertEquals(report1, provider.findReportDefinition(TEST_REPORT_1).getReport());

		register1.unregister();
	}

	@Test
	public void testAutoRegisterJasperReportsExecuteReport() throws Exception {
		JasperReport report1 = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResource(TEST_REPORT_1));

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_RANKING, 0);
		properties.put(org.openeos.reporting.jasperreports.Constants.SERVICE_REPORT_ID, TEST_REPORT_1);
		ServiceRegistration<JasperReport> register1 = bc.registerService(JasperReport.class, report1, properties);

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(JRParameter.REPORT_DATA_SOURCE, new JRBeanCollectionDataSource(createSampleBeans()));
		InputStream result = reportingService.generateReport(TEST_REPORT_1, "application/pdf", parameters);
		PdfReader reader = new PdfReader(result);
		assertEquals(1, reader.getNumberOfPages());
		reader.close();
		register1.unregister();
	}

	@Test
	public void testPrePostProcessors() throws Exception {
		JasperReport report1 = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResource(TEST_REPORT_1));

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_RANKING, 0);
		properties.put(org.openeos.reporting.jasperreports.Constants.SERVICE_REPORT_ID, TEST_REPORT_1);
		ServiceRegistration<JasperReport> register1 = bc.registerService(JasperReport.class, report1, properties);

		JRPreProcessor preProcessor = mock(JRPreProcessor.class);
		JRPostProcessor postProcessor = mock(JRPostProcessor.class);
		ServiceRegistration<JRPreProcessor> register2 = bc.registerService(JRPreProcessor.class, preProcessor, null);
		ServiceRegistration<JRPostProcessor> register3 = bc.registerService(JRPostProcessor.class, postProcessor, null);

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(JRParameter.REPORT_DATA_SOURCE, new JRBeanCollectionDataSource(createSampleBeans()));
		InputStream result = reportingService.generateReport(TEST_REPORT_1, "application/pdf", parameters);

		verify(preProcessor).preProcess(eq(report1), anyMap());
		verify(postProcessor).postProcess(eq(report1), anyMap(), any(JasperPrint.class));

		PdfReader reader = new PdfReader(result);
		assertEquals(1, reader.getNumberOfPages());
		reader.close();
		register3.unregister();
		register2.unregister();
		register1.unregister();

	}

	@Test
	public void testJRLoaderAndRegisterBean() throws Exception {
		JRLoaderAndRegisterBean jrLoader1 = new JRLoaderAndRegisterBean();
		jrLoader1.setId(TEST_REPORT_SRC_1);
		jrLoader1.setBundleContext(bc);
		jrLoader1.setRanking(0);
		jrLoader1.setReportBundlePath(TEST_REPORT_SRC_1);
		jrLoader1.register();
		assertNotNull(jrLoader1.getReport());
		assertEquals(jrLoader1.getReport(), provider.findReportDefinition(TEST_REPORT_SRC_1).getReport());

		JRLoaderAndRegisterBean jrLoader2 = new JRLoaderAndRegisterBean();
		jrLoader2.setId(TEST_REPORT_SRC_1);
		jrLoader2.setBundleContext(bc);
		jrLoader2.setRanking(10);
		jrLoader2.setReportBundlePath(TEST_REPORT_SRC_1);
		jrLoader2.register();
		assertNotNull(jrLoader2.getReport());
		assertEquals(jrLoader2.getReport(), provider.findReportDefinition(TEST_REPORT_SRC_1).getReport());

		jrLoader2.unregister();
		assertEquals(jrLoader1.getReport(), provider.findReportDefinition(TEST_REPORT_SRC_1).getReport());

		jrLoader1.unregister();

	}

	private Collection<?> createSampleBeans() {
		return Arrays.asList(new BeanSampleClass("Bean 1"), new BeanSampleClass("Bean 2"));
	}

}
