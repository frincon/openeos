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
package org.openeos.erp.acct;

import org.openeos.erp.acct.AcctWindows.AccountCombinationWindow;
import org.openeos.erp.acct.AcctWindows.AccountTreeWindow;
import org.openeos.erp.acct.AcctWindows.GeneralLedgerCategoryWindow;
import org.openeos.erp.acct.AcctWindows.GeneralLedgerConfigurationWindow;
import org.openeos.erp.acct.AcctWindows.GeneralLedgerJournalWindow;
import org.openeos.erp.core.ui.CoreMenus;
import org.openeos.services.ui.menu.AbstractMenuProvider;
import org.openeos.services.ui.model.IMenuDefinition;
import org.openeos.services.ui.model.IMenuDefinition.MenuType;

public class AcctMenus extends AbstractMenuProvider {

	public static final String MENU_ACCOUNTING_TRANSACTION_ID = AcctMenus.class.getName() + ".MENU_ACCOUNTING_TRANSACTION_ID";

	// @formatter:off
	public IMenuDefinition MENU_ACCOUNTING_TRANSACTION = newMenu()
			.parent(CoreMenus.MENU_ACCOUNTING_ID)
			.action(MenuType.MENU)
			.name("Transactions")
			.description("Transactions Menu")
			.order(0)
			.build();

	public IMenuDefinition MENU_GENERAL_LEDGER_JOURNAL = newMenu()
			.parent(MENU_ACCOUNTING_TRANSACTION)
			.action(MenuType.WINDOW)
			.window(GeneralLedgerJournalWindow.ID)
			.name("G/L Journal Entries")
			.description("General Ledger Journal Entries Menu")
			.order(0)
			.build();
	
	public IMenuDefinition MENU_ACCOUNT_TREE = newMenu()
			.parent(CoreMenus.MENU_ACCOUNTING_SETUP_ID)
			.action(MenuType.WINDOW)
			.window(AccountTreeWindow.ID)
			.name("Account Tree")
			.description("Account Tree Menu")
			.order(-1000)
			.build();
	
	public IMenuDefinition MENU_GENERAL_LEDGER_CONFIGURATION = newMenu()
			.parent(CoreMenus.MENU_ACCOUNTING_SETUP_ID)
			.action(MenuType.WINDOW)
			.window(GeneralLedgerConfigurationWindow.ID)
			.name("General Ledger Configuration")
			.description("General Ledger Configuration Menu")
			.order(-950)
			.build();

	public IMenuDefinition MENU_GENERAL_LEDGER_CATEGORY = newMenu()
			.parent(CoreMenus.MENU_ACCOUNTING_SETUP_ID)
			.action(MenuType.WINDOW)
			.window(GeneralLedgerCategoryWindow.ID)
			.name("G/L Category")
			.description("General Ledger Category Menu")
			.order(-500)
			.build();

	public IMenuDefinition MENU_VALID_COMBINATIONS = newMenu()
			.parent(CoreMenus.MENU_ACCOUNTING_SETUP_ID)
			.action(MenuType.WINDOW)
			.window(AccountCombinationWindow.ID)
			.name("Valid Accounting Combinations")
			.description("Valid Accounting Combinations Menu")
			.order(-400)
			.build();

	
	// @formatter:on

}
