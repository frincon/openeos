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
package org.openeos.erp.acct.gui;

import java.util.EnumSet;

import org.abstractform.binding.BForm;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFForm;
import org.abstractform.binding.fluent.BFSubForm;

import org.openeos.erp.acct.model.FinancialMgmtGLCategory;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;

public abstract class FinancialMgmtGLCategoryForm extends BFForm<FinancialMgmtGLCategory> implements
		AbstractFormBindingForm<FinancialMgmtGLCategory> {

	public static final int RANKING = 0;

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization",
			FinancialMgmtGLCategory.PROPERTY_ORGANIZATION);
	public BFField FIELD_NAME = SUBFORM_MAIN.addField(1, 0, null, "Name", FinancialMgmtGLCategory.PROPERTY_NAME);
	public BFField FIELD_CATEGORY_TYPE = SUBFORM_MAIN.addField(1, 1, null, "Category Type",
			FinancialMgmtGLCategory.PROPERTY_CATEGORY_TYPE);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description",
			FinancialMgmtGLCategory.PROPERTY_DESCRIPTION);

	public FinancialMgmtGLCategoryForm(String id, String name) {
		super(id, name, FinancialMgmtGLCategory.class);
	}

	@Override
	public Integer getRanking() {
		return RANKING;
	}

	@Override
	public BForm<FinancialMgmtGLCategory> getBForm() {
		return this;
	}

	public static class FinancialMgmtGLCategoryNewForm extends FinancialMgmtGLCategoryForm {

		public static final String ID = FinancialMgmtGLCategoryNewForm.class.getName();
		public static final String NAME = "Accounting Schema New Form";

		public FinancialMgmtGLCategoryNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

	}

	public static class FinancialMgmtGLCategoryEditForm extends FinancialMgmtGLCategoryForm {

		public static final String ID = FinancialMgmtGLCategoryEditForm.class.getName();
		public static final String NAME = "Account Tree Edit Form";

		public FinancialMgmtGLCategoryEditForm() {
			super(ID, NAME);
			FIELD_ORGANIZATION.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

}
