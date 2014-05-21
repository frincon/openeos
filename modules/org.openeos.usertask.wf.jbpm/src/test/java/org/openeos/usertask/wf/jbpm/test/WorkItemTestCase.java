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
package org.openeos.usertask.wf.jbpm.test;

import static org.openeos.jbpm.integration.test.JbpmIntegrationConfiguration.*;
import static org.openeos.test.UnoConfiguration.*;
import static org.openeos.wf.test.WorkflowConfiguration.*;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.NodeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
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

import org.openeos.erp.core.dao.ClientDAO;
import org.openeos.erp.core.dao.UserDAO;
import org.openeos.erp.core.model.Client;
import org.openeos.erp.core.model.Contact;
import org.openeos.erp.core.model.User;
import org.openeos.erp.core.model.list.UserType;
import org.openeos.usertask.UserTask;
import org.openeos.usertask.UserTaskService;
import org.openeos.wf.Deployment;
import org.openeos.wf.JavaServiceTaskService;
import org.openeos.wf.WorkflowDefinition;
import org.openeos.wf.WorkflowEngine;
import org.openeos.wf.WorkflowService;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class WorkItemTestCase implements Constants, org.openeos.wf.Constants {

	private static final Logger LOG = LoggerFactory.getLogger(WorkItemTestCase.class);

	private static final String USERNAME1 = "testUserName1";
	private static final String USERNAME2 = "testUserName2";

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

	@Inject
	@Filter(timeout = 10000)
	private UserDAO userDAO;

	@Inject
	@Filter(timeout = 10000)
	private ClientDAO clientDAO;

	@Inject
	@Filter(timeout = 10000)
	private UserTaskService userTaskService;

	private WorkflowDefinition def1;
	private Deployment deployment1;

	private Client systemClient;
	private User user1;
	private User user2;

	@Configuration
	public static Option[] config() {
		// @formatter:off
		return options(
				unoDefaultConfiguration(), 
				unoMasterData(),
				unoDerbyInMemoryConfiguration(), 
				junitBundles(), 
				wfBundles(),
				jbpmIntegrationBundles(), 
				mavenBundle("org.openeos", "org.openeos.wf.jbpm").versionAsInProject(),
				mavenBundle("org.openeos", "org.openeos.usertask").versionAsInProject(),
				mavenBundle("org.openeos", "org.openeos.erp.core").versionAsInProject(),
				mavenBundle("org.mockito", "mockito-all").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4) 
		);
		// @formatter:on
	}

	@Before
	public void installProcess() throws Exception {
		URL test1 = WorkItemTestCase.class.getClassLoader().getResource("test1.bpmn");
		deployment1 = workflowService.createDeployment().key("test_work_item1").version("0.0.0").addURL(test1).deploy();
		def1 = workflowService.getLastWorkflowDefinitionByKey("org.openeos.usertask.wf.jbpm.test1");
		assertEquals(deployment1.getId(), def1.getDeploymemtId());
		Thread.sleep(2000);
	}

	@Before
	public void createUser() throws Exception {
		systemClient = clientDAO.findClientBySearchKey("*");
		assertNotNull(systemClient);
		user1 = new User(new Contact(), systemClient, USERNAME1, UserType.USERNAME_PASSWORD, false);
		user2 = new User(new Contact(), systemClient, USERNAME2, UserType.USERNAME_PASSWORD, false);
		userDAO.create(user1);
		userDAO.create(user2);

	}

	@After
	public void removeProcess() throws Exception {
		workflowService.revertDeployment(deployment1.getId());
		deployment1 = null;
		def1 = null;
	}

	@After
	public void deleteUser() throws Exception {
		userDAO.delete(user1);
		userDAO.delete(user2);
	}

	@Test
	public void testCreateUserTask() throws Exception {
		String processInstanceId = engine.startProcess(def1.getId());
		List<UserTask> taskList = userTaskService.findTasksByAssignedUser(user1.getId());
		assertEquals(1, taskList.size());

		UserTask task = taskList.get(0);
		assertEquals("Test Name", task.getName());
		assertEquals("Test Comment", task.getDescription());
		assertEquals(40, task.getPriority());

		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) session.getProcessInstance(Long
				.parseLong(processInstanceId));
		assertEquals(WorkflowProcessInstance.STATE_ACTIVE, processInstance.getState());
		Collection<NodeInstance> nodes = processInstance.getNodeInstances();
		assertEquals(1, nodes.size());
		NodeInstance node = nodes.iterator().next();
		System.out.println(node);

		userTaskService.deleteTask(task);

		Thread.sleep(2000);

		processInstance = (WorkflowProcessInstance) session.getProcessInstance(Long.parseLong(processInstanceId));
		assertNull(processInstance);
	}

}
