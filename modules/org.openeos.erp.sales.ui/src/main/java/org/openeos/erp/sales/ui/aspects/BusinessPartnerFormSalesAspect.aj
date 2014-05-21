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
package org.openeos.erp.sales.ui.aspects;

import org.abstractform.binding.fluent.BFDrawer;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFSubForm;

import org.openeos.erp.core.model.BusinessPartner;
import org.openeos.erp.core.ui.forms.BusinessPartnerForm;

public aspect BusinessPartnerFormSalesAspect {

	public BFDrawer BusinessPartnerForm.DRAWER_CUSTOMER;
	public BFSubForm BusinessPartnerForm.SUBFORM_CUSTOMER;
	public BFField BusinessPartnerForm.FIELD_CUSTOMER;
	public BFField BusinessPartnerForm.FIELD_BP_TAX_CATEGORY;

	after(BusinessPartnerForm form) returning() : target(form) && execution(BusinessPartnerForm.new(..)) {
		form.DRAWER_CUSTOMER = form.addDrawer(null, "Customer");
		form.SUBFORM_CUSTOMER = form.DRAWER_CUSTOMER.addSubForm(null, 2);
		form.FIELD_CUSTOMER = form.SUBFORM_CUSTOMER.addField(0, 0, null, "Is Customer", BusinessPartner.PROPERTY_CUSTOMER);
		form.FIELD_BP_TAX_CATEGORY = form.SUBFORM_CUSTOMER.addField(1, 0, null, "Business Partner Tax Category",
				BusinessPartner.PROPERTY_BP_TAX_CATEGORY);
	}
}
