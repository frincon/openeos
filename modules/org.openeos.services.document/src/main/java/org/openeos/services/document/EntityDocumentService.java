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
package org.openeos.services.document;

import java.util.List;

public interface EntityDocumentService {

	/**
	 * 
	 * @param entityClass
	 * @param entity
	 * @param documentId
	 * @return The document name
	 */
	public <T> String generateDocument(Class<T> entityClass, T entity, String documentId);

	public String generateDocument(String entityClass, Object entity, String documentId);

	public <T> List<EntityDocument> findDocuments(Class<T> entityClass, T entity);

	public <T> EntityDocument findDocument(Class<T> entityClass, T entity, String documentName);

}
