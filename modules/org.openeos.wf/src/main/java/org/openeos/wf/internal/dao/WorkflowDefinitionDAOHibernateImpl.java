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
package org.openeos.wf.internal.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.hibernate.GenericDAOHibernateImpl;
import org.openeos.wf.dao.WorkflowDefinitionDAO;
import org.openeos.wf.model.WorkflowDefinition;

public class WorkflowDefinitionDAOHibernateImpl extends GenericDAOHibernateImpl<WorkflowDefinition, String> implements
		WorkflowDefinitionDAO {

	public WorkflowDefinitionDAOHibernateImpl() {
		super(WorkflowDefinition.class);
	}

	@Override
	@Transactional
	public WorkflowDefinition findLastByKey(String key) {
		Criteria crit = getSession().createCriteria(WorkflowDefinition.class);
		crit.add(Restrictions.eq(WorkflowDefinition.PROPERTY_VALUE, key));
		crit.add(Restrictions
				.sqlRestriction("{alias}.version=(select max(version) from wf_definition alias_wf where alias_wf.value={alias}.value)"));
		return (WorkflowDefinition) crit.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<WorkflowDefinition> findByKey(String key) {
		Criteria crit = getSession().createCriteria(WorkflowDefinition.class);
		crit.add(Restrictions.eq(WorkflowDefinition.PROPERTY_VALUE, key));
		crit.addOrder(Order.desc(WorkflowDefinition.PROPERTY_VERSION));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<WorkflowDefinition> findAll() {
		return getSession().createCriteria(WorkflowDefinition.class).list();
	}

}
