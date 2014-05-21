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
package org.openeos.jbpm.integration.internal;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.SingleSessionCommandService;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.jpa.processinstance.JPAWorkItemManagerFactory;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.persistence.ProcessPersistenceContextManager;
import org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory;
import org.jbpm.persistence.processinstance.JPASignalManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import org.openeos.jbpm.integration.KnowledgeBaseProvider;

public class KnowledgeManager {

	private static final Logger LOG = LoggerFactory.getLogger(KnowledgeManager.class);

	private ProcessPersistenceContextManager processPersistenceContextManager;
	private KnowledgeIdResolver knowledgeIdResolver;
	private KnowledgeBaseProvider knowledgeBaseProvider;

	public KnowledgeManager(ProcessPersistenceContextManager processPersistenceContextManager,
			KnowledgeIdResolver knowledgeIdResolver, KnowledgeBaseProvider knowledgeBaseProvider) {
		if (processPersistenceContextManager == null || knowledgeIdResolver == null || knowledgeBaseProvider == null) {
			throw new IllegalArgumentException();
		}
		this.processPersistenceContextManager = processPersistenceContextManager;
		this.knowledgeIdResolver = knowledgeIdResolver;
		this.knowledgeBaseProvider = knowledgeBaseProvider;
	}

	public StatefulKnowledgeSession createSession() {
		System.setProperty("jbpm.enable.multi.con", Boolean.toString(true));
		Properties props = new Properties();
		props.put("drools.processInstanceManagerFactory", JPAProcessInstanceManagerFactory.class.getName());
		props.put("drools.workItemManagerFactory", JPAWorkItemManagerFactory.class.getName());
		props.put("drools.processSignalManagerFactory", JPASignalManagerFactory.class.getName());

		KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(props);

		Environment env = KnowledgeBaseFactory.newEnvironment();
		env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, processPersistenceContextManager);

		AnnotationTransactionAspect txAspect = AnnotationTransactionAspect.aspectOf();
		AbstractPlatformTransactionManager txManager = (AbstractPlatformTransactionManager) txAspect.getTransactionManager();
		LOG.debug("Transaction manager: " + txManager.toString());
		TransactionManager tm = new UnoTransactionManager(txManager,
				(UnoProcessPersistenceContext) processPersistenceContextManager.getProcessPersistenceContext());
		env.set(EnvironmentName.TRANSACTION_MANAGER, tm);

		Integer sessionId = knowledgeIdResolver.getKnowledgeSessionId();
		SingleSessionCommandService sscs;
		KnowledgeBase kBase = knowledgeBaseProvider.getKnowledgeBase();
		if (sessionId == null) {
			sscs = new SingleSessionCommandService(kBase, ksconf, env);
			knowledgeIdResolver.saveKnowledgeSessionId(sscs.getSessionId());
		} else {
			sscs = new SingleSessionCommandService(sessionId, kBase, ksconf, env);
		}
		return new CommandBasedStatefulKnowledgeSession(sscs);

	}

}
