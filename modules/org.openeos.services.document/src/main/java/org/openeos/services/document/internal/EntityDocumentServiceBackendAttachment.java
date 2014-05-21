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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openeos.attachments.Attachment;
import org.openeos.attachments.AttachmentsService;
import org.openeos.services.document.EntityDocument;
import org.openeos.services.document.spi.EntityDocumentProviderResult;

public class EntityDocumentServiceBackendAttachment implements EntityDocumentServiceBackend {

	private static class EntityDocumentAtt implements EntityDocument {
		private Attachment attachment;

		public EntityDocumentAtt(Attachment attachment) {
			this.attachment = attachment;
		}

		@Override
		public String getDocumentId() {
			return attachment.getType();
		}

		@Override
		public String getName() {
			return attachment.getName();
		}

		@Override
		public String getMimeType() {
			return attachment.getMimeType();
		}

		@Override
		public InputStream openInputStream() throws IOException {
			return attachment.openStream();
		}

		@Override
		public byte[] getContent() throws IOException {
			return attachment.getContent();
		}

	}

	private AttachmentsService attachmentService;

	public EntityDocumentServiceBackendAttachment(AttachmentsService attachmentService) {
		this.attachmentService = attachmentService;
	}

	@Override
	public <T> void saveResult(Class<T> entityClass, T entity, String documentId, EntityDocumentProviderResult result) {
		attachmentService.attachFile(entityClass, entity, documentId, result.getName(), result.getMimeType(),
				result.getInputStream());

	}

	@Override
	public <T> List<EntityDocument> findDocuments(Class<T> entityClass, T entity) {
		List<Attachment> attachmentList = attachmentService.getAttachmentList(entityClass, entity);
		// TODO Make better implementation
		List<EntityDocument> result = new ArrayList<EntityDocument>(attachmentList.size());
		for (Attachment att : attachmentList) {
			result.add(new EntityDocumentAtt(att));
		}
		return Collections.unmodifiableList(result);
	}

}
