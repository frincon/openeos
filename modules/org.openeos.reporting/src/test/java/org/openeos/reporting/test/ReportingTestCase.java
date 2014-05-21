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
package org.openeos.reporting.test;

import static org.openeos.test.UnoConfiguration.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
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

import org.openeos.reporting.BaseReportDefinition;
import org.openeos.reporting.ReportingEngine;
import org.openeos.reporting.ReportingService;
import org.openeos.reporting.ReportsProvider;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ReportingTestCase implements Constants {

	private static final Logger LOG = LoggerFactory.getLogger(ReportingTestCase.class);

	private static final String REPORT1_ID = "REPORT1_ID";
	private static final String REPORT1_CONTENT = "REPORT1_CONTENT";
	private static final String REPORT1_RESULT = "REPORT1_RESULT";

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 10000)
	private ReportingService reportingService;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options(
				unoMinimalConfiguration(), 
				unoDerbyInMemoryConfiguration(), 
				blueprintBundles(),
				junitBundles(), 
				mavenBundle("org.mockito", "mockito-all").versionAsInProject(),
				mavenBundle("org.apache.commons", "com.springsource.org.apache.commons.io").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4) 
		);
		// @formatter:on
	}

	@Test
	public void testServiceIsUp() {
		assertNotNull(reportingService);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRegisterReportingEngineAndProvider() throws Exception {
		ReportingEngine<String> engine = mock(ReportingEngine.class);
		when(engine.getReportClass()).thenReturn(String.class);
		when(engine.generateReport(anyString(), anyMap(), anyString())).thenReturn(
				new ByteArrayInputStream(REPORT1_RESULT.getBytes()));
		reportingService.registerReportingEngine(engine, 0);

		ReportsProvider<String> reportsProvider = mock(ReportsProvider.class);
		when(reportsProvider.findReportDefinition(REPORT1_ID)).thenReturn(new BaseReportDefinition<String>(REPORT1_ID, 0) {

			@Override
			public Class<String> getReportClass() {
				return String.class;
			}

			@Override
			public String getReport() {
				return REPORT1_CONTENT;
			}

		});
		reportingService.registerReportsProvider(reportsProvider);

		InputStream is = reportingService.generateReport(REPORT1_ID);
		String result = IOUtils.toString(is);
		assertEquals(REPORT1_RESULT, result);

		verify(reportsProvider).findReportDefinition(REPORT1_ID);

		verify(engine).generateReport(REPORT1_CONTENT, null, null);

		reportingService.unregisterReportsProvider(reportsProvider);
		reportingService.unregisterReportingEngine(engine);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAutoRegisterReportingEngineAndProvider() throws Exception {
		ReportingEngine<String> engine = mock(ReportingEngine.class);
		when(engine.getReportClass()).thenReturn(String.class);
		when(engine.generateReport(anyString(), anyMap(), anyString())).thenReturn(
				new ByteArrayInputStream(REPORT1_RESULT.getBytes()));
		ServiceRegistration<ReportingEngine> registration1 = bc.registerService(ReportingEngine.class, engine, null);

		ReportsProvider<String> reportsProvider = mock(ReportsProvider.class);
		when(reportsProvider.findReportDefinition(REPORT1_ID)).thenReturn(new BaseReportDefinition<String>(REPORT1_ID, 0) {

			@Override
			public Class<String> getReportClass() {
				return String.class;
			}

			@Override
			public String getReport() {
				return REPORT1_CONTENT;
			}

		});
		ServiceRegistration<ReportsProvider> registration2 = bc.registerService(ReportsProvider.class, reportsProvider, null);

		InputStream is = reportingService.generateReport(REPORT1_ID);
		String result = IOUtils.toString(is);
		assertEquals(REPORT1_RESULT, result);

		verify(reportsProvider).findReportDefinition(REPORT1_ID);

		verify(engine).generateReport(REPORT1_CONTENT, null, null);

		registration2.unregister();
		registration1.unregister();
	}

}
