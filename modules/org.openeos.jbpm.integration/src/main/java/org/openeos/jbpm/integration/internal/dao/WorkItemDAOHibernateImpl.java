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

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.hibernate.GenericDAOHibernateImpl;
import org.openeos.jbpm.integration.dao.WorkItemDAO;
import org.openeos.jbpm.integration.model.WorkItem;

public class WorkItemDAOHibernateImpl extends GenericDAOHibernateImpl<WorkItem, String> implements WorkItemDAO {

	public WorkItemDAOHibernateImpl() {
		super(WorkItem.class);
	}

	@Override
	@Transactional(readOnly = true)
	public WorkItem findByDroolsInternalId(Long droolsInternalId) {
		Criteria crit = getSession().createCriteria(WorkItem.class);
		crit.add(Restrictions.eq(WorkItem.PROPERTY_DROOLS_INTERNAL_ID, droolsInternalId));
		return (WorkItem) crit.uniqueResult();
	}

	@Override
	@Transactional
	public WorkItem merge(WorkItem workItem) {
		return (WorkItem) getSession().merge(workItem);
	}

}
