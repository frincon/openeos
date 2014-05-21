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
package org.openeos.wf.jbpm.internal.events;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.runtime.process.NodeInstance;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.wf.Constants;

public class EntityManagerEventListener implements ProcessEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(EntityManagerEventListener.class);

	private SessionFactory sessionFactory;

	public EntityManagerEventListener(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void beforeProcessStarted(ProcessStartedEvent event) {
	}

	@Override
	public void afterProcessStarted(ProcessStartedEvent event) {
	}

	@Override
	public void beforeProcessCompleted(ProcessCompletedEvent event) {
	}

	@Override
	public void afterProcessCompleted(ProcessCompletedEvent event) {
	}

	@Override
	public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
		LOG.debug("Node triggered, checking if the process has entity to establish");
		Object entityId = event.getNodeInstance().getVariable(Constants.PROPERTY_WORKFLOW_ENTITY_ID);
		Object entityName = event.getNodeInstance().getVariable(Constants.PROPERTY_WORKFLOW_ENTITY_NAME);
		if (entityId != null && entityId instanceof String && entityName != null && entityName instanceof String) {
			LOG.debug("Entity name: '{}' Entity id: '{}'", entityName, entityId);
			addEntityVariable(event.getNodeInstance(), (String) entityName, (String) entityId);
		}
	}

	@Transactional(readOnly = true)
	private void addEntityVariable(NodeInstance nodeInstance, String entityName, String entityId) {
		Session session = sessionFactory.getCurrentSession();
		Object entity = session.get(entityName, entityId);
		if (entity == null) {
			LOG.warn("Entity with name '{}' and id '{}' not found in database", entityName, entityId);
		} else {
			LOG.debug("Adding entity variable to process.");
			nodeInstance.setVariable(Constants.PROPERTY_WORKFLOW_ENTITY, entity);
		}
	}

	@Override
	public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
		LOG.debug("After node triggered, checking if the process has entity variable to remove it.");
		Object entityId = event.getNodeInstance().getVariable(Constants.PROPERTY_WORKFLOW_ENTITY_ID);
		Object entityName = event.getNodeInstance().getVariable(Constants.PROPERTY_WORKFLOW_ENTITY_NAME);
		if (entityId != null && entityId instanceof String && entityName != null && entityName instanceof String) {
			LOG.debug("Removing entity variable of the process with entity name '{}' and entity id '{}'", entityName, entityId);
			event.getNodeInstance().setVariable(Constants.PROPERTY_WORKFLOW_ENTITY, null);
		}
	}

	@Override
	public void beforeNodeLeft(ProcessNodeLeftEvent event) {

	}

	@Override
	public void afterNodeLeft(ProcessNodeLeftEvent event) {

	}

	@Override
	public void beforeVariableChanged(ProcessVariableChangedEvent event) {

	}

	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {

	}

}
