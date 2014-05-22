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
import static org.openeos.wf.test.WorkflowConfiguration.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.net.URL;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.openeos.wf.Deployment;
import org.openeos.wf.WorkflowDefinition;
import org.openeos.wf.WorkflowEngine;
import org.openeos.wf.WorkflowService;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class WorkflowServiceTestCase implements Constants {

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowServiceTestCase.class);

	private static final String TEST1_FILE = "test1.bpmn";
	private static final String TEST1_MODIFIED_FILE = "test1_modified.bpmn";
	private static final String TEST1_KEY = "org.openeos.jbpm.integration.test1";

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 10000)
	private WorkflowService workflowService;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options(
				unoDefaultConfiguration(), 
				unoDerbyInMemoryConfiguration(), 
				junitBundles(), 
				wfDependencyBundles(),
				weaverVerbose(),
				mavenBundle("org.mockito", "mockito-all").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4)
		);
		// @formatter:on
	}

	@Test
	public void testDeploySingleProcess() {
		URL test1 = getClass().getClassLoader().getResource(TEST1_FILE);
		LOG.debug("Deploying one definition");
		Deployment deploy = workflowService.createDeployment().key("testDeploySingleProcess").addURL(test1).deploy();
		WorkflowDefinition definition = workflowService.getLastWorkflowDefinitionByKey(TEST1_KEY);
		LOG.debug("Definition is null?");
		assertNotNull(definition);
		workflowService.revertDeployment(deploy.getId());
	}

	@Test
	public void testDeployAnotherVersion() {
		URL test1 = getClass().getClassLoader().getResource(TEST1_FILE);
		Deployment deploy1 = workflowService.createDeployment().key("testDeployAnotherVersion").addURL(test1).version("1").deploy();
		long count = workflowService.getWorkflowDefinitionListByKey(TEST1_KEY).size();
		assertEquals(1, count);
		Deployment deploy2 = workflowService.createDeployment().key("testDeployAnotherVersion").addURL(test1).version("2").deploy();
		count = workflowService.getWorkflowDefinitionListByKey(TEST1_KEY).size();
		assertEquals(2, count);

		WorkflowDefinition def = workflowService.getLastWorkflowDefinitionByKey(TEST1_KEY);

		assertEquals(def.getDeploymemtId(), deploy2.getId());

		workflowService.revertDeployment(deploy2.getId());
		workflowService.revertDeployment(deploy1.getId());
	}

	@Test
	public void testDeployAndUndeployPreviousVersion() {
		URL test1 = getClass().getClassLoader().getResource(TEST1_FILE);
		Deployment deploy1 = workflowService.createDeployment().key("testDeployAndUndeployPreviousVersion").addURL(test1)
				.enableDuplicateFiltering().deploy();

		long count = workflowService.getWorkflowDefinitionListByKey(TEST1_KEY).size();
		assertEquals(1, count);

		Deployment deploy2 = workflowService.createDeployment().key("testDeployAndUndeployPreviousVersion").addURL(test1)
				.enableDuplicateFiltering().deploy();
		count = workflowService.getWorkflowDefinitionListByKey(TEST1_KEY).size();
		assertEquals(2, count);

		workflowService.revertDeployment(deploy2.getId());
		workflowService.revertDeployment(deploy1.getId());

	}

	@Test
	public void testDefinitionStoreContent() {
		URL test1 = getClass().getClassLoader().getResource(TEST1_FILE);
		Deployment deploy1 = workflowService.createDeployment().key("testDefinitionStoreContent").addURL(test1)
				.enableDuplicateFiltering().deploy();
		long count = workflowService.getWorkflowDefinitionListByKey(TEST1_KEY).size();
		assertEquals(1, count);

		WorkflowDefinition def = workflowService.getLastWorkflowDefinitionByKey(TEST1_KEY);
		byte[] content = def.getContent();
		assertNotNull(content);
		assertTrue(content.length > 0);
		workflowService.revertDeployment(deploy1.getId());
	}

	@Test
	public void testEngineRegistry() {
		WorkflowEngine engine = mock(WorkflowEngine.class);
		ServiceRegistration<WorkflowEngine> registration = bc.registerService(WorkflowEngine.class, engine, null);
		URL test1 = getClass().getClassLoader().getResource(TEST1_FILE);
		Deployment deploy1 = workflowService.createDeployment().key("testEngineRegistry").addURL(test1).enableDuplicateFiltering()
				.deploy();
		WorkflowDefinition def = workflowService.getLastWorkflowDefinitionByKey(TEST1_KEY);

		workflowService.startProcess(def.getId());

		verify(engine).startProcess(def.getId());

		registration.unregister();
		workflowService.revertDeployment(deploy1.getId());
	}

	@Test
	public void testAutoAddUserParameter() {
		WorkflowEngine engine = mock(WorkflowEngine.class);
		ServiceRegistration<WorkflowEngine> registration = bc.registerService(WorkflowEngine.class, engine, null);
		URL test1 = getClass().getClassLoader().getResource(TEST1_FILE);
		Deployment deploy1 = workflowService.createDeployment().key("testAutoAddUserParameter").addURL(test1)
				.enableDuplicateFiltering().deploy();
		WorkflowDefinition def = workflowService.getLastWorkflowDefinitionByKey(TEST1_KEY);

		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("testUser", "testCredentials"));

		workflowService.startProcess(def.getId());

		ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

		verify(engine).startProcess(eq(def.getId()), captor.capture());

		Map<String, Object> parameters = captor.getValue();
		assertEquals("testUser", parameters.get(org.openeos.wf.Constants.LANUCHER_USER_PARAMETER));

		registration.unregister();
		workflowService.revertDeployment(deploy1.getId());

		SecurityContextHolder.getContext().setAuthentication(null);
	}

}
