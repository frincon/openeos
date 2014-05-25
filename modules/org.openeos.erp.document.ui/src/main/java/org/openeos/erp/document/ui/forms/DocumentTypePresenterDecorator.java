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

public class DocumentTypePresenterDecorator implements BPresenter {

	private boolean documentNoEstablished = false;

	private BPresenter delegate;
	private BFormInstance<?> formInstance;
	private String fieldDocumentTypeId;
	private String fieldDocumentNoId;

	public DocumentTypePresenterDecorator(BPresenter delegate, BFormInstance<?> formInstance, String fieldDocumentTypeId,
			String fieldDocumentNoId) {
		this.delegate = delegate;
		this.formInstance = formInstance;
		this.fieldDocumentTypeId = fieldDocumentTypeId;
		this.fieldDocumentNoId = fieldDocumentNoId;
		checkForDocumentNo();
	}

	@Override
	public void fieldHasChanged(String fieldId) {
		delegate.fieldHasChanged(fieldId);
		checkForDocumentNo();
	}

	private void checkForDocumentNo() {
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

	// Delegate methods
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		delegate.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		delegate.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * 
	 * @see org.abstractform.binding.BPresenter#getPropertyValue(java.lang.String)
	 */
	@Override
	public Object getPropertyValue(String propertyName) {
		return delegate.getPropertyValue(propertyName);
	}

	/**
	 * 
	 * @see org.abstractform.binding.BPresenter#setPropertyValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setPropertyValue(String propertyName, Object value) {
		delegate.setPropertyValue(propertyName, value);
		checkForDocumentNo();
	}

}
