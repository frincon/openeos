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

import org.openeos.erp.sales.model.BPTaxCategory;
import org.openeos.erp.sales.model.SalesInvoice;
import org.openeos.erp.sales.model.Tax;
import org.openeos.erp.sales.model.TaxCategory;
import org.openeos.services.ui.window.AbstractDictionaryBasedWindowDefinition;

public class SalesWindows {

	public static class BPTaxCategoryWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = BPTaxCategoryWindow.class.getName();

		public BPTaxCategoryWindow() {
			super(ID, BPTaxCategory.class);
			setName("B. Partner Tax Category");
			addVisibleField(BPTaxCategory.PROPERTY_ORGANIZATION);
			addVisibleField(BPTaxCategory.PROPERTY_NAME);
			addVisibleField(BPTaxCategory.PROPERTY_DESCRIPTION);
			addOrderAsc(BPTaxCategory.PROPERTY_NAME);
		}

	}

	public static class TaxCategoryWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = TaxCategoryWindow.class.getName();

		public TaxCategoryWindow() {
			super(ID, TaxCategory.class);
			setName("Tax Category");
			addVisibleField(TaxCategory.PROPERTY_ORGANIZATION);
			addVisibleField(TaxCategory.PROPERTY_NAME);
			addVisibleField(TaxCategory.PROPERTY_DESCRIPTION);
			addOrderAsc(TaxCategory.PROPERTY_NAME);
		}

	}

	public static class TaxWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = Tax.class.getName();

		public TaxWindow() {
			super(ID, Tax.class);
			setName("Tax");
			addVisibleField(Tax.PROPERTY_ORGANIZATION);
			addVisibleField(Tax.PROPERTY_NAME);
			addVisibleField(Tax.PROPERTY_DESCRIPTION);
			addVisibleField(Tax.PROPERTY__B_P_TAX_CATEGORY);
			addVisibleField(Tax.PROPERTY_TAX_CATEGORY);
			addVisibleField(Tax.PROPERTY_SUMMARY);
			addOrderAsc(Tax.PROPERTY_NAME);
		}

	}

	public static class SalesInvoiceWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = SalesInvoiceWindow.class.getName();

		public SalesInvoiceWindow() {
			super(ID, SalesInvoice.class);
			setName("Sales Invoice");
			addVisibleField(SalesInvoice.PROPERTY_DOCUMENT_NO);
			addVisibleField(SalesInvoice.PROPERTY_TRANSACTION_DATE);
			addVisibleField(SalesInvoice.PROPERTY_BUSINESS_PARTNER);
			addVisibleField(SalesInvoice.PROPERTY_GRANDTOTAL);
			addOrderDesc(SalesInvoice.PROPERTY_TRANSACTION_DATE);
			addOrderDesc(SalesInvoice.PROPERTY_DOCUMENT_NO);
		}
	}

}
