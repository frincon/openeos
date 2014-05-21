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
package org.openeos.wf.jbpm.test;

import static org.openeos.jbpm.integration.test.JbpmIntegrationConfiguration.*;
import static org.openeos.test.UnoConfiguration.*;
import static org.openeos.wf.test.WorkflowConfiguration.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.util.Hashtable;

import javax.inject.Inject;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

import org.openeos.wf.Deployment;
import org.openeos.wf.JavaServiceTaskService;
import org.openeos.wf.WorkflowDefinition;
import org.openeos.wf.WorkflowEngine;
import org.openeos.wf.WorkflowService;
import org.openeos.wf.jbpm.internal.JbpmJavaServiceTaskHandler;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class WorkItemTestCase implements Constants, org.openeos.wf.Constants {

	private static final Logger LOG = LoggerFactory.getLogger(WorkItemTestCase.class);

	@Inject
	@Filter(timeout = 10000)
	private WorkflowService workflowService;

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 10000)
	private WorkflowEngine engine;

	@Inject
	@Filter(timeout = 10000)
	private StatefulKnowledgeSession session;

	@Inject
	@Filter(timeout = 10000)
	private JavaServiceTaskService javaServiceTaskService;

	private WorkflowDefinition def1;
	private Deployment deployment1;
	private WorkflowDefinition def2;
	private Deployment deployment2;
	private WorkflowDefinition def3;
	private Deployment deployment3;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options(
				unoDefaultConfiguration(), 
				unoDerbyInMemoryConfiguration(), 
				junitBundles(), 
				wfBundles(),
				jbpmIntegrationBundles(), 
				mavenBundle("org.mockito", "mockito-all").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4), 
				weaverVerbose()
		);
		// @formatter:on
	}

	@Before
	public void installProcess() throws Exception {
		URL test1 = WorkItemTestCase.class.getClassLoader().getResource("test_work_item1.bpmn");
		deployment1 = workflowService.createDeployment().key("test_work_item1").version("0.0.0").addURL(test1).deploy();
		def1 = workflowService.getLastWorkflowDefinitionByKey("org.openeos.jbpm.integration.test1");
		assertEquals(deployment1.getId(), def1.getDeploymemtId());

		URL test2 = WorkItemTestCase.class.getClassLoader().getResource("test_work_item2.bpmn");
		deployment2 = workflowService.createDeployment().key("test_work_item2").version("0.0.0").addURL(test2).deploy();
		def2 = workflowService.getLastWorkflowDefinitionByKey("org.openeos.jbpm.integration.test2");
		assertEquals(deployment2.getId(), def2.getDeploymemtId());

		URL test3 = WorkItemTestCase.class.getClassLoader().getResource("test_work_item3.bpmn");
		deployment3 = workflowService.createDeployment().key("test_work_item3").version("0.0.0").addURL(test3).deploy();
		def3 = workflowService.getLastWorkflowDefinitionByKey("org.openeos.jbpm.integration.test3");
		assertEquals(deployment3.getId(), def3.getDeploymemtId());

		Thread.sleep(2000);
	}

	@After
	public void removeProcess() throws Exception {
		workflowService.revertDeployment(deployment1.getId());
		workflowService.revertDeployment(deployment2.getId());
		workflowService.revertDeployment(deployment3.getId());
		deployment1 = null;
		def1 = null;
		deployment2 = null;
		def2 = null;
		deployment3 = null;
		def3 = null;
	}

	@Test
	public void testAutoRegisterServiceTaskHandler() throws Exception {
		WorkItemHandler handler = mock(WorkItemHandler.class);
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put(SERVICE_RANKING, Integer.MAX_VALUE);
		props.put("org.openeos.wf.jbpm.SERVICE_WORKITEM_HANDLER_NAME", "Service Task");
		ServiceRegistration<WorkItemHandler> registration = bc.registerService(WorkItemHandler.class, handler, props);
		Thread.sleep(100);
		engine.startProcess(def1.getId());

		ArgumentCaptor<WorkItem> argumentWorkItem = ArgumentCaptor.forClass(WorkItem.class);

		verify(handler).executeWorkItem(argumentWorkItem.capture(), any(WorkItemManager.class));
		WorkItem workItem = argumentWorkItem.getValue();

		assertEquals("workItemTestService", workItem.getParameter("Interface"));
		assertEquals("testSimpleCall", workItem.getParameter("Operation"));
		assertEquals("Test String Input Parameter", workItem.getParameter(JbpmJavaServiceTaskHandler.PARAMETER_INPUT_PARAMS));

		registration.unregister();
	}

	@Test
	public void testRanking() throws Exception {
		WorkItemHandler handler1 = mock(WorkItemHandler.class);
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put(SERVICE_RANKING, Integer.MAX_VALUE - 10);
		props.put("org.openeos.wf.jbpm.SERVICE_WORKITEM_HANDLER_NAME", "Service Task");
		ServiceRegistration<WorkItemHandler> registration1 = bc.registerService(WorkItemHandler.class, handler1, props);
		Thread.sleep(100);
		engine.startProcess(def1.getId());
		verify(handler1).executeWorkItem(any(WorkItem.class), any(WorkItemManager.class));

		WorkItemHandler handler2 = mock(WorkItemHandler.class);
		props = new Hashtable<String, Object>();
		props.put(SERVICE_RANKING, Integer.MAX_VALUE);
		props.put("org.openeos.wf.jbpm.SERVICE_WORKITEM_HANDLER_NAME", "Service Task");
		ServiceRegistration<WorkItemHandler> registration2 = bc.registerService(WorkItemHandler.class, handler2, props);
		engine.startProcess(def1.getId());
		verify(handler2).executeWorkItem(any(WorkItem.class), any(WorkItemManager.class));
		registration2.unregister();

		engine.startProcess(def1.getId());
		verify(handler1).executeWorkItem(any(WorkItem.class), any(WorkItemManager.class));

		registration1.unregister();

	}

	@Test
	public void testDefaultJavaServiceTask() throws Exception {
		TestService testService = mock(TestService.class);
		when(testService.testSimpleCall(any())).thenReturn("Result Test!!!");
		javaServiceTaskService.registerService("workItemTestService", testService);
		engine.startProcess(def2.getId());

		verify(testService).testSimpleCall("Test String Input Parameter");
		verify(testService).testSimpleCall("Result Test!!!");

	}

	@Test
	public void testWorkflowExceptionThrown() throws Exception {
		session.getWorkItemManager().registerWorkItemHandler("Human Task", null);
		TestService testService = mock(TestService.class);
		when(testService.testSimpleCall(any())).thenReturn("Result Test!!!");
		javaServiceTaskService.registerService("workItemTestService", testService);
		Exception exception = null;
		try {
			engine.startProcess(def3.getId());
		} catch (Exception ex) {
			exception = ex;
		}
		verify(testService).testSimpleCall("Test String Input Parameter");

		assertNotNull(exception);
		assertTrue(exception instanceof WorkflowRuntimeException);
	}

	@Test
	public void testJavaServiceExceptionNotThrown() throws Exception {
		TestService testService = mock(TestService.class);
		when(testService.testSimpleCall(any())).thenThrow(new RuntimeException("Exception not thrown"));
		javaServiceTaskService.registerService("workItemTestService", testService);
		engine.startProcess(def2.getId());
		verify(testService).testSimpleCall("Test String Input Parameter");
	}
}
