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
package org.openeos.usertask.eventadmin;

public interface Constants {

	public static final String PREFIX_TOPIC = "org/openeos/usertask/UserTaskService/";

	public static final String TOPIC_CREATED = PREFIX_TOPIC + "CREATED";
	public static final String TOPIC_DELETED = PREFIX_TOPIC + "DELETED";
	public static final String TOPIC_COMPLETED = PREFIX_TOPIC + "COMPLETED";
	public static final String TOPIC_ASSIGNED = PREFIX_TOPIC + "ASSIGNED";
	public static final String TOPIC_UNASSIGNED = PREFIX_TOPIC + "UNASSIGNED";

	public static final String PROPERTY_TASK_ID = "TASK_ID";
	public static final String PROPERTY_ASSIGNED_USER_ID = "ASSIGNED_USER_ID";

}
