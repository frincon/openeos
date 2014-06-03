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

import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.BPresenter;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFSubForm;
import org.abstractform.binding.fluent.table.BFTableField;
import org.openeos.erp.document.ui.forms.DocumentTypePresenterDecorator;
import org.openeos.erp.sales.model.SalesInvoice;
import org.openeos.erp.sales.model.SalesInvoiceTax;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.BFUITable;
import org.openeos.services.ui.form.abstractform.UIAbstractForm;

public abstract class SalesInvoiceForm extends UIAbstractForm<SalesInvoice> {

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization", SalesInvoice.PROPERTY_ORGANIZATION);
	public BFField FIELD_DOCUMENT_TYPE = SUBFORM_MAIN.addField(0, 1, null, "Document Type", SalesInvoice.PROPERTY_DOCUMENT_TYPE);
	public BFField FIELD_DOCUMENTNO = SUBFORM_MAIN.addField(1, 0, null, "Document No", SalesInvoice.PROPERTY_DOCUMENT_NO);
	public BFField FIELD_BUSINESS_PARTNER = SUBFORM_MAIN.addField(2, 0, null, "Business Partner",
			SalesInvoice.PROPERTY_BUSINESS_PARTNER);
	public BFField FIELD_BP_LOCATION = SUBFORM_MAIN.addField(2, 1, null, "Location", SalesInvoice.PROPERTY__B_P_LOCATION);
	public BFField FIELD_DATEINVOICED = SUBFORM_MAIN.addField(3, 0, null, "Date Invoiced", SalesInvoice.PROPERTY_TRANSACTION_DATE);
	public BFField FIELD_TAXDATE = SUBFORM_MAIN.addField(3, 1, null, "Tax Invoiced", SalesInvoice.PROPERTY_TAXDATE);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description", SalesInvoice.PROPERTY_DESCRIPTION);
	public BFSubForm SUBFORM_TAXES = addSubForm(null, 1);
	public BFUITable TABLE_TAXES = SUBFORM_TAXES
			.addField(0, 0, null, "Taxes", SalesInvoice.PROPERTY_SALES_INVOICE_TAXES, BFUITable.class)
			.elementsClass(SalesInvoiceTax.class).pageLenght(3);
	public BFTableField TFIELD_TAXBASEAMT = TABLE_TAXES.addTableField("Tax Base Amount", SalesInvoiceTax.PROPERTY_TAXBASEAMT);
	public BFTableField TFIELD_TAXAMT = TABLE_TAXES.addTableField("Tax Amount", SalesInvoiceTax.PROPERTY_TAXAMT);
	public BFSubForm SUBFORM_TOTALS = addSubForm(null, 3);
	public BFField FIELD_GRANDTOTAL = SUBFORM_TOTALS.addField(0, 2, null, "Grand Total", SalesInvoice.PROPERTY_GRANDTOTAL);

	public SalesInvoiceForm(String id, String name) {
		super(id, name, SalesInvoice.class);
	}

	public static class SalesInvoiceEditForm extends SalesInvoiceForm {

		public static final String ID = SalesInvoiceEditForm.class.getName();
		public static final String NAME = "Sales Invoice Edit Form";

		public SalesInvoiceEditForm() {
			super(ID, NAME);
			FIELD_DOCUMENTNO.readOnly(true);
			FIELD_DOCUMENT_TYPE.readOnly(true);
			FIELD_ORGANIZATION.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

	public static class SalesInvoiceNewForm extends SalesInvoiceForm {

		public static final String ID = SalesInvoiceNewForm.class.getName();
		public static final String NAME = "Sales Invoice New Form";

		public SalesInvoiceNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

		@Override
		public BPresenter createPresenter(final BFormInstance<UIBean, ?> formInstance, UIBean model) {
			return new DocumentTypePresenterDecorator(super.createPresenter(formInstance, model), formInstance,
					FIELD_DOCUMENT_TYPE.getId(), FIELD_DOCUMENTNO.getId());
		}
	}

	public static class SalesInvoiceTaxForm extends UIAbstractForm<SalesInvoiceTax> {

		public static final String ID = SalesInvoiceTaxForm.class.getName();
		public static final String NAME = "Sales Invoice Tax Form";

		public BFSubForm SUBFORM_MAIN = addSubForm(null, 3);
		public BFField FIELD_LINE = SUBFORM_MAIN.addField(0, 0, null, "Line", SalesInvoiceTax.PROPERTY_LINE);
		public BFField FIELD_TAXBASEAMT = SUBFORM_MAIN.addField(0, 1, null, "Tax Base Amount", SalesInvoiceTax.PROPERTY_TAXBASEAMT);
		public BFField FIELD_TAXAMT = SUBFORM_MAIN.addField(0, 2, null, "Tax Amount", SalesInvoiceTax.PROPERTY_TAXAMT);

		public SalesInvoiceTaxForm() {
			super(ID, NAME, SalesInvoiceTax.class);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT, BindingFormCapability.NEW);
		}

	}

}
