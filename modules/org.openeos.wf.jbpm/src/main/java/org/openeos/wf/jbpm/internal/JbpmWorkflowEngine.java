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
package org.openeos.wf.jbpm.internal;

import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.wf.WorkflowEngine;

public class JbpmWorkflowEngine implements WorkflowEngine {

	private static final Logger LOG = LoggerFactory.getLogger(JbpmWorkflowEngine.class);

	private StatefulKnowledgeSession session;

	public JbpmWorkflowEngine(StatefulKnowledgeSession session, ProcessListenerManager processListenerManager) {
		this.session = session;
		session.addEventListener(processListenerManager);
	}

	@Override
	public String startProcess(String processId) {
		checkSessionOrThrow();
		ProcessInstance instance = session.startProcess(processId);
		return Long.toString(instance.getId());
	}

	@Override
	public String startProcess(String processId, Map<String, Object> parameters) {
		checkSessionOrThrow();
		ProcessInstance instance = session.startProcess(processId, parameters);
		return Long.toString(instance.getId());
	}

	private void checkSessionOrThrow() {
		if (session == null) {
			throw new IllegalStateException("Thre isn't no stateful session binding yet.");
		}
	}

	@Override
	public void abortProcessInstance(String processInstanceId) {
		session.abortProcessInstance(Long.parseLong(processInstanceId));
	}

}
