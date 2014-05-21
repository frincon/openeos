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
package org.openeos.services.document.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.document.EntityDocument;
import org.openeos.services.document.EntityDocumentService;
import org.openeos.services.document.spi.EntityDocumentProvider;
import org.openeos.services.document.spi.EntityDocumentProviderResult;

public class EntityDocumentServiceImpl implements EntityDocumentService {

	private static final Logger LOG = LoggerFactory.getLogger(EntityDocumentServiceImpl.class);

	private BundleContext bc;
	private EntityDocumentServiceBackend backend;
	private IDictionaryService dictionaryService;

	private ServiceTracker<EntityDocumentProvider, EntityDocumentProvider> providersTracker;

	public EntityDocumentServiceImpl(BundleContext bc, EntityDocumentServiceBackend backend, IDictionaryService dictionaryService) {
		this.bc = bc;
		this.backend = backend;
		this.dictionaryService = dictionaryService;
	}

	public void init() {
		providersTracker = new ServiceTracker<EntityDocumentProvider, EntityDocumentProvider>(bc, EntityDocumentProvider.class,
				null);
		providersTracker.open();
	}

	public void destroy() {
		providersTracker.close();
		providersTracker = null;
	}

	@Override
	public <T> String generateDocument(Class<T> entityClass, T entity, String documentId) {
		Collection<EntityDocumentProvider> providerList = findProviders();
		EntityDocumentProviderResult result = null;
		for (EntityDocumentProvider provider : providerList) {
			result = provider.generateDocument(entityClass, entity, documentId);
			if (result != null) {
				break;
			}
		}
		if (result == null) {
			throw new RuntimeException(String.format(
					"No suitable provider to generate document for entity class '%s' and document id '%s'", entityClass.getName(),
					documentId));
		}
		backend.saveResult(entityClass, entity, documentId, result);
		try {
			result.getInputStream().close();
		} catch (IOException e) {
			LOG.error("Unable to close the input stream of result of generating document.", e);
		}
		return result.getName();
	}

	private Collection<EntityDocumentProvider> findProviders() {
		// TODO Copy 
		return providersTracker.getTracked().values();
	}

	@Override
	public <T> List<EntityDocument> findDocuments(Class<T> entityClass, T entity) {
		return backend.findDocuments(entityClass, entity);
	}

	@Override
	public <T> EntityDocument findDocument(Class<T> entityClass, T entity, String documentName) {
		// TODO make better implementation (search the backend)
		for (EntityDocument entityDocument : findDocuments(entityClass, entity)) {
			if (entityDocument.getName().equals(documentName)) {
				return entityDocument;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String generateDocument(String entityClass, Object entity, String documentId) {
		return generateDocument((Class<Object>) dictionaryService.getClassDefinition(entityClass).getClassDefined(), entity,
				documentId);
	}

}
