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
package org.openeos.hibernate.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.service.UnknownServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.openeos.hibernate.SessionObserver;

public class SessionFactoryProxyHandler implements InvocationHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryProxyHandler.class);

	// TODO Make better syncronization

	private SessionFactory realSessionFactory;
	private int currentOpenedSessions;

	private SessionFactoryBuilder sessionFactoryBuilder;

	private class SessionProxyHandler implements InvocationHandler {
		private SessionFactory sessionFactory;
		private Session realSession;
		private SessionObserver observer;

		public SessionProxyHandler(SessionFactory sessionFactory, Session realSession, SessionObserver observer) {
			this.sessionFactory = sessionFactory;
			this.realSession = realSession;
			this.observer = observer;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object ret = method.invoke(realSession, args);
			if (method.getName().equals("close")) {
				sessionClosed(sessionFactory);
				if (observer != null) {
					observer.sessionClosed((Session) proxy);
				}
			}
			return ret;
		}

	}

	public SessionFactoryProxyHandler(SessionFactoryBuilder sessionFactoryBuilder) {
		this.sessionFactoryBuilder = sessionFactoryBuilder;
	}

	@Override
	public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		LOG.trace("Method '{}' called to session factory", method.getName());
		if (realSessionFactory == null) {
			buildNewSessionFactory();
		}
		if (method.getName().equals("openSession")) {
			return openSession();
		} else if (method.getName().equals("openStatelessSession")) {
			return openStatelessSession(args);
		} else if (method.getName().equals("getCurrentSession")) {
			return currentSession(proxy);
		} else {
			return method.invoke(realSessionFactory, args);
		}
	}

	private Object currentSession(Object proxy) {
		Object value = TransactionSynchronizationManager.getResource(proxy);
		if (value instanceof Session) {
			return (Session) value;
		} else if (value instanceof SessionHolder) {
			SessionHolder sessionHolder = (SessionHolder) value;
			Session session = sessionHolder.getSession();
			if (TransactionSynchronizationManager.isSynchronizationActive() && !sessionHolder.isSynchronizedWithTransaction()) {
				throw new UnsupportedOperationException();
			}
			return session;
		} else {
			throw new HibernateException("No Session found for current thread");
		}

	}

	private Object openStatelessSession(Object[] args) {
		throw new UnsupportedOperationException();
	}

	public synchronized void invalidateSessionFactory() {
		deprecateActualSessionFactory();
		LOG.debug("Session factory deprecated. Building new one");
		buildNewSessionFactory();
	}

	private void buildNewSessionFactory() {
		this.realSessionFactory = sessionFactoryBuilder.buildSessionFactory();
		LOG.debug("New session factory builded");
	}

	private Object openSession() {
		LOG.debug("Openning new session for actual session factory, currently active open sessions: {}", currentOpenedSessions);
		Session realSession = this.realSessionFactory.openSession();
		currentOpenedSessions++;

		SessionObserver observer = null;
		try {
			observer = ((SessionFactoryImplementor) realSessionFactory).getServiceRegistry().getService(SessionObserver.class);
		} catch (UnknownServiceException e) {
			// Do nothing
		}

		SessionProxyHandler proxyHandler = new SessionProxyHandler(this.realSessionFactory, realSession, observer);
		Session proxy = (Session) Proxy.newProxyInstance(Session.class.getClassLoader(), new Class<?>[] { Session.class,
				SessionImplementor.class }, proxyHandler);

		if (observer != null) {
			observer.sessionCreated(proxy);
		}

		return proxy;

	}

	private synchronized void sessionClosed(SessionFactory sessionFactory) {
		if (sessionFactory == realSessionFactory) {
			currentOpenedSessions--;
			LOG.debug("Hibernate session closed for actual session factory. Sessions remaining: {}", currentOpenedSessions);
			notify();
		} else {
			throw new UnsupportedOperationException();
		}

	}

	private void deprecateActualSessionFactory() {
		if (realSessionFactory == null) {
			return;
		}
		while (currentOpenedSessions > 0) {

			LOG.debug("Deprecating old session but there are {} opened sessions. Waiting to all sessions are closed",
					currentOpenedSessions);
			try {
				wait();
			} catch (InterruptedException e) {
				LOG.warn("Thread interrupted!!!", e);
			}
		}
		LOG.debug("Closing actual session factory.");
		releaseSessionFactory(realSessionFactory);
		this.realSessionFactory = null;
		this.currentOpenedSessions = 0;
	}

	private void releaseSessionFactory(SessionFactory realSessionFactory2) {
		realSessionFactory2.close();
	}

}
