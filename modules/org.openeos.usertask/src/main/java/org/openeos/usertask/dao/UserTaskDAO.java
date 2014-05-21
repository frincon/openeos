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
package org.openeos.usertask.dao;

import java.util.List;

import org.openeos.dao.GenericDAO;
import org.openeos.erp.core.model.User;
import org.openeos.usertask.model.UserTask;
import org.openeos.usertask.model.list.TaskStatus;

public interface UserTaskDAO extends GenericDAO<UserTask, String> {

	public List<UserTask> findUserTaskByUserAssigned(TaskStatus status, User userAssigned);

	public List<UserTask> findUserTaskByMetaDataValue(TaskStatus status, String metaDataKey, String metaDataValue);

	public int countUserTaskByUserAssigned(TaskStatus status, User user);

	public int countUserTaskByUserAssigned(TaskStatus status, String userId);

}
