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
package org.openeos.reporting.jasperreports.entity.test;

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
import java.util.List;

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

import org.openeos.erp.core.dao.BusinessPartnerDAO;
import org.openeos.erp.core.model.BusinessPartner;
import org.openeos.reporting.ReportingService;
import org.openeos.reporting.ReportsProvider;
import org.openeos.reporting.entity.EntityReportingService;
import org.openeos.reporting.jasperreports.JRPostProcessor;
import org.openeos.reporting.jasperreports.JRPreProcessor;
import org.openeos.reporting.jasperreports.JRReportsRegistry;
import org.openeos.reporting.jasperreports.entity.JREntityLoaderAndRegisterBean;
import org.openeos.reporting.jasperreports.test.SimpleEngineTestCase.BeanSampleClass;
import org.openeos.services.document.EntityDocument;
import org.openeos.services.document.EntityDocumentService;
import org.openeos.wf.test.WorkflowConfiguration;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OsgiTestCase implements Constants {

	private static final Logger LOG = LoggerFactory.getLogger(OsgiTestCase.class);

	private static final String TEST_REPORT_BEAN = "test_report_bean.jasper";
	private static final String TEST_REPORT_SQL = "test_report_sql.jasper";

	private static final String TEST_REPORT_BEAN_SRC = "test_report_bean.jrxml";
	private static final String TEST_REPORT_BEAN2_SRC = "test_report_bean2.jrxml";

	private static final String TEST_REPORT_ID = "TEST_REPORT_ID";

	private static final String TEST_DOCUMENT_TYPE_ID = "TEST_DOCUMENT_TYPE_ID";

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 10000)
	private ReportingService reportingService;

	@Inject
	@Filter(timeout = 10000)
	private BusinessPartnerDAO bpDAO;

	@Inject
	@Filter(timeout = 10000)
	private EntityReportingService entityReportingService;

	@Inject
	@Filter(timeout = 10000)
	private EntityDocumentService entityDocumentService;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options(
				unoDefaultConfiguration(),
				unoDerbyInMemoryConfiguration(),
				unoMasterData(),
				unoSampleData(),
				junitBundles(),
				jrBundles(),
				WorkflowConfiguration.wfBundles(),
				mavenBundle("org.openeos", "org.openeos.reporting.entity").versionAsInProject(),
				mavenBundle("org.openeos", "org.openeos.attachments").versionAsInProject(),
				mavenBundle("org.openeos", "org.openeos.services.document").versionAsInProject(),
				mavenBundle("org.ops4j.pax.tinybundles", "tinybundles").versionAsInProject(),
				mavenBundle("biz.aQute.bnd", "bndlib").versionAsInProject(),
				mavenBundle("org.mockito", "mockito-all").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4) 
		);
		// @formatter:on
	}

	@Test
	public void testEntityReportBean() throws Exception {
		testReport(TEST_REPORT_BEAN);
	}

	@Test
	public void testEntityReportSql() throws Exception {
		testReport(TEST_REPORT_SQL);
	}

	@Test
	public void testEntityReportingService() throws Exception {
		JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResource(TEST_REPORT_BEAN));

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_RANKING, 0);
		properties.put(org.openeos.reporting.jasperreports.Constants.SERVICE_REPORT_ID, TEST_REPORT_ID);
		ServiceRegistration<JasperReport> register1 = bc.registerService(JasperReport.class, report, properties);

		InputStream result = entityReportingService.generateReport(TEST_REPORT_ID, BusinessPartner.class, bpDAO.findAll());

		PdfReader reader = new PdfReader(result);
		assertEquals(1, reader.getNumberOfPages());

		assertTrue(findPdfString(reader, "testField"));

		reader.close();
		register1.unregister();

	}

	@Test
	public void testLoadAndRegister() throws Exception {
		JREntityLoaderAndRegisterBean loader = new JREntityLoaderAndRegisterBean();
		loader.setBundleContext(bc);
		loader.setDocumentMimeType("application/pdf");
		loader.setDocumentNameTemplate("\"TEST-\" + entity.name + \".pdf\"");
		loader.setDocumentType(TEST_DOCUMENT_TYPE_ID);
		loader.setEntityClass(BusinessPartner.class);
		loader.setId(TEST_REPORT_ID);
		loader.setRanking(0);
		loader.setReportBundlePath(TEST_REPORT_BEAN_SRC);
		loader.register();

		String expectedName = "TEST-" + org.openeos.data.sampledata.test.Constants.BUSSINES_PARTNER_1_NAME + ".pdf";

		BusinessPartner bp = bpDAO.read(org.openeos.data.sampledata.test.Constants.BUSINESS_PARTNER_1_ID);

		String name = entityDocumentService.generateDocument(BusinessPartner.class, bp, TEST_DOCUMENT_TYPE_ID);
		assertEquals(expectedName, name);
		List<EntityDocument> entityDocList = entityDocumentService.findDocuments(BusinessPartner.class, bp);
		assertEquals(1, entityDocList.size());

		EntityDocument doc = entityDocList.get(0);
		assertEquals(TEST_DOCUMENT_TYPE_ID, doc.getDocumentId());
		assertEquals("application/pdf", doc.getMimeType());
		assertEquals(expectedName, doc.getName());

		PdfReader reader = new PdfReader(doc.getContent());
		assertEquals(1, reader.getNumberOfPages());

		assertTrue(findPdfString(reader, "testField"));

		reader.close();
		loader.unregister();
		
	}

	private void testReport(String reportName) throws Exception {
		JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResource(reportName));

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_RANKING, 0);
		properties.put(org.openeos.reporting.jasperreports.Constants.SERVICE_REPORT_ID, TEST_REPORT_ID);
		ServiceRegistration<JasperReport> register1 = bc.registerService(JasperReport.class, report, properties);

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(org.openeos.reporting.jasperreports.entity.Constants.PARAMETER_IS_ENTITY_REPORT, true);
		parameters.put(org.openeos.reporting.jasperreports.entity.Constants.PARAMETER_ENTITY_COLLECTION, bpDAO.findAll());
		parameters.put(org.openeos.reporting.jasperreports.entity.Constants.PARAMETER_ENTITY_CLASS, BusinessPartner.class);
		InputStream result = reportingService.generateReport(TEST_REPORT_ID, "application/pdf", parameters);

		PdfReader reader = new PdfReader(result);
		assertEquals(1, reader.getNumberOfPages());

		assertTrue(findPdfString(reader, "testField"));

		reader.close();
		register1.unregister();

	}

	public boolean findPdfString(PdfReader reader, String string) throws Exception {
		byte[] streamBytes = reader.getPageContent(1);
		LOG.debug("Finding string {} in pdf", string);
		PRTokeniser tokenizer = new PRTokeniser(new RandomAccessFileOrArray(streamBytes));
		while (tokenizer.nextToken()) {
			if (tokenizer.getTokenType() == PRTokeniser.TK_STRING) {
				LOG.debug("Found String in pdf: " + tokenizer.getStringValue());
				if (tokenizer.getStringValue().equals(string)) {
					return true;
				}
			}
		}
		return false;

	}

}
