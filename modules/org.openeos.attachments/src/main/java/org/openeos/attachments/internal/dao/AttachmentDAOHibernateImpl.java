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
package org.openeos.attachments.internal.dao;

import java.io.InputStream;
import java.sql.Blob;
import java.util.List;
import java.util.Stack;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.attachments.dao.AttachmentDAO;
import org.openeos.attachments.model.Attachment;
import org.openeos.dao.hibernate.GenericDAOHibernateImpl;

public class AttachmentDAOHibernateImpl extends GenericDAOHibernateImpl<Attachment, String> implements AttachmentDAO {

	private Stack<ClassLoader> classLoaderStack = new Stack<ClassLoader>();

	public AttachmentDAOHibernateImpl() {
		super(Attachment.class);
	}

	@Override
	@Transactional
	public void setContent(Attachment attachment, InputStream inputStream) {
		replaceClassLoader(this.getClass().getClassLoader());
		Blob blob = AttachmentUtils.createBlob(getSession().getLobHelper(), inputStream);
		attachment.setContent(blob);
		restoreClassLoader();
	}

	@Override
	@Transactional
	public Attachment read(String id) {
		replaceClassLoader(this.getClass().getClassLoader());
		Attachment result = super.read(id);
		restoreClassLoader();
		return result;
	}

	@Override
	@Transactional
	public void update(Attachment o) {
		replaceClassLoader(this.getClass().getClassLoader());
		super.update(o);
		restoreClassLoader();
	}

	@Override
	@Transactional
	public void refresh(Attachment instance) {
		replaceClassLoader(this.getClass().getClassLoader());
		super.refresh(instance);
		restoreClassLoader();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public <T> List<Attachment> findByEntity(Class<T> entityClass, String entityId) {
		replaceClassLoader(this.getClass().getClassLoader());
		Criteria crit = getSession().createCriteria(Attachment.class);
		crit.add(Restrictions.eq(Attachment.PROPERTY_ENTITY_CLASS, entityClass.getName()));
		crit.add(Restrictions.eq(Attachment.PROPERTY_ENTITY_ID, entityId));
		crit.addOrder(Order.asc(Attachment.PROPERTY_CREATION_DATE));
		List<Attachment> result = crit.list();
		restoreClassLoader();
		return result;
	}

	@Override
	public String create(Attachment o) {
		String result = super.create(o);
		// Make flush to read streams
		getSession().flush();
		return result;
	}

	private void replaceClassLoader(ClassLoader loader) {
		classLoaderStack.push(Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(loader);
	}

	private void restoreClassLoader() {
		Thread.currentThread().setContextClassLoader(classLoaderStack.pop());
	}

}
