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
package org.openeos.usertask.internal.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.hibernate.GenericDAOHibernateImpl;
import org.openeos.erp.core.model.User;
import org.openeos.usertask.dao.UserTaskDAO;
import org.openeos.usertask.model.UserTask;
import org.openeos.usertask.model.UserTaskMetaData;
import org.openeos.usertask.model.list.TaskStatus;

public class UserTaskDAOHibernateImpl extends GenericDAOHibernateImpl<UserTask, String> implements UserTaskDAO {

	public UserTaskDAOHibernateImpl() {
		super(UserTask.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<UserTask> findUserTaskByUserAssigned(TaskStatus status, User userAssigned) {
		Criteria crit = getSession().createCriteria(UserTask.class);
		crit.add(Restrictions.eq(UserTask.PROPERTY_STATUS, status));
		crit.add(Restrictions.eq(UserTask.PROPERTY_USER, userAssigned));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<UserTask> findUserTaskByMetaDataValue(TaskStatus status, String metaDataKey, String metaDataValue) {
		Criteria crit = getSession().createCriteria(UserTask.class);
		crit.add(Restrictions.eq(UserTask.PROPERTY_STATUS, status));
		crit.createAlias(UserTask.PROPERTY_META_DATA, UserTask.PROPERTY_META_DATA);
		crit.add(Restrictions.eq(UserTask.PROPERTY_META_DATA + "." + UserTaskMetaData.PROPERTY_META_KEY, metaDataKey));
		crit.add(Restrictions.eq(UserTask.PROPERTY_META_DATA + "." + UserTaskMetaData.PROPERTY_VALUE, metaDataValue));
		return crit.list();
	}

	@Override
	@Transactional
	public int countUserTaskByUserAssigned(TaskStatus status, User user) {
		return countUserTaskByUserAssigned(status, user.getId());
	}

	@Override
	@Transactional
	public int countUserTaskByUserAssigned(TaskStatus status, String userId) {
		Criteria crit = getSession().createCriteria(UserTask.class);
		crit.add(Restrictions.eq(UserTask.PROPERTY_STATUS, status));
		crit.add(Restrictions.eq(UserTask.PROPERTY_USER + "." + User.PROPERTY_ID, userId));
		return ((Number) crit.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

}
