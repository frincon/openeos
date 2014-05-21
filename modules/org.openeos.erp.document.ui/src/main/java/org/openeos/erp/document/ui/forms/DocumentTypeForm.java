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

import java.util.EnumSet;

import org.abstractform.binding.BForm;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFForm;
import org.abstractform.binding.fluent.BFSubForm;

import org.openeos.erp.document.model.DocumentType;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;

public class DocumentTypeForm extends BFForm<DocumentType> implements AbstractFormBindingForm<DocumentType> {

	public static final Integer RANKING = 0;

	public static final String ID = DocumentTypeForm.class.getName();
	public static final String NAME = "Document Type Form";

	// Form DefinitionF
	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization", DocumentType.PROPERTY_ORGANIZATION);
	public BFField FIELD_NAME = SUBFORM_MAIN.addField(0, 1, null, "Name", DocumentType.PROPERTY_NAME);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description", DocumentType.PROPERTY_DESCRIPTION);
	public BFSubForm SUBFORM_DETAIL = addSubForm(null, 2);
	public BFField FIELD_TARGETTYPE = SUBFORM_DETAIL.addField(0, 0, null, "Target Type", DocumentType.PROPERTY_TARGET_TYPE);
	public BFField FIELD_DOCNOCONTROLLED = SUBFORM_DETAIL.addField(1, 0, null, "Doc. Num. Controlled",
			DocumentType.PROPERTY_DOC_NO_CONTROLLED);
	public BFField FIELD_SEQUENCE_DRAFT = SUBFORM_DETAIL.addField(2, 0, null, "Draft Sequnence",
			DocumentType.PROPERTY_DRAFT_SEQUENCE);
	public BFField FIELD_SEQUENCE_FINAL = SUBFORM_DETAIL.addField(2, 1, null, "Final Sequence",
			DocumentType.PROPERTY_FINAL_SEQUENCE);

	public DocumentTypeForm() {
		super(ID, NAME, DocumentType.class);
	}

	@Override
	public Integer getRanking() {
		return RANKING;
	}

	@Override
	public EnumSet<BindingFormCapability> getCapabilities() {
		return EnumSet.of(BindingFormCapability.EDIT, BindingFormCapability.NEW);
	}

	@Override
	public BForm<DocumentType> getBForm() {
		return this;
	}

}
