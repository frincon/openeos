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
package org.openeos.jbpm.integration.internal.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.hibernate.GenericDAOHibernateImpl;
import org.openeos.jbpm.integration.dao.JbpmProcessInstanceDAO;
import org.openeos.jbpm.integration.model.JbpmProcessInstance;
import org.openeos.jbpm.integration.model.JbpmProcessInstanceEventType;

public class JbpmProcessInstanceDAOHibernateImpl extends GenericDAOHibernateImpl<JbpmProcessInstance, String> implements
		JbpmProcessInstanceDAO {

	public JbpmProcessInstanceDAOHibernateImpl() {
		super(JbpmProcessInstance.class);
	}

	@Override
	@Transactional(readOnly = true)
	public JbpmProcessInstance findByJbpmInternalId(Long jbpmInternalId) {
		Criteria crit = getSession().createCriteria(JbpmProcessInstance.class);
		crit.add(Restrictions.eq(JbpmProcessInstance.PROPERTY_JBPM_INTERNAL_ID, jbpmInternalId));
		return (JbpmProcessInstance) crit.uniqueResult();
	}

	@Override
	@Transactional(readOnly = true)
	public List<JbpmProcessInstance> findByEventType(String eventType) {
		Criteria crit = getSession().createCriteria(JbpmProcessInstance.class);
		crit.createCriteria(JbpmProcessInstance.PROPERTY_EVENT_TYPE_LIST).add(
				Restrictions.eq(JbpmProcessInstanceEventType.PROPERTY_EVENT_TYPE, eventType));

		return Collections.checkedList(crit.list(), JbpmProcessInstance.class);
	}
}
