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
package org.openeos.usertask;

import java.util.List;
import java.util.Map;

public interface UserTaskService {

	UserTask createTaskAssigned(String taskName, String taskDescription, int priority, String assignedUserId,
			Map<String, String> metaData);

	UserTask createTaskUnassigned(String taskName, String taskDescription, int priority, String roleCandidateId,
			Map<String, String> metaData);

	void assignTask(UserTask userTask, String assignedUserId);

	void unassignTask(UserTask userTask);

	/**
	 * Find tasks assigned to user in status STATUS_ASSIGNED
	 * 
	 * @param assignedUserId
	 *            The user id to search assigned tasks
	 * @return List of user tasks assigned to provided user in status
	 *         STATUS_ASSIGNED
	 */
	List<UserTask> findTasksByAssignedUser(String assignedUserId);

	/**
	 * Find tasks by meta data value in status STATUS_ASSIGNED
	 * 
	 * @param metaDataKey
	 *            The key of metadata to search
	 * @param metaDataValue
	 *            The value of metadata to search
	 * @return List of user tasks with the meta data provided values in status
	 *         STATUS_ASSIGNED
	 */
	List<UserTask> findTasksByMetaDataValue(String metaDataKey, String metaDataValue);

	void deleteTask(UserTask task);

	void completeTask(UserTask task);

	int countTasksByAssignedUser(String assignedUserId);

}
