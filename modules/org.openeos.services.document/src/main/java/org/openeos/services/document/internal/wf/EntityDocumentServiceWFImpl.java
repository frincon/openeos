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
package org.openeos.services.document.internal.wf;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.document.EntityDocumentService;
import org.openeos.services.document.wf.EntityDocumentServiceWF;
import org.openeos.wf.JavaServiceTaskAware;

public class EntityDocumentServiceWFImpl implements EntityDocumentServiceWF, JavaServiceTaskAware<EntityDocumentServiceWF> {

	private SessionFactory sessionFactory;
	private IDictionaryService dictionaryService;
	private EntityDocumentService entityDocumentService;

	public EntityDocumentServiceWFImpl(SessionFactory sessionFactory, IDictionaryService dictionaryService,
			EntityDocumentService entityDocumentService) {
		this.sessionFactory = sessionFactory;
		this.dictionaryService = dictionaryService;
		this.entityDocumentService = entityDocumentService;
	}

	@Override
	public String getName() {
		return "entityDocumentServiceWF";
	}

	@Override
	public Class<EntityDocumentServiceWF> getServiceInterface() {
		return EntityDocumentServiceWF.class;
	}

	@Override
	@Transactional
	public String generateDocument(String entityClassName, String entityId, String documentId) {
		@SuppressWarnings("unchecked")
		Class<Object> entityClass = (Class<Object>) dictionaryService.getClassDefinition(entityClassName).getClassDefined();
		Object entity = sessionFactory.getCurrentSession().get(entityClass, entityId);
		return entityDocumentService.generateDocument(entityClass, entity, documentId);
	}

}
