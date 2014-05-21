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
package org.openeos.services.ui.vaadin.internal;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.openeos.services.ui.vaadin.ApplicationLifecycleListener;
import org.openeos.vaadin.main.IUnoVaadinApplication;

public class OpenSessionInViewListener implements ApplicationLifecycleListener {

	private static final Logger LOG = LoggerFactory.getLogger(OpenSessionInViewListener.class);

	private SessionFactory sessionFactory;

	private ThreadLocal<Session> tlSession = new ThreadLocal<Session>();

	public OpenSessionInViewListener(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void onInit(IUnoVaadinApplication application) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClose(IUnoVaadinApplication application) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTransactionStart(IUnoVaadinApplication application) {
		if (sessionFactory != null) {
			if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
				LOG.debug("Participating in existing open session.");
			} else {
				LOG.debug("Opening session in View...");
				Session session = SessionFactoryUtils.openSession(sessionFactory);
				session.setFlushMode(FlushMode.MANUAL);
				TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
				tlSession.set(session);
			}
		}
	}

	@Override
	public void onTransactionEnd(IUnoVaadinApplication application) {
		if (tlSession.get() != null) {
			LOG.debug("Open session in view is binding... closing it.");
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
			tlSession.set(null);
		}
	}

}
