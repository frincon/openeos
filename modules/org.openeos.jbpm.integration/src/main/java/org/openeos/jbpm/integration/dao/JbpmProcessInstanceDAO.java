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
package org.openeos.jbpm.integration.dao;

import java.util.List;

import org.openeos.dao.GenericDAO;
import org.openeos.jbpm.integration.model.JbpmProcessInstance;

public interface JbpmProcessInstanceDAO extends GenericDAO<JbpmProcessInstance, String> {

	public JbpmProcessInstance findByJbpmInternalId(Long jbpmInternalId);
	
	public List<JbpmProcessInstance> findByEventType(String eventType);
}
