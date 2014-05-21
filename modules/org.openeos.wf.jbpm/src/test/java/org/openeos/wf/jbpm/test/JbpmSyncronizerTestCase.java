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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.jbpm.integration.KnowledgeBaseProvider;
import org.openeos.wf.Deployment;
import org.openeos.wf.JavaServiceTaskService;
import org.openeos.wf.WorkflowDefinition;
import org.openeos.wf.WorkflowService;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class JbpmSyncronizerTestCase implements Constants, org.openeos.wf.Constants {

	private static final Logger LOG = LoggerFactory.getLogger(JbpmSyncronizerTestCase.class);

	@Inject
	@Filter(timeout = 20000)
	private WorkflowService workflowService;

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 20000)
	private KnowledgeBaseProvider kbaseProvider;

	// This service is for waiting to blueprint because the implemention waits long time 
	@Inject
	@Filter(timeout = 20000)
	private JavaServiceTaskService javaServiceTaskService;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options(
				unoDefaultConfiguration(), 
				unoDerbyInMemoryConfiguration(), 
				junitBundles(), 
				wfBundles(),
				jbpmIntegrationBundles(), 
				mavenBundle("org.ops4j.pax.tinybundles", "tinybundles").versionAsInProject(),
				mavenBundle("biz.aQute.bnd", "bndlib").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4), 
				weaverVerbose(),
				systemTimeout(1000 * 60 * 15)
		);
		// @formatter:on
	}

	@Test
	public void testServiceIsUp() throws Exception {
		assertNotNull(kbaseProvider);
	}

	@Test
	public void testInstallNewProcess() throws Exception {
		URL test1 = getClass().getClassLoader().getResource("test1.bpmn");
		Deployment deployment = workflowService.createDeployment().key("test1").version("0.0.0").addURL(test1).deploy();
		WorkflowDefinition def = workflowService.getLastWorkflowDefinitionByKey("org.openeos.jbpm.integration.test1");
		assertEquals(deployment.getId(), def.getDeploymemtId());
		Thread.sleep(10000);
		org.drools.definition.process.Process process = kbaseProvider.getKnowledgeBase().getProcess(def.getId());
		assertNotNull(process);
	}

}
