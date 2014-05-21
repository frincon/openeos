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
package org.openeos.erp.sales.model.aspects;

import org.openeos.erp.sales.model.SalesInvoice;

import org.openeos.erp.document.TransactionDocument;
import org.hibernate.annotations.Cascade;

import org.openeos.services.dictionary.validation.annotations.SQLValidation;

public aspect SalesInvoiceAdditionsAspect {

	declare parents: SalesInvoice implements TransactionDocument;

	declare @method: public * SalesInvoice.getSalesInvoiceTaxes() : @Cascade(org.hibernate.annotations.CascadeType.ALL);

	declare @method: public * SalesInvoice.getBPLocation() : @SQLValidation("{alias}.c_bpartner_id=@source.businessPartner.id@");

	declare @method: public * SalesInvoice.getBusinessPartner() : @SQLValidation("{alias}.customer=@true@");

}
