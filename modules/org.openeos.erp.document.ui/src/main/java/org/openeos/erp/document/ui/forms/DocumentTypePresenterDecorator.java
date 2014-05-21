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
package org.openeos.erp.document.ui.forms;

import java.beans.PropertyChangeListener;

import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.BPresenter;

import org.openeos.erp.document.model.DocumentType;

public class DocumentTypePresenterDecorator<T> implements BPresenter<T> {

	private boolean documentNoEstablished = false;

	private BPresenter<T> delegate;
	private BFormInstance<T> formInstance;
	private String fieldDocumentTypeId;
	private String fieldDocumentNoId;

	public DocumentTypePresenterDecorator(BPresenter<T> delegate, BFormInstance<T> formInstance, String fieldDocumentTypeId,
			String fieldDocumentNoId) {
		this.delegate = delegate;
		this.formInstance = formInstance;
		this.fieldDocumentTypeId = fieldDocumentTypeId;
		this.fieldDocumentNoId = fieldDocumentNoId;
	}

	@Override
	public void fieldHasChanged(String fieldId, T model) {
		delegate.fieldHasChanged(fieldId, model);
		checkForDocumentNo();
	}

	@Override
	public void modelHasChanged(String propertyName, T model) {
		delegate.modelHasChanged(propertyName, model);
		checkForDocumentNo();
	}

	@Override
	public void setModel(T model) {
		delegate.setModel(model);
		checkForDocumentNo();
	}

	private void checkForDocumentNo() {
		if (getModel() != null) {
			DocumentType docType = (DocumentType) formInstance.getFieldValue(fieldDocumentTypeId);
			if (docType != null && docType.isDocNoControlled() && !documentNoEstablished) {
				formInstance.setFieldValue(fieldDocumentNoId, "<AUTO>");
				formInstance.setFieldReadOnly(fieldDocumentNoId, true);
				documentNoEstablished = true;
			} else if ((docType == null || !docType.isDocNoControlled()) && documentNoEstablished) {
				formInstance.setFieldReadOnly(fieldDocumentNoId, false);
				formInstance.setFieldValue(fieldDocumentNoId, null);
				documentNoEstablished = false;
			}
		}
	}

	// Delegate methods
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		delegate.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		delegate.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public T getModel() {
		return delegate.getModel();
	}

}
