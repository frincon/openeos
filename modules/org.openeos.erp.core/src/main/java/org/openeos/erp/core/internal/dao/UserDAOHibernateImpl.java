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
package org.openeos.erp.core.internal.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.hibernate.GenericDAOHibernateImpl;
import org.openeos.erp.core.dao.UserDAO;
import org.openeos.erp.core.model.User;

public class UserDAOHibernateImpl extends GenericDAOHibernateImpl<User, String> implements UserDAO {

	public UserDAOHibernateImpl() {
		super(User.class);
	}

	@Override
	@Transactional
	public User findByUsername(String username) {
		Criteria crit = getSession().createCriteria(User.class);
		crit.add(Restrictions.eq(User.PROPERTY_USERNAME, username));
		return (User) crit.uniqueResult();
	}

	@Override
	@Transactional
	public User findByUsernameLockedState(String username, boolean isLocked) {
		Criteria crit = getSession().createCriteria(User.class);
		crit.add(Restrictions.eq(User.PROPERTY_USERNAME, username));
		crit.add(Restrictions.eq(User.PROPERTY_LOCKED, isLocked));
		return (User) crit.uniqueResult();
	}

}
