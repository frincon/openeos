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
package org.openeos.erp.audit;

import java.util.Calendar;

import org.hibernate.SessionFactory;

import org.openeos.dao.SaveOrUpdateEvent;
import org.openeos.dao.SaveOrUpdateListener;
import org.openeos.dao.UserException;
import org.openeos.erp.audit.AuditAspect.Auditable;

public class AuditPostSaveEventListener implements SaveOrUpdateListener<AuditAspect.Auditable> {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void beforeSaveOrUpdate(SaveOrUpdateEvent<? extends Auditable> event) throws UserException {
		// Nothing to do
	}

	@Override
	public void afterSaveOrUpdate(SaveOrUpdateEvent<? extends Auditable> event) {
		/*
		AuditAspect.Auditable auditable = event.getEntity();
		Audit audit = auditable.getAudit();
		if (audit == null) {
			audit = new Audit();
			audit.setCreated(Calendar.getInstance());
			auditable.setAudit(audit);
			audit.setId((String) event.getResultId());
		}
		audit.setUpdated(Calendar.getInstance());
		sessionFactory.getCurrentSession().saveOrUpdate(audit);
		*/
	}

}
