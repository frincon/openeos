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

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.erp.core.INameValue;
import org.openeos.erp.core.dao.NameValueFinder;

public class NameValueFinderHibernateImpl implements NameValueFinder {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	@Transactional(readOnly = true)
	public INameValue findByValue(Class<INameValue> searchClass, String value) {
		Object result = sessionFactory.getCurrentSession().createCriteria(searchClass)
				.add(Restrictions.eq(INameValue.PROPERTY_VALUE, value)).uniqueResult();
		return (INameValue) result;
	}

}
