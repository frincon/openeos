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
package org.openeos.usertask.internal.eventadmin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.usertask.UserTask;
import org.openeos.usertask.eventadmin.Constants;
import org.openeos.usertask.internal.UserTaskNotificator;

public class UserTaskNotifierEventAdmin implements UserTaskNotificator, Constants {

	private static final Logger LOG = LoggerFactory.getLogger(UserTaskNotifierEventAdmin.class);

	private EventAdmin eventAdmin;

	public UserTaskNotifierEventAdmin(EventAdmin eventAdmin) {
		if (eventAdmin == null) {
			throw new IllegalArgumentException();
		}
		this.eventAdmin = eventAdmin;
	}

	private Map<String, Object> createProperties(UserTask task) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(PROPERTY_TASK_ID, task.getId());
		result.put(PROPERTY_ASSIGNED_USER_ID, task.getAssignedUserId());
		for (Entry<String, String> entry : task.getMetaData().entrySet()) {
			if (result.containsKey(entry.getKey())) {
				LOG.warn(
						"When creating properties for user task event, found metadata with key conflict with another key of the event. The metadata with key '{}' will not be included",
						entry.getKey());
			} else {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	@Override
	public void notifyDeleted(UserTask task) {
		Map<String, Object> properties = createProperties(task);
		eventAdmin.postEvent(new Event(TOPIC_DELETED, properties));
	}

	@Override
	public void notifyAssigned(UserTask task) {
		Map<String, Object> properties = createProperties(task);
		eventAdmin.postEvent(new Event(TOPIC_ASSIGNED, properties));
	}

	@Override
	public void notifyUnassigned(UserTask task, String oldAssignedUserId) {
		Map<String, Object> properties = createProperties(task);
		properties.put(PROPERTY_ASSIGNED_USER_ID, oldAssignedUserId);
		eventAdmin.postEvent(new Event(TOPIC_DELETED, properties));
	}

	@Override
	public void notifyCreated(UserTask task) {
		Map<String, Object> properties = createProperties(task);
		eventAdmin.postEvent(new Event(TOPIC_CREATED, properties));
	}

	@Override
	public void notifyCompleted(UserTask task) {
		Map<String, Object> properties = createProperties(task);
		eventAdmin.postEvent(new Event(TOPIC_COMPLETED, properties));
	}

}
