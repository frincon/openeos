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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.persistence.ProcessPersistenceContext;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.jbpm.integration.dao.JbpmProcessInstanceDAO;
import org.openeos.jbpm.integration.model.JbpmProcessInstance;
import org.openeos.jbpm.integration.model.JbpmProcessInstanceEventType;

public class UnoProcessPersistenceContext extends UnoPersistenceContext implements ProcessPersistenceContext {

	private static final String JBPM_PROCESS_INSTANCE_SEQUENCE_ID = "ff8081813f77bc49013f77bc4f800000";

	private JbpmProcessInstanceDAO jbpmProcessInstanceDAO;

	private Map<Long, ProcessInstanceInfo> processInfoMap = new HashMap<Long, ProcessInstanceInfo>();

	public void setJbpmProcessInstanceDAO(JbpmProcessInstanceDAO jbpmProcessInstanceDAO) {
		this.jbpmProcessInstanceDAO = jbpmProcessInstanceDAO;
	}

	@Override
	@Transactional
	public void persist(ProcessInstanceInfo processInstanceInfo) {
		persist(processInstanceInfo, false);
	}

	private void persist(ProcessInstanceInfo processInstanceInfo, boolean notPresistsIfUpdateFail) {
		if (processInstanceInfo.getId() == null) {
			processInstanceInfo.setId(Long.parseLong(getNumerationService().getAndIncrement(JBPM_PROCESS_INSTANCE_SEQUENCE_ID)));
		}
		JbpmProcessInstance jpi = jbpmProcessInstanceDAO.findByJbpmInternalId(processInstanceInfo.getId());
		try {
			processInstanceInfo.update();
		} catch (NullPointerException ex) {
			if (notPresistsIfUpdateFail) {
				return;
			} else {
				throw ex;
			}
		}
		processInfoMap.put(processInstanceInfo.getId(), processInstanceInfo);
		if (jpi == null) {
			jpi = new JbpmProcessInstance();
			copyFromInfo(jpi, processInstanceInfo);
			jbpmProcessInstanceDAO.create(jpi);
		} else {
			copyFromInfo(jpi, processInstanceInfo);
			jbpmProcessInstanceDAO.update(jpi);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ProcessInstanceInfo findProcessInstanceInfo(Long processId) {
		JbpmProcessInstance jpi = jbpmProcessInstanceDAO.findByJbpmInternalId(processId);
		if (jpi != null) {
			ProcessInstanceInfo info = copyFromJbpmProcessInstance(jpi);
			processInfoMap.put(processId, info);
			return info;
		} else {
			return null;
		}
	}

	@Override
	public void remove(ProcessInstanceInfo processInstanceInfo) {
		JbpmProcessInstance jpi = jbpmProcessInstanceDAO.findByJbpmInternalId(processInstanceInfo.getId());
		processInfoMap.remove(processInstanceInfo.getId());
		if (jpi != null) {
			jbpmProcessInstanceDAO.delete(jpi);
		}
	}

	@Override
	public List<Long> getProcessInstancesWaitingForEvent(String type) {
		List<JbpmProcessInstance> jpiList = jbpmProcessInstanceDAO.findByEventType(type);
		List<Long> retList = new ArrayList<Long>();
		for (JbpmProcessInstance jpi : jpiList) {
			retList.add(jpi.getJbpmInternalId());
		}
		return retList;
	}

	private void copyFromInfo(JbpmProcessInstance jpi, ProcessInstanceInfo processInstanceInfo) {
		jpi.setBytes(processInstanceInfo.getProcessInstanceByteArray());
		jpi.setJbpmInternalId(processInstanceInfo.getProcessInstanceId());
		jpi.setLastModificationDate(processInstanceInfo.getLastModificationDate());
		jpi.setLastReadDate(processInstanceInfo.getLastReadDate());
		jpi.setProcessId(processInstanceInfo.getProcessId());
		jpi.setStartDate(processInstanceInfo.getStartDate());
		jpi.setState(processInstanceInfo.getState());
		jpi.getEventTypeList().clear();
		for (String eventType : processInstanceInfo.getEventTypes()) {
			jpi.getEventTypeList().add(new JbpmProcessInstanceEventType(jpi, eventType));
		}
	}

	private ProcessInstanceInfo copyFromJbpmProcessInstance(JbpmProcessInstance jpi) {
		Set<String> set = new HashSet<String>();
		for (JbpmProcessInstanceEventType event : jpi.getEventTypeList()) {
			set.add(event.getEventType());
		}
		return new UnoProcessInstanceInfo(jpi.getJbpmInternalId(), jpi.getProcessId(), jpi.getStartDate(), jpi.getLastReadDate(),
				jpi.getLastModificationDate(), jpi.getState(), jpi.getBytes(), set);
	}

	@Override
	public void saveAll() {
		super.saveAll();
		for (ProcessInstanceInfo pii : processInfoMap.values()) {
			persist(pii, true);
		}
	}

	@Override
	public void clear() {
		super.clear();
		processInfoMap.clear();
	}

}
