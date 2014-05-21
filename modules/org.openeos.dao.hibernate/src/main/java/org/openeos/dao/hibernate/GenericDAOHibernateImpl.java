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
package org.openeos.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.GenericDAO;
import org.openeos.dao.hibernate.internal.SessionFactoryManager;

public abstract class GenericDAOHibernateImpl<T, PK extends Serializable> implements GenericDAO<T, PK> {

	private Class<T> type;

	public GenericDAOHibernateImpl(Class<T> type) {
		this.type = type;
	}

	@Override
	@Transactional
	public PK create(T o) {
		return (PK) getSession().save(o);
	}

	@Override
	@Transactional(readOnly = true)
	public T read(PK id) {
		return (T) getSession().get(type, id);
	}

	@Override
	@Transactional
	public void update(T o) {
		getSession().saveOrUpdate(o);
	}

	@Override
	@Transactional
	public void delete(T o) {
		getSession().delete(o);
	}

	@Override
	@Transactional
	public void refresh(T instance) {
		getSession().refresh(instance);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<T> findAll() {
		return getSession().createCriteria(type).list();
	}

	protected Session getSession() {
		return SessionFactoryManager.getSessionFactory().getCurrentSession();
	}
}
