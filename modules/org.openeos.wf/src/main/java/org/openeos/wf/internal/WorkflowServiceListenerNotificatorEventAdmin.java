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
package org.openeos.wf.internal;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import org.openeos.wf.Constants;
import org.openeos.wf.Deployment;

public class WorkflowServiceListenerNotificatorEventAdmin implements WorkflowServiceListenerNotificator, Constants {

	private EventAdmin eventAdmin;

	public WorkflowServiceListenerNotificatorEventAdmin(EventAdmin eventAdmin) {
		if (eventAdmin == null) {
			throw new IllegalArgumentException();
		}
		this.eventAdmin = eventAdmin;
	}

	@Override
	public void notifyDeploymentAdded(Deployment deployment) {
		Map<String, Object> mapProperties = new HashMap<String, Object>();
		mapProperties.put(EVENT_DEPLOYMENT_ID, deployment.getId());
		Event event = new Event(DEPLOYMENT_TOPIC, mapProperties);
		eventAdmin.sendEvent(event);
	}

	@Override
	public void notifyDeploymentRemoved(String deploymentId) {
		Map<String, Object> mapProperties = new HashMap<String, Object>();
		mapProperties.put(EVENT_DEPLOYMENT_ID, deploymentId);
		Event event = new Event(UNDEPLOYMENT_TOPIC, mapProperties);
		eventAdmin.sendEvent(event);
	}

}
