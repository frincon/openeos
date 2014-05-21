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
package org.openeos.usertask.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import org.openeos.erp.core.dao.UserDAO;
import org.openeos.erp.core.model.User;
import org.openeos.usertask.UserTask;
import org.openeos.usertask.UserTaskService;
import org.openeos.usertask.dao.UserTaskDAO;
import org.openeos.usertask.model.UserTaskMetaData;
import org.openeos.usertask.model.list.TaskStatus;

public class UserTaskServiceImpl implements UserTaskService {

	private UserDAO userDAO;
	private UserTaskDAO userTaskDAO;
	private UserTaskNotificator userTaskNotificator;

	private class UserTaskImpl implements UserTask {

		private org.openeos.usertask.model.UserTask userTaskModel;
		private Map<String, String> metadata;

		@Override
		public String getAssignedUserId() {
			return userTaskModel.getUser().getId();
		}

		@Override
		public String getAssignedRoleId() {
			return null;
		}

		@Override
		public String getName() {
			return userTaskModel.getName();
		}

		@Override
		public String getDescription() {
			return userTaskModel.getDescription();
		}

		@Override
		public int getPriority() {
			return userTaskModel.getPriority();
		}

		@Override
		public String getId() {
			return userTaskModel.getId();
		}

		@Override
		public String getMetaData(String key) {
			return metadata.get(key);
		}

		@Override
		public Map<String, String> getMetaData() {
			return Collections.unmodifiableMap(metadata);
		}

		@Override
		public TaskStatus getStatus() {
			return userTaskModel.getStatus();
		}

	}

	public UserTaskServiceImpl(UserDAO userDAO, UserTaskDAO userTaskDAO, UserTaskNotificator userTaskNotificator) {
		this.userDAO = userDAO;
		this.userTaskDAO = userTaskDAO;
		this.userTaskNotificator = userTaskNotificator;
	}

	@Override
	@Transactional
	public UserTask createTaskAssigned(String taskName, String taskDescription, int priority, String assignedUserId,
			Map<String, String> metaData) {
		User user = userDAO.read(assignedUserId);
		if (user == null) {
			throw new IllegalArgumentException("The user id not exists");
		}
		org.openeos.usertask.model.UserTask userTaskModel = new org.openeos.usertask.model.UserTask();
		userTaskModel.setName(taskName);
		userTaskModel.setDescription(taskDescription);
		userTaskModel.setPriority(priority);
		userTaskModel.setUser(user);
		userTaskModel.setStatus(TaskStatus.STATUS_ASSIGNED);
		userTaskModel.setMetaData(createUserTaskMetaData(userTaskModel, metaData));
		userTaskDAO.create(userTaskModel);
		UserTask result = wrapModel(userTaskModel);
		userTaskNotificator.notifyCreated(result);
		userTaskNotificator.notifyAssigned(result);
		return result;
	}

	private Set<UserTaskMetaData> createUserTaskMetaData(org.openeos.usertask.model.UserTask model, Map<String, String> metaData) {
		if (metaData == null) {
			return null;
		}
		Set<UserTaskMetaData> result = new HashSet<UserTaskMetaData>(metaData.size());
		for (String key : metaData.keySet()) {
			result.add(new UserTaskMetaData(model, key, metaData.get(key)));
		}
		return result;
	}

	@Override
	public UserTask createTaskUnassigned(String taskName, String taskDescription, int priority, String roleCandidateId,
			Map<String, String> metaData) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public void assignTask(UserTask userTask, String assignedUserId) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public void unassignTask(UserTask userTask) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	@Transactional
	public List<UserTask> findTasksByAssignedUser(String assignedUserId) {
		User user = userDAO.read(assignedUserId);
		if (user == null) {
			throw new IllegalArgumentException("The user id not exists");
		}

		List<org.openeos.usertask.model.UserTask> userTaskList = userTaskDAO.findUserTaskByUserAssigned(
				TaskStatus.STATUS_ASSIGNED, user);
		return wrapList(userTaskList);
	}

	@Override
	@Transactional
	public void deleteTask(UserTask task) {
		org.openeos.usertask.model.UserTask userTask = userTaskDAO.read(task.getId());
		if (userTask == null) {
			throw new IllegalArgumentException("The task is already deleted.");
		}
		userTaskDAO.delete(userTask);
		userTaskNotificator.notifyDeleted(wrapModel(userTask));
	}

	private UserTask wrapModel(org.openeos.usertask.model.UserTask userTaskModel) {
		UserTaskImpl impl = new UserTaskImpl();
		impl.userTaskModel = userTaskModel;
		impl.metadata = createMetaData(userTaskModel);
		return impl;
	}

	private Map<String, String> createMetaData(org.openeos.usertask.model.UserTask userTaskModel) {
		if (userTaskModel.getMetaData() == null) {
			return Collections.emptyMap();
		}
		Map<String, String> result = new HashMap<String, String>(userTaskModel.getMetaData().size());
		for (UserTaskMetaData metaData : userTaskModel.getMetaData()) {
			result.put(metaData.getMetaKey(), metaData.getValue());
		}
		return result;
	}

	private List<UserTask> wrapList(List<org.openeos.usertask.model.UserTask> userTaskList) {
		List<UserTask> result = new ArrayList<UserTask>();
		for (org.openeos.usertask.model.UserTask userTask : userTaskList) {
			result.add(wrapModel(userTask));
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public List<UserTask> findTasksByMetaDataValue(String metaDataKey, String metaDataValue) {
		return wrapList(userTaskDAO.findUserTaskByMetaDataValue(TaskStatus.STATUS_ASSIGNED, metaDataKey, metaDataValue));
	}

	@Override
	public void completeTask(UserTask task) {
		// TODO Check for previous status?
		org.openeos.usertask.model.UserTask userTask = userTaskDAO.read(task.getId());
		userTask.setStatus(TaskStatus.STATUS_COMPLETED);
		userTaskDAO.update(userTask);
		userTaskNotificator.notifyCompleted(wrapModel(userTask));
	}

	@Override
	public int countTasksByAssignedUser(String assignedUserId) {
		return userTaskDAO.countUserTaskByUserAssigned(TaskStatus.STATUS_ASSIGNED, assignedUserId);
	}

}
