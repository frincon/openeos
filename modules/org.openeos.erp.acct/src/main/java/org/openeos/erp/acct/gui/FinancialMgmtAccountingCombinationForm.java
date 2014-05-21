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

import org.openeos.erp.acct.model.FinancialMgmtAccountingCombination;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;

public abstract class FinancialMgmtAccountingCombinationForm extends BFForm<FinancialMgmtAccountingCombination> implements
		AbstractFormBindingForm<FinancialMgmtAccountingCombination> {

	public static final int RANKING = 0;

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization",
			FinancialMgmtAccountingCombination.PROPERTY_ORGANIZATION);
	public BFField FIELD_SCHEMA = SUBFORM_MAIN.addField(0, 1, null, "Account Configuration",
			FinancialMgmtAccountingCombination.PROPERTY_FINANCIAL_MGMT_ACCT_SCHEMA);
	public BFField FIELD_ALIAS = SUBFORM_MAIN.addField(1, 0, null, "Alias", FinancialMgmtAccountingCombination.PROPERTY_ALIAS);
	public BFField FIELD_ELEMENT_VALUE = SUBFORM_MAIN.addField(1, 1, null, "Element",
			FinancialMgmtAccountingCombination.PROPERTY_FINANCIAL_MGMT_ELEMENT_VALUE);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description",
			FinancialMgmtAccountingCombination.PROPERTY_DESCRIPTION);

	public FinancialMgmtAccountingCombinationForm(String id, String name) {
		super(id, name, FinancialMgmtAccountingCombination.class);
	}

	@Override
	public Integer getRanking() {
		return RANKING;
	}

	@Override
	public BForm<FinancialMgmtAccountingCombination> getBForm() {
		return this;
	}

	public static class FinancialMgmtAccountingCombinationNewForm extends FinancialMgmtAccountingCombinationForm {

		public static final String ID = FinancialMgmtAccountingCombinationNewForm.class.getName();
		public static final String NAME = "Valid Combination New Form";

		public FinancialMgmtAccountingCombinationNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

	}

	public static class FinancialMgmtAccountingCombinationEditForm extends FinancialMgmtAccountingCombinationForm {

		public static final String ID = FinancialMgmtAccountingCombinationEditForm.class.getName();
		public static final String NAME = "Valid Combination Edit Form";

		public FinancialMgmtAccountingCombinationEditForm() {
			super(ID, NAME);
			FIELD_ORGANIZATION.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

}
