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
package org.openeos.attachments.dao;

import java.io.InputStream;
import java.util.List;

import org.openeos.attachments.model.Attachment;
import org.openeos.dao.GenericDAO;

public interface AttachmentDAO extends GenericDAO<Attachment, String> {

	/**
	 * Find attachments based on an entity. The list return must be sorted by
	 * creation date
	 * 
	 * @param entityClass
	 * @param entityId
	 * @return
	 */
	public <T> List<Attachment> findByEntity(Class<T> entityClass, String entityId);

	public void setContent(Attachment attachment, InputStream inputStream);

}
