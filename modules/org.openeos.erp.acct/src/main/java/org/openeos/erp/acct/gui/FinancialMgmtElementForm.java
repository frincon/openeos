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

import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFSubForm;
import org.abstractform.binding.fluent.table.BFTableField;
import org.openeos.erp.acct.model.FinancialMgmtElement;
import org.openeos.erp.acct.model.FinancialMgmtElementValue;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.BFUITable;
import org.openeos.services.ui.form.abstractform.UIAbstractForm;

public abstract class FinancialMgmtElementForm extends UIAbstractForm<FinancialMgmtElement> {

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization",
			FinancialMgmtElement.PROPERTY_ORGANIZATION);
	public BFField FIELD_NAME = SUBFORM_MAIN.addField(1, 0, null, "Name", FinancialMgmtElement.PROPERTY_NAME);
	public BFField FIELD_ELEMENT_TYPE = SUBFORM_MAIN.addField(1, 1, null, "Type", FinancialMgmtElement.PROPERTY_ELEMENT_TYPE);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description",
			FinancialMgmtElement.PROPERTY_DESCRIPTION);

	public BFSubForm SUBFORM_ELEMENT_VALUE = addSubForm(null, 1);
	public BFUITable TABLE_ELEMENT_VALUE = SUBFORM_ELEMENT_VALUE
			.addField(0, 0, null, "Element Value", FinancialMgmtElement.PROPERTY_FINANCIAL_MGMT_ELEMENT_VALUES, BFUITable.class)
			.elementsClass(FinancialMgmtElementValue.class).pageLenght(3);
	public BFTableField TFIELD_VALUE = TABLE_ELEMENT_VALUE.addTableField("Search Key", FinancialMgmtElementValue.PROPERTY_VALUE);
	public BFTableField TFIELD_NAME = TABLE_ELEMENT_VALUE.addTableField("Name", FinancialMgmtElementValue.PROPERTY_NAME);

	public FinancialMgmtElementForm(String id, String name) {
		super(id, name, FinancialMgmtElement.class);
	}

	public static class FinancialMgmtElementNewForm extends FinancialMgmtElementForm {

		public static final String ID = FinancialMgmtElementNewForm.class.getName();
		public static final String NAME = "Account Tree New Form";

		public FinancialMgmtElementNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

	}

	public static class FinancialMgmtElementEditForm extends FinancialMgmtElementForm {

		public static final String ID = FinancialMgmtElementEditForm.class.getName();
		public static final String NAME = "Account Tree Edit Form";

		public FinancialMgmtElementEditForm() {
			super(ID, NAME);
			FIELD_ORGANIZATION.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

	public static abstract class FinancialMgmtElementValueForm extends UIAbstractForm<FinancialMgmtElementValue> {

		public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
		public BFField FIELD_VALUE = SUBFORM_MAIN.addField(0, 0, null, "Search Key", FinancialMgmtElementValue.PROPERTY_VALUE);
		public BFField FIELD_NAME = SUBFORM_MAIN.addField(1, 0, null, "Name", FinancialMgmtElementValue.PROPERTY_NAME);
		public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
		public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description",
				FinancialMgmtElementValue.PROPERTY_DESCRIPTION);

		public FinancialMgmtElementValueForm(String id, String name) {
			super(id, name, FinancialMgmtElementValue.class);
		}

		public static class FinancialMgmtElementValueNewForm extends FinancialMgmtElementValueForm {
			public static final String ID = FinancialMgmtElementValueNewForm.class.getName();
			public static final String NAME = "Element Value New Form";

			public FinancialMgmtElementValueNewForm() {
				super(ID, NAME);
			}

			@Override
			public EnumSet<BindingFormCapability> getCapabilities() {
				return EnumSet.of(BindingFormCapability.NEW);
			}

		}

		public static class FinancialMgmtElementValueEditForm extends FinancialMgmtElementValueForm {
			public static final String ID = FinancialMgmtElementValueEditForm.class.getName();
			public static final String NAME = "Element Value Edit Form";

			public FinancialMgmtElementValueEditForm() {
				super(ID, NAME);
			}

			@Override
			public EnumSet<BindingFormCapability> getCapabilities() {
				return EnumSet.of(BindingFormCapability.EDIT);
			}

		}

	}

}
