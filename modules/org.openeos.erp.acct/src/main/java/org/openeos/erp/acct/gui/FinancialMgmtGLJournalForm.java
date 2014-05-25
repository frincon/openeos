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

import java.math.BigDecimal;
import java.util.EnumSet;

import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.BPresenter;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFSection;
import org.abstractform.binding.fluent.BFSubForm;
import org.abstractform.binding.fluent.table.BFTableField;
import org.openeos.erp.acct.model.FinancialMgmtGLJournal;
import org.openeos.erp.acct.model.FinancialMgmtGLJournalLine;
import org.openeos.erp.document.ui.forms.DocumentTypePresenterDecorator;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.BFUITable;
import org.openeos.services.ui.form.abstractform.UIAbstractForm;
import org.openeos.services.ui.form.abstractform.UIPresenter;

public abstract class FinancialMgmtGLJournalForm extends UIAbstractForm<FinancialMgmtGLJournal> {

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization",
			FinancialMgmtGLJournal.PROPERTY_ORGANIZATION);
	public BFField FIELD_DOCUMENT_TYPE = SUBFORM_MAIN.addField(1, 0, null, "Document Type",
			FinancialMgmtGLJournal.PROPERTY_DOCUMENT_TYPE);
	public BFField FIELD_DOCUMENT_NO = SUBFORM_MAIN.addField(1, 1, null, "Document No.",
			FinancialMgmtGLJournal.PROPERTY_DOCUMENT_NO);
	public BFField FIELD_TRANSACTION_DATE = SUBFORM_MAIN.addField(2, 0, null, "Transaction Date",
			FinancialMgmtGLJournal.PROPERTY_TRANSACTION_DATE);
	public BFField FIELD_ACCT_DATE = SUBFORM_MAIN
			.addField(2, 1, null, "Accounting Date", FinancialMgmtGLJournal.PROPERTY_ACCT_DATE);
	public BFField FIELD_CATEGORY = SUBFORM_MAIN.addField(3, 0, null, "Category",
			FinancialMgmtGLJournal.PROPERTY_FINANCIAL_MGMT_G_L_CATEGORY);
	//public BFField FIELD_PERIOD = SUBFORM_MAIN
	//		.addField(3, 0, null, "Period", FinancialMgmtGLJournal.PROPERTY_FINANCIAL_MGMT_PERIOD);

	public BFSection SECTION_TOTAL = addSection(null, "Total");
	public BFSubForm SUBFORM_TOTAL = SECTION_TOTAL.addSubForm(null, 2);
	public BFField FIELD_TOTAL_DEBIT = SUBFORM_TOTAL.addField(0, 0, null, "Total Debit", FinancialMgmtGLJournal.PROPERTY_TOTAL_D_R)
			.readOnly(true);
	public BFField FIELD_TOTAL_CREDIT = SUBFORM_TOTAL.addField(0, 1, null, "Total Credit",
			FinancialMgmtGLJournal.PROPERTY_TOTAL_C_R).readOnly(true);

	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description",
			FinancialMgmtGLJournal.PROPERTY_DESCRIPTION);

	public BFSection SECTION_LINES = addSection(null, "Lines");
	public BFSubForm SUBFORM_LINES = SECTION_LINES.addSubForm(null, 1);
	public BFUITable TABLE_LINES = SUBFORM_LINES
			.addField(0, 0, null, "Lines", FinancialMgmtGLJournal.PROPERTY_FINANCIAL_MGMT_G_L_JOURNAL_LINES, BFUITable.class)
			.elementsClass(FinancialMgmtGLJournalLine.class).pageLenght(6);
	public BFTableField TFIELD_LINE = TABLE_LINES.addTableField("Line", FinancialMgmtGLJournalLine.PROPERTY_LINE);
	public BFTableField TFIELD_ACCOUNT = TABLE_LINES.addTableField("Account",
			FinancialMgmtGLJournalLine.PROPERTY_FINANCIAL_MGMT_ACCOUNTING_COMBINATION);
	public BFTableField TFIELD_DEBIT = TABLE_LINES.addTableField("Debit", FinancialMgmtGLJournalLine.PROPERTY_AMT_ACCT_D_R);
	public BFTableField TFIELD_CREDIT = TABLE_LINES.addTableField("Credit", FinancialMgmtGLJournalLine.PROPERTY_AMT_ACCT_C_R);
	public BFTableField TFIELD_DESCRIPTION = TABLE_LINES.addTableField("Description",
			FinancialMgmtGLJournalLine.PROPERTY_DESCRIPTION);

	public FinancialMgmtGLJournalForm(String id, String name) {
		super(id, name, FinancialMgmtGLJournal.class);
	}

	@Override
	public BPresenter createPresenter(final BFormInstance<UIBean> formInstance, UIBean model) {
		return new DocumentTypePresenterDecorator(new UIPresenter<FinancialMgmtGLJournal>(FinancialMgmtGLJournal.class, model) {

			@Override
			public void fieldHasChanged(String fieldId) {
				super.fieldHasChanged(fieldId);
				calculateTotals();
			}

			@Override
			public void setPropertyValue(String propertyName, Object value) {
				super.setPropertyValue(propertyName, value);
				calculateTotals();
			}

			private void calculateTotals() {
				if (getModel() != null) {
					BigDecimal cr = getBeanWrapped().getTotalCR();
					BigDecimal dr = getBeanWrapped().getTotalDR();
					BigDecimal newCr, newDr;
					newCr = newDr = BigDecimal.ZERO;
					for (FinancialMgmtGLJournalLine line : getBeanWrapped().getFinancialMgmtGLJournalLines()) {
						newCr = newCr.add(line.getAmtAcctCR());
						newDr = newDr.add(line.getAmtAcctDR());
					}
					if (newCr.compareTo(cr) != 0) {
						formInstance.setFieldValue(FIELD_TOTAL_CREDIT.getId(), newCr);
					}
					if (newDr.compareTo(dr) != 0) {
						formInstance.setFieldValue(FIELD_TOTAL_DEBIT.getId(), newDr);
					}
				}
			}

		}, formInstance, FIELD_DOCUMENT_TYPE.getId(), FIELD_DOCUMENT_NO.getId());
	}

	public static class FinancialMgmtGLJournalNewForm extends FinancialMgmtGLJournalForm {

		public static final String ID = FinancialMgmtGLJournalNewForm.class.getName();
		public static final String NAME = "G/L Journal New Form";

		public FinancialMgmtGLJournalNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

	}

	public static class FinancialMgmtGLJournalEditForm extends FinancialMgmtGLJournalForm {

		public static final String ID = FinancialMgmtGLJournalEditForm.class.getName();
		public static final String NAME = "G/L Journal Edit Form";

		public FinancialMgmtGLJournalEditForm() {
			super(ID, NAME);
			FIELD_ORGANIZATION.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

	public static class FinancialMgmtGLJournalLineForm extends UIAbstractForm<FinancialMgmtGLJournalLine> {

		public static final String ID = FinancialMgmtGLJournalLineForm.class.getName();
		public static final String NAME = "G/L Journal Line Form";

		public BFSubForm SUBFORM_MAIN = addSubForm(null, 4);
		public BFField FIELD_COMBINATION = SUBFORM_MAIN.addField(0, 0, null, "Combination",
				FinancialMgmtGLJournalLine.PROPERTY_FINANCIAL_MGMT_ACCOUNTING_COMBINATION);
		public BFField FIELD_CREDIT = SUBFORM_MAIN.addField(0, 1, null, "Credit", FinancialMgmtGLJournalLine.PROPERTY_AMT_ACCT_C_R);
		public BFField FIELD_DEBIT = SUBFORM_MAIN.addField(0, 2, null, "Debit", FinancialMgmtGLJournalLine.PROPERTY_AMT_ACCT_D_R);
		public BFField FIELD_DESCRIPTION = SUBFORM_MAIN.addField(0, 3, null, "Description",
				FinancialMgmtGLJournalLine.PROPERTY_DESCRIPTION);

		public FinancialMgmtGLJournalLineForm() {
			super(ID, NAME, FinancialMgmtGLJournalLine.class);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT, BindingFormCapability.NEW);
		}

	}

}
