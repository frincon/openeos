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
package org.openeos.wf.test;

import static org.openeos.test.UnoConfiguration.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.openeos.wf.test.WorkflowConfiguration.*;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.wf.JavaServiceTaskAware;
import org.openeos.wf.JavaServiceTaskException;
import org.openeos.wf.JavaServiceTaskService;
import org.openeos.wf.WorkflowService;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class WorkflowJavaServiceTestCase implements Constants {

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowJavaServiceTestCase.class);

	private static final String SERVICE_NAME = "testService";

	@Inject
	@Filter(timeout = 100000)
	private JavaServiceTaskService callerService;

	@Inject
	private BundleContext bc;

	@Configuration
	public static Option[] config() {
		return options(unoDefaultConfiguration(), unoDerbyInMemoryConfiguration(), junitBundles(), wfDependencyBundles(),
				mavenBundle("org.mockito", "mockito-all").versionAsInProject(), weaverVerbose(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4));
	}

	@Test
	public void testServiceCaller() throws Exception {
		TestService service = mock(TestService.class);
		callerService.registerService(SERVICE_NAME, service);
		checkService(service);
		callerService.unregisterService(SERVICE_NAME);
	}

	private void checkService(TestService service) throws Exception {
		List<String> result = Arrays.asList("Uno", "Dos", "Tres");
		when(service.testCallWithReturnValue(anyString())).thenReturn(result);

		Object result1 = callerService.callService(SERVICE_NAME, "testSimpleCall", "TEST");
		verify(service).testSimpleCall("TEST");
		assertNull(result1);

		List<String> result2 = (List<String>) callerService.callService(SERVICE_NAME, "testCallWithReturnValue", "TEST");
		assertEquals(result, result2);
		verify(service).testCallWithReturnValue("TEST");

	}

	@Test
	public void testAutoRegister() throws Exception {
		TestService service = mock(TestService.class, withSettings().extraInterfaces(JavaServiceTaskAware.class));
		JavaServiceTaskAware serviceAware = (JavaServiceTaskAware) service;
		when(serviceAware.getName()).thenReturn(SERVICE_NAME);
		ServiceRegistration registration = bc.registerService(new String[] { TestService.class.getName(),
				JavaServiceTaskAware.class.getName() }, service, null);
		Thread.sleep(1000);
		checkService(service);
		registration.unregister();
	}

	@Test(expected = JavaServiceTaskException.class)
	public void testAuntoUnregister() throws Exception {
		testAutoRegister();
		checkService(mock(TestService.class));
	}
}
