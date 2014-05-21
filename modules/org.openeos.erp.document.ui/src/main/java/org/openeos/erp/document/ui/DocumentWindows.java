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
package org.openeos.erp.document.ui;

import org.openeos.erp.document.model.DocumentType;
import org.openeos.services.ui.window.AbstractDictionaryBasedWindowDefinition;

public class DocumentWindows {

	public static class DocumentTypeWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = DocumentTypeWindow.class.getName();

		public DocumentTypeWindow() {
			super(ID, DocumentType.class);
			setName("Document Type");
			addVisibleField(DocumentType.PROPERTY_ORGANIZATION);
			addVisibleField(DocumentType.PROPERTY_NAME);
			addVisibleField(DocumentType.PROPERTY_DESCRIPTION);
			addOrderAsc(DocumentType.PROPERTY_NAME);
		}

	}

}
