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

import org.openeos.erp.core.ui.CoreMenus;
import org.openeos.erp.sales.ui.SalesWindows.BPTaxCategoryWindow;
import org.openeos.erp.sales.ui.SalesWindows.SalesInvoiceWindow;
import org.openeos.erp.sales.ui.SalesWindows.TaxCategoryWindow;
import org.openeos.erp.sales.ui.SalesWindows.TaxWindow;
import org.openeos.services.ui.menu.AbstractMenuProvider;
import org.openeos.services.ui.model.IMenuDefinition;
import org.openeos.services.ui.model.IMenuDefinition.MenuType;

public class SalesMenus extends AbstractMenuProvider {

	public IMenuDefinition MENU_BP_TAX_CATEGORY = newMenu().parent(CoreMenus.MENU_ACCOUNTING_SETUP_ID).action(MenuType.WINDOW)
			.window(BPTaxCategoryWindow.ID).name("Business Partner Tax Category").description("Business Partner Tax Category Menu")
			.order(100).build();
	public IMenuDefinition MENU_TAX_CATEGORY = newMenu().parent(CoreMenus.MENU_ACCOUNTING_SETUP_ID).action(MenuType.WINDOW)
			.window(TaxCategoryWindow.ID).name("Tax Category").description("Tax Category Menu").order(200).build();
	public IMenuDefinition MENU_TAX = newMenu().parent(CoreMenus.MENU_ACCOUNTING_SETUP_ID).action(MenuType.WINDOW)
			.window(TaxWindow.ID).name("Tax").description("Tax Menu").order(300).build();

	public IMenuDefinition MENU_SALES = newMenu().action(MenuType.MENU).name("Sales").description("Sales Menu").order(1500).build();
	public IMenuDefinition MENU_SALES_TRANSACTIONS = newMenu().parent(MENU_SALES).action(MenuType.MENU).name("Transactions")
			.description("Sales Transactions Menu").order(100).build();
	public IMenuDefinition MENU_SALES_INVOICE = newMenu().parent(MENU_SALES_TRANSACTIONS).action(MenuType.WINDOW)
			.window(SalesInvoiceWindow.ID).name("Sales Invoice").description("Sales Invoice Menu").order(100).build();

}
