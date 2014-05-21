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
package org.openeos.jbpm.integration.internal;

import java.util.HashMap;
import java.util.Map;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.jbpm.integration.dao.DroolsSessionDAO;
import org.openeos.jbpm.integration.dao.WorkItemDAO;
import org.openeos.jbpm.integration.model.DroolsSession;
import org.openeos.jbpm.integration.model.WorkItem;
import org.openeos.numeration.NumerationService;

public class UnoPersistenceContext implements PersistenceContext {

	private static final String DROOLS_SESSION_SEQUENCE_ID = "ff8081813f77bc49013f77bc507a0001";
	private static final String DROOLS_WORK_ITEM_SEQUENCE_ID = "ff8081813f77bc49013f77bc507b0005";

	private Map<Integer, SessionInfo> sessionInfoMap = new HashMap<Integer, SessionInfo>();
	private Map<Long, WorkItemInfo> workItemInfoMap = new HashMap<Long, WorkItemInfo>();

	private DroolsSessionDAO droolsSessionDAO;
	private WorkItemDAO workItemDAO;
	private NumerationService numerationService;

	protected NumerationService getNumerationService() {
		return numerationService;
	}

	public void setNumerationService(NumerationService numerationService) {
		this.numerationService = numerationService;
	}

	public void setDroolsSessionDAO(DroolsSessionDAO droolsSessionDAO) {
		this.droolsSessionDAO = droolsSessionDAO;
	}

	public void setWorkItemDAO(WorkItemDAO workItemDAO) {
		this.workItemDAO = workItemDAO;
	}

	@Override
	@Transactional
	public void persist(SessionInfo entity) {
		if (entity.getId() == null) {
			entity.setId(Integer.parseInt(getNumerationService().getAndIncrement(DROOLS_SESSION_SEQUENCE_ID)));
		}
		DroolsSession ds = droolsSessionDAO.findByDroolsInternalId(entity.getId());
		entity.update();
		sessionInfoMap.put(entity.getId(), entity);
		if (ds == null) {
			ds = new DroolsSession();
			copyFromInfo(ds, entity);
			droolsSessionDAO.create(ds);
		} else {
			copyFromInfo(ds, entity);
			droolsSessionDAO.update(ds);
		}

	}

	private void copyFromInfo(DroolsSession ds, SessionInfo entity) {
		ds.setBytes(entity.getData());
		ds.setDroolsInternalId(entity.getId());
		ds.setLastModificationDate(entity.getLastModificationDate());
		ds.setStartDate(entity.getStartDate());
	}

	private void copyFromInfo(WorkItem wi, WorkItemInfo workItemInfo) {
		wi.setBytes(workItemInfo.getWorkItemByteArray());
		wi.setCreationDate(workItemInfo.getCreationDate());
		wi.setDroolsInternalId(workItemInfo.getId());
		wi.setName(workItemInfo.getName());
		wi.setProcessInstanceId(workItemInfo.getProcessInstanceId());
		wi.setState(workItemInfo.getState());
	}

	@Override
	public SessionInfo findSessionInfo(Integer id) {
		DroolsSession jpi = droolsSessionDAO.findByDroolsInternalId(id);
		if (jpi != null) {
			SessionInfo info = copyFromDroolsSession(jpi);
			sessionInfoMap.put(info.getId(), info);
			return info;
		} else {
			return null;
		}
	}

	private SessionInfo copyFromDroolsSession(DroolsSession ds) {
		return new UnoSessionInfo(ds.getDroolsInternalId(), ds.getStartDate(), ds.getLastModificationDate(), ds.getBytes());
	}

	private WorkItemInfo copyFromWorkItem(WorkItem wi) {
		return new UnoWorkItemInfo(wi.getDroolsInternalId(), wi.getName(), wi.getCreationDate(), wi.getProcessInstanceId(),
				wi.getState(), wi.getBytes());
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public void joinTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void persist(WorkItemInfo workItemInfo) {
		if (workItemInfo.getId() == null) {
			workItemInfo.setId(Long.parseLong(getNumerationService().getAndIncrement(DROOLS_WORK_ITEM_SEQUENCE_ID)));
		}
		WorkItem wi = workItemDAO.findByDroolsInternalId(workItemInfo.getId());
		workItemInfo.update();
		workItemInfoMap.put(workItemInfo.getId(), workItemInfo);
		if (wi == null) {
			wi = new WorkItem();
			copyFromInfo(wi, workItemInfo);
			workItemDAO.create(wi);
		} else {
			copyFromInfo(wi, workItemInfo);
			workItemDAO.update(wi);
		}

	}

	@Override
	public WorkItemInfo findWorkItemInfo(Long id) {
		WorkItem wi = workItemDAO.findByDroolsInternalId(id);
		if (wi != null) {
			WorkItemInfo info = copyFromWorkItem(wi);
			workItemInfoMap.put(info.getId(), info);
			return info;
		} else {
			return null;
		}
	}

	@Override
	public void remove(WorkItemInfo workItemInfo) {
		workItemInfoMap.remove(workItemInfo.getId());
		WorkItem workItem = workItemDAO.findByDroolsInternalId(workItemInfo.getId());
		if (workItem != null) {
			workItemDAO.delete(workItem);
		}
	}

	@Override
	public WorkItemInfo merge(WorkItemInfo workItemInfo) {
		workItemInfoMap.remove(workItemInfo.getId());
		WorkItem wi = new WorkItem();
		wi.setId(workItemDAO.findByDroolsInternalId(workItemInfo.getId()).getId());
		copyFromInfo(wi, workItemInfo);
		WorkItem wi2 = workItemDAO.merge(wi);
		WorkItemInfo workItemInfo2 = copyFromWorkItem(wi2);
		workItemInfoMap.put(workItemInfo2.getId(), workItemInfo);
		return workItemInfo2;
	}

	public void saveAll() {
		for (SessionInfo sesInfo : sessionInfoMap.values()) {
			persist(sesInfo);
		}
		for (WorkItemInfo workItemInfo : workItemInfoMap.values()) {
			persist(workItemInfo);
		}
	}

	public void clear() {
		sessionInfoMap.clear();
		workItemInfoMap.clear();
	}
}
