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
package org.openeos.erp.document.triggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.SaveOrUpdateEvent;
import org.openeos.dao.SaveOrUpdateListener;
import org.openeos.dao.UserException;
import org.openeos.erp.document.TransactionDocument;
import org.openeos.numeration.NumerationService;
import org.openeos.numeration.model.Sequence;

public class TransactionDocumentAssignDocumentNo implements SaveOrUpdateListener<TransactionDocument> {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionDocumentAssignDocumentNo.class);

	private NumerationService numerationService;

	public void setNumerationService(NumerationService numerationService) {
		this.numerationService = numerationService;
	}

	@Override
	@Transactional(readOnly = false)
	public void beforeSaveOrUpdate(SaveOrUpdateEvent<? extends TransactionDocument> event) throws UserException {
		TransactionDocument document = event.getEntity();
		LOG.debug(String.format("Save or update transaction document, checking if need document number. Document class: %s",
				document.getClass()));
		if (document.getId() == null && document.getDocumentType() != null && document.getDocumentType().isDocNoControlled()) {
			LOG.debug("The document need document number, generating...");
			Sequence sequence;
			if (document.isDraft()) {
				sequence = document.getDocumentType().getDraftSequence();
			} else {
				sequence = document.getDocumentType().getFinalSequence();
			}
			String documentNo = numerationService.getAndIncrement(sequence.getId(), document);
			LOG.debug("New document number generated for document type '{}': '{}'", new Object[] {
					document.getDocumentType().getName(), documentNo });
			document.setDocumentNo(documentNo);
		}
	}

	@Override
	public void afterSaveOrUpdate(SaveOrUpdateEvent<? extends TransactionDocument> event) {
		// Nothing to do
	}

}
