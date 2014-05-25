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
package org.openeos.erp.sales.ui;

import java.util.EnumSet;

import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFSubForm;
import org.openeos.erp.sales.model.BPTaxCategory;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.UIAbstractForm;

public abstract class BPTaxCategoryForm extends UIAbstractForm<BPTaxCategory> {

	public static final int RANKING = 0;

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization", BPTaxCategory.PROPERTY_ORGANIZATION);
	public BFField FIELD_NAME = SUBFORM_MAIN.addField(1, 0, null, "Name", BPTaxCategory.PROPERTY_NAME);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description", BPTaxCategory.PROPERTY_DESCRIPTION);

	public BPTaxCategoryForm(String id, String name) {
		super(id, name, BPTaxCategory.class);
	}

	@Override
	public Integer getRanking() {
		return RANKING;
	}

	public static class BPTaxCategoryNewForm extends BPTaxCategoryForm {

		public static final String ID = BPTaxCategoryNewForm.class.getName();
		public static final String NAME = "Business Partner Tax Category New Form";

		public BPTaxCategoryNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

	}

	public static class BPTaxCategoryEditForm extends BPTaxCategoryForm {

		public static final String ID = BPTaxCategoryEditForm.class.getName();
		public static final String NAME = "Business Partner Tax Category Edit Form";

		public BPTaxCategoryEditForm() {
			super(ID, NAME);
			FIELD_ORGANIZATION.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

}
