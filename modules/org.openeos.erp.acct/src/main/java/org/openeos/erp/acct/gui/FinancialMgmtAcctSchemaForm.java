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

import org.openeos.erp.acct.model.FinancialMgmtAcctSchema;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;

public abstract class FinancialMgmtAcctSchemaForm extends BFForm<FinancialMgmtAcctSchema> implements
		AbstractFormBindingForm<FinancialMgmtAcctSchema> {

	public static final int RANKING = 0;

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization",
			FinancialMgmtAcctSchema.PROPERTY_ORGANIZATION);
	public BFField FIELD_NAME = SUBFORM_MAIN.addField(1, 0, null, "Name", FinancialMgmtAcctSchema.PROPERTY_NAME);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description",
			FinancialMgmtAcctSchema.PROPERTY_DESCRIPTION);

	public FinancialMgmtAcctSchemaForm(String id, String name) {
		super(id, name, FinancialMgmtAcctSchema.class);
	}

	@Override
	public Integer getRanking() {
		return RANKING;
	}

	@Override
	public BForm<FinancialMgmtAcctSchema> getBForm() {
		return this;
	}

	public static class FinancialMgmtAcctSchemaNewForm extends FinancialMgmtAcctSchemaForm {

		public static final String ID = FinancialMgmtAcctSchemaNewForm.class.getName();
		public static final String NAME = "Accounting Schema New Form";

		public FinancialMgmtAcctSchemaNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

	}

	public static class FinancialMgmtAcctSchemaEditForm extends FinancialMgmtAcctSchemaForm {

		public static final String ID = FinancialMgmtAcctSchemaEditForm.class.getName();
		public static final String NAME = "Account Tree Edit Form";

		public FinancialMgmtAcctSchemaEditForm() {
			super(ID, NAME);
			FIELD_ORGANIZATION.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

}
