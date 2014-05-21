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
package org.openeos.usertask.wf.jbpm.internal;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.erp.core.dao.UserDAO;
import org.openeos.erp.core.model.User;
import org.openeos.usertask.UserTask;
import org.openeos.usertask.UserTaskService;

public class UserTaskHandler implements WorkItemHandler {

	private static final Logger LOG = LoggerFactory.getLogger(UserTaskHandler.class);

	public static final String PROCESS_INSTANCE_ID_KEY = UserTaskHandler.class.getName() + ".PROCESS_ID_KEY";
	public static final String WORKITEM_ID_KEY = UserTaskHandler.class.getName() + ".WORKITEM_ID_KEY";

	private UserTaskService userTaskService;
	private UserDAO userDAO;

	private UserTaskListener userTaskListener;

	public UserTaskHandler(UserTaskService userTaskService, UserDAO userDAO, UserTaskListener userTaskListener) {
		this.userTaskService = userTaskService;
		this.userDAO = userDAO;
		this.userTaskListener = userTaskListener;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String taskName = (String) workItem.getParameter("TaskName");
		Integer priority = Integer.valueOf((String) workItem.getParameter("Priority"));
		String comment = (String) workItem.getParameter("Comment");
		Boolean skippable = Boolean.parseBoolean((String) workItem.getParameter("Skippable"));
		String content = (String) workItem.getParameter("Content");
		String locale = (String) workItem.getParameter("Locale");
		String actorId = (String) workItem.getParameter("ActorId");
		String roleId = (String) workItem.getParameter("GroupId");
		Object metaDataObject = workItem.getParameter("Metadata");
		if (skippable != null && skippable) {
			LOG.warn("The task is skippable but is not supported by UserTaskService jet, ignoring");
		}
		if (locale != null) {
			LOG.warn("Locale not supported for user task jet.");
		}
		if (content != null) {
			LOG.warn("Content is not supported for user task jet.");
		}

		if (actorId == null && roleId == null) {
			throw new UnsupportedOperationException("The user task has no actor nor role");
		}
		if (actorId != null && roleId != null) {
			throw new UnsupportedOperationException("Providing user and role is not supported");
		}
		if (actorId != null && actorId.contains(",")) {
			throw new UnsupportedOperationException("The user task service don't support multiple actors jet");
		}
		if (roleId != null && roleId.contains(",")) {
			throw new UnsupportedOperationException("The user task service don't support multiple roles jet");
		}
		if (taskName == null || taskName.trim().length() == 0) {
			throw new UnsupportedOperationException("The user task need a name");
		}
		HashMap<String, String> metaData = new HashMap<String, String>();
		metaData.put(PROCESS_INSTANCE_ID_KEY, Long.toString(workItem.getProcessInstanceId()));
		metaData.put(WORKITEM_ID_KEY, Long.toString(workItem.getId()));

		if (metaDataObject != null) {
			if (metaDataObject instanceof Map) {
				LOG.debug("Found metadata map parameter... assigning values to task");
				Map<?, ?> metaDataMap = (Map) metaDataObject;
				for (Entry<?, ?> entry : metaDataMap.entrySet()) {
					if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
						metaData.put((String) entry.getKey(), (String) entry.getValue());
					} else {
						LOG.warn("Found not string key or value in meta data map parameter. Key '{}', value '{}'", entry.getKey(),
								entry.getValue());
					}
				}
			} else {
				LOG.warn("Found metadata parameter but is not instance of map... ignoring");
			}
		}

		if (actorId != null) {
			actorId = checkForUser(actorId);
			LOG.debug(MessageFormat.format("Creating user task {0} assigned to user {1}", taskName, actorId));
			userTaskService.createTaskAssigned(taskName, comment, priority, actorId, metaData);
		} else {
			roleId = checkForRole(roleId);
			LOG.debug(MessageFormat
					.format("Creating user task '{0}' with posible users assigned from role '{1}'", taskName, roleId));
			userTaskService.createTaskUnassigned(taskName, comment, priority, roleId, metaData);
		}

	}

	private String checkForUser(String user) {
		LOG.debug("Checking if provided user '{}' exists in database", user);
		User userModel = userDAO.read(user);
		if (userModel != null) {
			return user;
		} else {
			LOG.debug("The user not found by id, searching by username");
			userModel = userDAO.findByUsername(user);
			if (userModel == null) {
				LOG.error("User not found... throwing exception");
				throw new UnsupportedOperationException(MessageFormat.format(
						"The user [{0}] provided in workflow not found in database", user));
			} else {
				return userModel.getId();
			}
		}
	}

	private String checkForRole(String role) {
		throw new UnsupportedOperationException("Roles not implemented jet");
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		userTaskListener.addWorkItemAborting(workItem.getId());
		try {
			List<UserTask> userTaskList = userTaskService
					.findTasksByMetaDataValue(WORKITEM_ID_KEY, Long.toString(workItem.getId()));
			if (userTaskList.size() == 0) {
				LOG.warn("User task not found when triying to abort");
			} else if (userTaskList.size() > 1) {
				LOG.warn("Wrong number of user task found... expected 1 but found {}. Abort all user task found",
						userTaskList.size());
			}
			for (UserTask userTask : userTaskList) {
				userTaskService.deleteTask(userTask);
			}
			manager.abortWorkItem(workItem.getId());
		} finally {
			userTaskListener.removeWorkItemAborting(workItem.getId());
		}
	}

}
