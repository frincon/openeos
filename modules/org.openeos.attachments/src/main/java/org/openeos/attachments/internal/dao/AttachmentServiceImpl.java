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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ProxyInputStream;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.openeos.attachments.Attachment;
import org.openeos.attachments.AttachmentsService;
import org.openeos.attachments.dao.AttachmentDAO;
import org.openeos.services.dictionary.IDictionaryService;

public class AttachmentServiceImpl implements AttachmentsService {

	private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

	private AttachmentDAO attachmentDAO;
	private IDictionaryService dictionaryService;

	private class AttachmentModelBased implements Attachment {
		private org.openeos.attachments.model.Attachment model;

		private AttachmentModelBased(org.openeos.attachments.model.Attachment model) {
			this.model = model;
		}

		@Override
		public String getMimeType() {
			return model.getMimeType();
		}

		@Override
		public String getName() {
			return model.getName();
		}

		@Override
		public String getType() {
			return model.getAttachmentType();
		}

		@Override
		public InputStream openStream() throws IOException {

			AnnotationTransactionAspect txAspect = AnnotationTransactionAspect.aspectOf();
			PlatformTransactionManager manager = txAspect.getTransactionManager();
			final TransactionStatus status = manager.getTransaction(new DefaultTransactionDefinition());
			try {
				attachmentDAO.refresh(model);
				return new ProxyInputStream(model.getContent().getBinaryStream()) {

					@Override
					public void close() throws IOException {
						super.close();
						if (status.isNewTransaction()) {
							AnnotationTransactionAspect txAspect = AnnotationTransactionAspect.aspectOf();
							PlatformTransactionManager manager = txAspect.getTransactionManager();
							manager.commit(status);
						}
					}

				};
			} catch (SQLException e) {
				throw new IOException("Unable to open stream from blob object", e);
			}
		}

		@Override
		@Transactional
		public byte[] getContent() throws IOException {
			InputStream is = openStream();
			byte[] result = IOUtils.toByteArray(is);
			is.close();
			return result;
		}

	}

	public AttachmentServiceImpl(AttachmentDAO attachmentDAO, IDictionaryService dictionaryService) {
		this.attachmentDAO = attachmentDAO;
		this.dictionaryService = dictionaryService;
	}

	@Override
	@Transactional
	public <T> Attachment attachFile(Class<T> entityClass, T entity, String attachmentType, String name, String mimeType,
			InputStream content) {
		return attachFile(entityClass, (String) dictionaryService.getClassDefinition(entityClass).getId(entity), attachmentType,
				name, mimeType, content);
	}

	@Override
	@Transactional
	public <T> Attachment attachFile(Class<T> entityClass, T entity, String attachmentType, URL url) {
		try {
			InputStream is = url.openStream();
			Attachment result = attachFile(entityClass, entity, attachmentType, FilenameUtils.getName(url.getPath()),
					getMimeType(url.getFile()), is);
			is.close();
			return result;
		} catch (IOException e) {
			throw new RuntimeException("Unable to set content to attachement model object", e);
		}
	}

	private Attachment createAttachment(org.openeos.attachments.model.Attachment attachModel) {
		return new AttachmentModelBased(attachModel);
	}

	private String getMimeType(String file) {
		String mimeType = URLConnection.guessContentTypeFromName(file);
		if (mimeType == null) {
			mimeType = DEFAULT_MIME_TYPE;
		}
		return mimeType;
	}

	@Override
	public <T> List<Attachment> getAttachmentList(Class<T> entityClass, T entity) {
		return getAttachmentList(entityClass, (String) dictionaryService.getClassDefinition(entityClass).getId(entity));
	}

	@Override
	public Attachment attachFile(Class<?> entityClass, String entityId, String attachmentType, URL url) {
		try {
			InputStream is = url.openStream();
			Attachment result = attachFile(entityClass, entityId, attachmentType, FilenameUtils.getName(url.getPath()),
					getMimeType(url.getFile()), is);
			is.close();
			return result;
		} catch (IOException e) {
			throw new RuntimeException("Unable to set content to attachement model object", e);
		}
	}

	@Override
	public Attachment attachFile(Class<?> entityClass, String entityId, String attachmentType, String name, String mimeType,
			InputStream content) {
		try {
			org.openeos.attachments.model.Attachment attachModel = new org.openeos.attachments.model.Attachment();
			attachModel.setAttachmentType(attachmentType);
			attachModel.setCreationDate(new Date());
			attachModel.setModifiedDate(attachModel.getCreationDate());
			attachModel.setEntityClass(entityClass.getName());
			attachModel.setEntityId(entityId);
			attachModel.setName(name);
			attachModel.setMimeType(mimeType);
			attachmentDAO.setContent(attachModel, content);
			attachmentDAO.create(attachModel);
			return createAttachment(attachModel);
		} catch (ConstraintViolationException e) {
			throw new RuntimeException("The given name, attachment type already exists in the entity given");
		}
	}

	@Override
	public List<Attachment> getAttachmentList(Class<?> entityClass, String entityId) {
		//TODO Make better implementation
		List<org.openeos.attachments.model.Attachment> model = attachmentDAO.findByEntity(entityClass, entityId);
		List<Attachment> result = new ArrayList<Attachment>(model.size());
		for (org.openeos.attachments.model.Attachment attModel : model) {
			result.add(createAttachment(attModel));
		}
		return result;
	}

}
