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
package org.openeos.wf;

public interface Constants {

	public static final String HEADER_WORKFLOW_DEBUG = "Uno-Workflow-Debug";

	public static final String HEADER_WORKFLOW_HEADER = "Uno-Workflow-Paths";

	public static final String DEPLOYMENT_PREFIX = "org.openeos.wd.extender.";

	public static final String PREFIX_TOPIC = "org/openeos/wf/";

	public static final String DEPLOYMENT_TOPIC = PREFIX_TOPIC + "DEPLOY";

	public static final String UNDEPLOYMENT_TOPIC = PREFIX_TOPIC + "UNDEPLOY";

	public static final String EVENT_DEPLOYMENT_ID = "org.openeos.wd.DEPLOYMENT_ID";

	public static final String LANUCHER_USER_PARAMETER = "launcherUser";

	public static final String PROPERTY_WORKFLOW_ENTITY_ID = "entityId";
	public static final String PROPERTY_WORKFLOW_ENTITY_NAME = "entityName";
	public static final String PROPERTY_WORKFLOW_ENTITY = "entity";

}
