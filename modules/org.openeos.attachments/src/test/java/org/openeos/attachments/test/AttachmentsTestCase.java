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
package org.openeos.attachments.test;

import static org.openeos.test.UnoConfiguration.*;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.util.PathUtils;

import org.openeos.attachments.Attachment;
import org.openeos.attachments.AttachmentsService;
import org.openeos.data.sampledata.test.Constants;
import org.openeos.erp.core.dao.BusinessPartnerDAO;
import org.openeos.erp.core.model.BusinessPartner;
import org.openeos.wf.test.WorkflowConfiguration;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AttachmentsTestCase {

	private static final String PDF1_NAME = "lorem.pdf";

	private static final String ATT_TYPE_OTHERS = "OTHERS";

	@Inject
	@Filter(timeout = 10000)
	private AttachmentsService attService;

	@Inject
	@Filter(timeout = 10000)
	private BusinessPartnerDAO businessInvoiceDAO;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options(
				unoDefaultConfiguration(),
				unoDerbyInMemoryConfiguration(),
				//unoPostgresqlConfiguration(),
				unoMasterData(),
				unoSampleData(),
				junitBundles(),
				WorkflowConfiguration.wfBundles(),
				mavenBundle("org.openeos", "org.openeos.reporting.jasperreports.entity").version(getTestVersion()),
				mavenBundle("org.ops4j.pax.tinybundles", "tinybundles").versionAsInProject(),
				mavenBundle("biz.aQute.bnd", "bndlib").versionAsInProject(),
				mavenBundle("org.mockito", "mockito-all").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4) 
		);
		// @formatter:on
	}

	@Test
	public void testAttachmentPdfFromUrl() throws Exception {
		BusinessPartner bp = getSampleBusinessPartner();
		URL pdfExample = getClass().getClassLoader().getResource(PDF1_NAME);
		attService.attachFile(BusinessPartner.class, bp, ATT_TYPE_OTHERS, pdfExample);
		List<Attachment> attachmentList = attService.getAttachmentList(BusinessPartner.class, bp);
		assertEquals(1, attachmentList.size());
		Attachment attachment = attachmentList.get(0);
		assertEquals("application/pdf", attachment.getMimeType());
		assertEquals(PDF1_NAME, attachment.getName());
		assertEquals(ATT_TYPE_OTHERS, attachment.getType());
		//assertEquals(IOUtils.toByteArray(pdfExample.openStream()), IOUtils.toByteArray(attachment.openStream()));
	}

	private BusinessPartner getSampleBusinessPartner() {
		return businessInvoiceDAO.read(Constants.BUSINESS_PARTNER_1_ID);
	}
}
