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
import static org.ops4j.pax.tinybundles.core.TinyBundles.*;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.openeos.wf.test.WorkflowConfiguration.wfDependencyBundles;

import java.io.InputStream;

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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.wf.Deployment;
import org.openeos.wf.WorkflowService;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class WorkflowExtenderTestCase implements Constants, org.openeos.wf.Constants {

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowExtenderTestCase.class);

	private static final String TEST1_FILE = "test1.bpmn";
	private static final String TEST1_MODIFIED_FILE = "test1_modified.bpmn";
	private static final String TEST1_KEY = "org.openeos.jbpm.integration.test1";

	private static final String BUNDLE1_SYMBOLIC_NAME = "test.symbolic.1";
	private static final String BUNDLE1_VERSION = "1.0.0.Final";

	@Inject
	@Filter(timeout = 100000)
	private WorkflowService workflowService;

	@Inject
	private BundleContext bc;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options( 
				unoDefaultConfiguration(), 
				unoDerbyInMemoryConfiguration(), 
				junitBundles(), 
				wfDependencyBundles(),
				mavenBundle("org.ops4j.pax.tinybundles", "tinybundles").versionAsInProject(),
				mavenBundle("biz.aQute.bnd", "bndlib").versionAsInProject(), 
				weaverVerbose(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4)
		); 
		// @formatter:on
	}

	@Test
	public void testExtenderBundleDefaultValues() throws Exception {
		InputStream in = bundle().add("OSGI-INF/bpmn/test1.bpmn", bc.getBundle().getEntry(TEST1_FILE))
				.set(BUNDLE_SYMBOLICNAME, BUNDLE1_SYMBOLIC_NAME).set(BUNDLE_VERSION, BUNDLE1_VERSION).build();
		Bundle bundle = bc.installBundle(BUNDLE1_SYMBOLIC_NAME, in);
		bundle.start();
		Thread.sleep(10000);
		LOG.debug("The bundle is in state: {}" + bundle.getState());
		Deployment deployment = workflowService.getDeployment(DEPLOYMENT_PREFIX + BUNDLE1_SYMBOLIC_NAME, BUNDLE1_VERSION);
		assertNotNull(deployment);
		workflowService.revertDeployment(deployment.getId());
	}

}
