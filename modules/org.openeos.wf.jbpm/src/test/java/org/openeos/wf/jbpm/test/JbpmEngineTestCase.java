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
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.net.URL;

import javax.inject.Inject;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.junit.After;
import org.junit.Before;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.wf.Deployment;
import org.openeos.wf.WorkflowDefinition;
import org.openeos.wf.WorkflowEngine;
import org.openeos.wf.WorkflowService;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JbpmEngineTestCase implements Constants, org.openeos.wf.Constants {

	private static final Logger LOG = LoggerFactory.getLogger(JbpmEngineTestCase.class);

	@Inject
	@Filter(timeout = 20000)
	private WorkflowService workflowService;

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 20000)
	private WorkflowEngine engine;

	@Inject
	@Filter(timeout = 20000)
	private StatefulKnowledgeSession session;

	private WorkflowDefinition def;
	private Deployment deployment;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options(
				unoDefaultConfiguration(), 
				unoDerbyInMemoryConfiguration(), 
				junitBundles(), 
				wfBundles(),
				jbpmIntegrationBundles(), 
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4), 
				weaverVerbose(),
				systemTimeout(1000 * 60 * 15)
		);
		// @formatter:on
	}

	@Before
	public void installProcess() throws Exception {
		URL test1 = JbpmEngineTestCase.class.getClassLoader().getResource("test1.bpmn");
		deployment = workflowService.createDeployment().key("test1").version("0.0.0").addURL(test1).deploy();
		def = workflowService.getLastWorkflowDefinitionByKey("org.openeos.jbpm.integration.test1");
		assertEquals(deployment.getId(), def.getDeploymemtId());
		Thread.sleep(10000);
	}

	@After
	public void removeProcess() throws Exception {
		workflowService.revertDeployment(deployment.getId());
		deployment = null;
		def = null;
	}

	@Test
	public void testServiceIsUp() throws Exception {
		assertNotNull(engine);
	}

	@Test
	public void testStartProcess() throws Exception {
		String instanceId = engine.startProcess(def.getId());
		ProcessInstance instance = session.getProcessInstance(Long.parseLong(instanceId));
		assertNotNull(instance);
	}
}
