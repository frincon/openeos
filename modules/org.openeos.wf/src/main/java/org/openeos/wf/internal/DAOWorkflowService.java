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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.numeration.NumerationService;
import org.openeos.wf.Deployment;
import org.openeos.wf.WorkflowServiceException;
import org.openeos.wf.dao.DeploymentDAO;
import org.openeos.wf.dao.WorkflowDefinitionDAO;
import org.openeos.wf.model.WorkflowDefinition;
import org.openeos.wf.model.WorkflowDefinitionContent;

public class DAOWorkflowService extends BaseWorkflowService {

	private static final String NUMERATION_WORKFLOW_VERSION_SEQ_ID = "4b02d8da2a0842ea85296f7b4d73c585";

	private static final Logger LOG = LoggerFactory.getLogger(DAOWorkflowService.class);

	private DeploymentDAO deploymentDAO;
	private WorkflowDefinitionDAO workflowDefinitionDAO;
	private NumerationService numerationService;
	private WorkflowDefinitionValuesExtractor valuesExtractor;

	private class DeploymentModelBasedImpl implements Deployment {

		private org.openeos.wf.model.Deployment deployment;

		private DeploymentModelBasedImpl(org.openeos.wf.model.Deployment deployment) {
			this.deployment = deployment;
		}

		@Override
		public String getId() {
			return deployment.getId();
		}

		@Override
		public String getVersion() {
			return deployment.getVersion();
		}

		@Override
		public Date getDeploymentDate() {
			return deployment.getDeploymentdate();
		}

		@Override
		public String getKey() {
			return deployment.getValue();
		}

	}

	private class WorkflowDefinitionModelBasedImpl implements org.openeos.wf.WorkflowDefinition {

		private WorkflowDefinition definition;

		private WorkflowDefinitionModelBasedImpl(WorkflowDefinition definition) {
			this.definition = definition;
		}

		@Override
		public String getId() {
			return definition.getId();
		}

		@Override
		public String getKey() {
			return definition.getValue();
		}

		@Override
		public String getName() {
			return definition.getName();
		}

		@Override
		public String getVersion() {
			return definition.getVersion();
		}

		@Override
		public String getCheckSum() {
			return definition.getChecksum();
		}

		@Override
		public String getDeploymemtId() {
			return definition.getDeployment().getId();
		}

		@Override
		public boolean isLast() {
			throw new UnsupportedOperationException();
		}

		@Override
		@Transactional
		public byte[] getContent() {
			// Is transactional because lazy loading
			return definition.getWorkflowDefinitionContent().getContent();
		}

	}

	public DAOWorkflowService(WorkflowServiceListenerNotificator notificator, DeploymentDAO deploymentDAO,
			WorkflowDefinitionDAO workflowDefinitionDAO, NumerationService numerationService,
			WorkflowDefinitionValuesExtractor valuesExtractor) {
		super(notificator);
		if (deploymentDAO == null || workflowDefinitionDAO == null || numerationService == null || valuesExtractor == null) {
			throw new IllegalArgumentException();
		}
		this.deploymentDAO = deploymentDAO;
		this.workflowDefinitionDAO = workflowDefinitionDAO;
		this.numerationService = numerationService;
		this.valuesExtractor = valuesExtractor;
	}

	@Override
	@Transactional
	protected org.openeos.wf.WorkflowDefinition saveWorkflowDefinition(Deployment deployment, URL url) {
		WorkflowDefinition def = new WorkflowDefinition();
		def.setDeployment(deploymentDAO.read(deployment.getId()));
		byte[] content = null;
		try (InputStream stream = url.openStream()) {
			content = IOUtils.toByteArray(stream);
		} catch (IOException e) {
			LOG.error("An error has occurred while trying to read content from url workflow definition resource", e);
			throw new WorkflowServiceException(e);
		}
		WorkflowDefinitionContent defContent = new WorkflowDefinitionContent();
		defContent.setContent(content);
		defContent.setWorkflowDefinition(def);
		def.setWorkflowDefinitionContent(defContent);
		extractValues(def, content);
		def.setChecksum(DigestUtils.shaHex(content));
		def.setVersion(numerationService.getAndIncrement(NUMERATION_WORKFLOW_VERSION_SEQ_ID, def));
		workflowDefinitionDAO.create(def);
		return new WorkflowDefinitionModelBasedImpl(def);
	}

	private void extractValues(WorkflowDefinition def, byte[] content) {
		valuesExtractor.extractValues(def, content);
	}

	@Override
	@Transactional
	protected Deployment saveDeployment(DeploymentBuilderImpl deploymentBuilderImpl) {
		org.openeos.wf.model.Deployment modelDeployment = new org.openeos.wf.model.Deployment();
		modelDeployment.setDeploymentdate(new Date());
		modelDeployment.setValue(deploymentBuilderImpl.key);
		modelDeployment.setVersion(deploymentBuilderImpl.version);
		deploymentDAO.create(modelDeployment);
		return new DeploymentModelBasedImpl(modelDeployment);
	}

	@Override
	public Deployment getDeployment(String id) {
		org.openeos.wf.model.Deployment dep = deploymentDAO.read(id);
		if (dep == null) {
			return null;
		}
		return new DeploymentModelBasedImpl(dep);
	}

	@Override
	@Transactional
	protected void deleteDeployment(String deploymentId) {
		org.openeos.wf.model.Deployment dep = deploymentDAO.read(deploymentId);
		for (WorkflowDefinition def : dep.getWorkflowDefinitions()) {
			workflowDefinitionDAO.delete(def);
		}
		deploymentDAO.delete(dep);
	}

	@Override
	public org.openeos.wf.WorkflowDefinition getLastWorkflowDefinitionByKey(String key) {
		WorkflowDefinition model = workflowDefinitionDAO.findLastByKey(key);
		if (model != null) {
			return new WorkflowDefinitionModelBasedImpl(model);
		}
		return null;
	}

	@Override
	public org.openeos.wf.WorkflowDefinition getWorkflowDefinitionById(String id) {
		WorkflowDefinition model = workflowDefinitionDAO.read(id);
		if (model != null) {
			return new WorkflowDefinitionModelBasedImpl(model);
		}
		return null;
	}

	@Override
	public List<org.openeos.wf.WorkflowDefinition> getWorkflowDefinitionListByKey(String key) {
		List<WorkflowDefinition> modelList = workflowDefinitionDAO.findByKey(key);
		if (modelList.isEmpty()) {
			return Collections.emptyList();
		} else {
			ArrayList<org.openeos.wf.WorkflowDefinition> result = new ArrayList<org.openeos.wf.WorkflowDefinition>(
					modelList.size());
			// TODO This is not better way
			for (WorkflowDefinition model : modelList) {
				result.add(new WorkflowDefinitionModelBasedImpl(model));
			}
			return Collections.unmodifiableList(result);
		}
	}

	@Override
	public Deployment getDeployment(String key, String version) {
		org.openeos.wf.model.Deployment model = deploymentDAO.findDeploymentByValueVersion(key, version);
		if (model != null) {
			return new DeploymentModelBasedImpl(model);
		}
		return null;
	}

	@Override
	public List<org.openeos.wf.WorkflowDefinition> getAllWorkflowDefinition() {
		List<WorkflowDefinition> all = workflowDefinitionDAO.findAll();
		List<org.openeos.wf.WorkflowDefinition> result = new ArrayList<org.openeos.wf.WorkflowDefinition>(all.size());
		for (WorkflowDefinition def : all) {
			result.add(new WorkflowDefinitionModelBasedImpl(def));
		}
		return result;
	}

}
