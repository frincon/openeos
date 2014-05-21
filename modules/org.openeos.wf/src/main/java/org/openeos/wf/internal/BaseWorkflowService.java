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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.wf.Deployment;
import org.openeos.wf.DeploymentBuilder;
import org.openeos.wf.WorkflowDefinition;
import org.openeos.wf.WorkflowEngine;
import org.openeos.wf.WorkflowService;
import org.openeos.wf.WorkflowServiceException;

public abstract class BaseWorkflowService implements WorkflowService {

	protected class DeploymentBuilderImpl implements DeploymentBuilder {

		protected boolean duplicateFiltering = false;
		protected List<URL> urlList = new ArrayList<URL>();
		protected String version = null;
		protected String key = null;

		@Override
		public DeploymentBuilder enableDuplicateFiltering() {
			duplicateFiltering = true;
			return this;
		}

		@Override
		public DeploymentBuilder addURL(URL url) {
			urlList.add(url);
			return this;
		}

		@Override
		public DeploymentBuilder version(String version) {
			this.version = version;
			return this;
		}

		@Override
		public DeploymentBuilder key(String key) {
			this.key = key;
			return this;
		}

		@Override
		public Deployment deploy() {
			return deployBuilder(this);
		}

	}

	private WorkflowServiceListenerNotificator notificator;
	private WorkflowEngine workflowEngine;

	public BaseWorkflowService(WorkflowServiceListenerNotificator notificator) {
		this.notificator = notificator;
	}

	@Override
	public DeploymentBuilder createDeployment() {
		return new DeploymentBuilderImpl();
	}

	private Deployment deployBuilder(DeploymentBuilderImpl deploymentBuilderImpl) {
		Deployment deployment = internalDeployBuilder(deploymentBuilderImpl);
		if (notificator != null) {
			notificator.notifyDeploymentAdded(deployment);
		}
		return deployment;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private Deployment internalDeployBuilder(DeploymentBuilderImpl deploymentBuilderImpl) {
		checkDeploymentIsOk(deploymentBuilderImpl);
		Deployment deployment = saveDeployment(deploymentBuilderImpl);
		for (URL url : deploymentBuilderImpl.urlList) {
			saveWorkflowDefinition(deployment, url);
		}
		return deployment;
	}

	protected abstract WorkflowDefinition saveWorkflowDefinition(Deployment deployment, URL url);

	protected abstract Deployment saveDeployment(DeploymentBuilderImpl deploymentBuilderImpl);

	protected abstract void deleteDeployment(String deploymentId);

	protected void checkDeploymentIsOk(DeploymentBuilderImpl deploymentBuilderImpl) {
		if (deploymentBuilderImpl.key == null || deploymentBuilderImpl.key.trim().length() == 0) {
			throw new WorkflowServiceException("The key has no value");
		}
		if (deploymentBuilderImpl.urlList.isEmpty()) {
			throw new WorkflowServiceException("The url list is empty");
		}
	}

	@Override
	public void revertDeployment(String deploymentId) {
		deleteDeployment(deploymentId);
		if (notificator != null) {
			notificator.notifyDeploymentRemoved(deploymentId);
		}
	}

	@Override
	public String startProcess(String processId) {
		return workflowEngine.startProcess(processId);
	}

	@Override
	public String startProcess(String processId, Map<String, Object> parameters) {
		return workflowEngine.startProcess(processId, parameters);
	}

	@Override
	public void abortProcessInstance(String processInstanceId) {
		workflowEngine.abortProcessInstance(processInstanceId);
	}

	public void bindWorkflowEngine(WorkflowEngine workflowEngine) {
		this.workflowEngine = workflowEngine;
	}

	public void unbindWorkflowEngine(WorkflowEngine workflowEngine) {
		this.workflowEngine = null;
	}

}
