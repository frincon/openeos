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
package org.openeos.usertask.test;

import static org.openeos.test.UnoConfiguration.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import org.openeos.erp.core.dao.ClientDAO;
import org.openeos.erp.core.dao.UserDAO;
import org.openeos.erp.core.model.Client;
import org.openeos.erp.core.model.Contact;
import org.openeos.erp.core.model.User;
import org.openeos.erp.core.model.list.UserType;
import org.openeos.usertask.UserTask;
import org.openeos.usertask.UserTaskService;
import org.openeos.usertask.eventadmin.Constants;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TestUserTaskService {

	private static final String USERNAME1 = "testUserName1";
	private static final String USERNAME2 = "testUserName2";

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 10000)
	private UserTaskService userTaskService;

	@Inject
	@Filter(timeout = 10000)
	private UserDAO userDAO;

	@Inject
	@Filter(timeout = 10000)
	private ClientDAO clientDAO;

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
				mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").version("1.3.2"),
				mavenBundle("org.openeos", "org.openeos.erp.core").version(getTestVersion()),
				mavenBundle("org.mockito", "mockito-all").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4)
		);
		// @formatter:on
	}

	@Before
	public void setUp() {
		systemClient = clientDAO.findClientBySearchKey("*");
		assertNotNull(systemClient);
		user1 = new User(new Contact(), systemClient, USERNAME1, UserType.USERNAME_PASSWORD, false);
		user2 = new User(new Contact(), systemClient, USERNAME2, UserType.USERNAME_PASSWORD, false);
		userDAO.create(user1);
		userDAO.create(user2);
	}

	@After
	public void tearDown() {
		userDAO.delete(user1);
		userDAO.delete(user2);
	}

	@Test
	public void testAddTaskToUserAndDelete() {
		int priority = 50;
		String taskName = "Task Name";
		String taskDescription = "Task Description";
		UserTask task = userTaskService.createTaskAssigned(taskName, taskDescription, priority, user1.getId(), null);
		List<UserTask> taskList = userTaskService.findTasksByAssignedUser(user1.getId());
		assertEquals(1, taskList.size());
		assertEquals(task.getId(), taskList.get(0).getId());

		userTaskService.deleteTask(task);
		taskList = userTaskService.findTasksByAssignedUser(user1.getId());
		assertEquals(0, taskList.size());

	}

	@Test
	public void testAddTaskToUserAndDeleteWithMetaData() {
		int priority = 50;
		String taskName = "Task Name";
		String taskDescription = "Task Description";
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put("test1", "test1.value");
		metaData.put("test2", "test2.value");
		UserTask task = userTaskService.createTaskAssigned(taskName, taskDescription, priority, user1.getId(), metaData);
		List<UserTask> taskList = userTaskService.findTasksByAssignedUser(user1.getId());
		assertEquals(1, taskList.size());
		assertEquals(task.getId(), taskList.get(0).getId());
		assertEquals("test1.value", task.getMetaData("test1"));
		assertEquals("test2.value", task.getMetaData("test2"));
		assertEquals(2, task.getMetaData().keySet().size());

		userTaskService.deleteTask(task);
		taskList = userTaskService.findTasksByAssignedUser(user1.getId());
		assertEquals(0, taskList.size());

	}

	@Test
	public void testFindByMetaData() {
		int priority = 50;
		String taskName = "Task Name";
		String taskDescription = "Task Description";
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put("test1", "test1.value");
		metaData.put("test2", "test2.value");
		UserTask task = userTaskService.createTaskAssigned(taskName, taskDescription, priority, user1.getId(), metaData);

		metaData.clear();
		metaData.put("test1", "wrong.value");
		metaData.put("test2", "wrong.value");
		UserTask task2 = userTaskService.createTaskAssigned(taskName, taskDescription, priority, user1.getId(), metaData);

		List<UserTask> taskList = userTaskService.findTasksByMetaDataValue("test1", "test1.value");
		assertEquals(1, taskList.size());
		assertEquals(task.getId(), taskList.get(0).getId());
		assertEquals("test1.value", task.getMetaData("test1"));
		assertEquals("test2.value", task.getMetaData("test2"));
		assertEquals(2, task.getMetaData().keySet().size());

		taskList = userTaskService.findTasksByMetaDataValue("noid", "novalue");
		assertEquals(0, taskList.size());

		taskList = userTaskService.findTasksByMetaDataValue("test1", "wrong.value");
		assertEquals(1, taskList.size());

		userTaskService.deleteTask(task);
		userTaskService.deleteTask(task2);
		taskList = userTaskService.findTasksByAssignedUser(user1.getId());
		assertEquals(0, taskList.size());

	}

	@Test
	public void testCompleteTask() {
		int priority = 50;
		String taskName = "Task Name";
		String taskDescription = "Task Description";
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put("test1", "test1.value");
		metaData.put("test2", "test2.value");
		UserTask task = userTaskService.createTaskAssigned(taskName, taskDescription, priority, user1.getId(), metaData);

		List<UserTask> taskList = userTaskService.findTasksByMetaDataValue("test1", "test1.value");
		assertEquals(1, taskList.size());
		assertEquals(task.getId(), taskList.get(0).getId());
		assertEquals("test1.value", task.getMetaData("test1"));
		assertEquals("test2.value", task.getMetaData("test2"));
		assertEquals(2, task.getMetaData().keySet().size());

		userTaskService.completeTask(task);

		taskList = userTaskService.findTasksByMetaDataValue("test1", "test1.value");
		assertEquals(0, taskList.size());

		userTaskService.deleteTask(task);
	}

	@Test
	public void testEventAdmin() throws Exception {

		EventHandler handler = mock(EventHandler.class);
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, Constants.PREFIX_TOPIC + "*");
		ServiceRegistration<EventHandler> register = bc.registerService(EventHandler.class, handler, properties);

		int priority = 50;
		String taskName = "Task Name";
		String taskDescription = "Task Description";
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put("test1", "test1.value");
		metaData.put("test2", "test2.value");
		metaData.put(Constants.PROPERTY_ASSIGNED_USER_ID, "test for not propagate this to event");
		UserTask task = userTaskService.createTaskAssigned(taskName, taskDescription, priority, user1.getId(), metaData);

		Thread.sleep(100);
		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
		verify(handler, times(2)).handleEvent(eventCaptor.capture());

		Event event1 = eventCaptor.getAllValues().get(0);
		Event event2 = eventCaptor.getAllValues().get(1);

		assertEquals(Constants.TOPIC_CREATED, event1.getTopic());
		assertEquals(task.getId(), event1.getProperty(Constants.PROPERTY_TASK_ID));
		assertEquals(user1.getId(), event1.getProperty(Constants.PROPERTY_ASSIGNED_USER_ID));
		assertEquals("test1.value", event1.getProperty("test1"));
		assertEquals("test2.value", event1.getProperty("test2"));

		assertEquals(Constants.TOPIC_ASSIGNED, event2.getTopic());
		assertEquals(task.getId(), event2.getProperty(Constants.PROPERTY_TASK_ID));
		assertEquals(user1.getId(), event2.getProperty(Constants.PROPERTY_ASSIGNED_USER_ID));
		assertEquals("test1.value", event2.getProperty("test1"));
		assertEquals("test2.value", event2.getProperty("test2"));

		userTaskService.completeTask(task);
		Thread.sleep(100);

		eventCaptor = ArgumentCaptor.forClass(Event.class);
		verify(handler, times(3)).handleEvent(eventCaptor.capture());
		Event event3 = eventCaptor.getValue();

		assertEquals(Constants.TOPIC_COMPLETED, event3.getTopic());
		assertEquals(task.getId(), event3.getProperty(Constants.PROPERTY_TASK_ID));
		assertEquals(user1.getId(), event3.getProperty(Constants.PROPERTY_ASSIGNED_USER_ID));
		assertEquals("test1.value", event3.getProperty("test1"));
		assertEquals("test2.value", event3.getProperty("test2"));

		userTaskService.deleteTask(task);
		Thread.sleep(100);

		verify(handler, times(4)).handleEvent(eventCaptor.capture());
		Event event4 = eventCaptor.getValue();

		assertEquals(Constants.TOPIC_DELETED, event4.getTopic());
		assertEquals(task.getId(), event4.getProperty(Constants.PROPERTY_TASK_ID));
		assertEquals(user1.getId(), event4.getProperty(Constants.PROPERTY_ASSIGNED_USER_ID));
		assertEquals("test1.value", event4.getProperty("test1"));
		assertEquals("test2.value", event4.getProperty("test2"));

		register.unregister();

	}
}
