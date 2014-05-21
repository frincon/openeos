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
package org.openeos.erp.core.ui;

import org.openeos.erp.core.ui.actions.ChangeProfileAction;
import org.openeos.services.ui.menu.AbstractMenuProvider;
import org.openeos.services.ui.model.IMenuDefinition;
import org.openeos.services.ui.model.IMenuDefinition.MenuType;

public class CoreMenus extends AbstractMenuProvider {

	public static final String MENU_FINANCIAL_MANAGEMENT_ID = CoreMenus.class.getName() + ".MENU_FINANCIAL_MANAGEMENT_ID";
	public static final String MENU_ACCOUNTING_ID = CoreMenus.class.getName() + ".MENU_ACCOUNTING_ID";
	public static final String MENU_ACCOUNTING_SETUP_ID = CoreMenus.class.getName() + ".MENU_ACCOUNTING_SETUP_ID";

	public static final String MENU_GENERAL_SETUP_ID = CoreMenus.class.getName() + ".MENU_GENERAL_SETUP";
	public static final String MENU_CLIENT_GROUP_ID = CoreMenus.class.getName() + ".MENU_CLIENT_GROUP_ID";
	public static final String MENU_CLIENT_ID = CoreMenus.class.getName() + ".MENU_CLIENT_ID";
	public static final String MENU_ORGANIZATION_ID = CoreMenus.class.getName() + ".MENU_ORGANIZATION_ID";
	public static final String MENU_SECURITY_ID = CoreMenus.class.getName() + ".MENU_SECURITY";
	public static final String MENU_USERS_ID = CoreMenus.class.getName() + ".MENU_USERS";

	public static final String MENU_MASTER_DATA_ID = CoreMenus.class.getName() + ".MENU_MASTER_DATA_ID";
	public static final String MENU_BUSINESS_PARTNER_ID = CoreMenus.class.getName() + ".MENU_BUSINESS_PARTNER_ID";
	public static final String MENU_CHANGE_PROFILE_ID = CoreMenus.class.getName() + ".MENU_BUSINESS_PARTNER_ID";

	public IMenuDefinition MENU_FINANCIAL_MANAGEMENT = newMenu().id(MENU_FINANCIAL_MANAGEMENT_ID).action(MenuType.MENU)
			.name("Financial Management").description("Financial Management Menu").order(2000).build();
	public IMenuDefinition MENU_ACCOUNTING = newMenu().id(MENU_ACCOUNTING_ID).parent(MENU_FINANCIAL_MANAGEMENT)
			.action(MenuType.MENU).name("Accounting").description("Accounting").order(100).build();
	public IMenuDefinition MENU_ACCOUNTING_SETUP = newMenu().id(MENU_ACCOUNTING_SETUP_ID).parent(MENU_ACCOUNTING)
			.action(MenuType.MENU).name("Setup").description("Accounting Setup Menu").order(100).build();

	public IMenuDefinition MENU_GENERAL_SETUP = newMenu().id(MENU_GENERAL_SETUP_ID).order(0).action(MenuType.MENU)
			.name("General Setup").description("General Setup Menu").build();
	public IMenuDefinition MENU_CLIENT_GROUP = newMenu().id(MENU_CLIENT_GROUP_ID).order(0).action(MenuType.MENU).name("Client")
			.description("Client Setup").parent(MENU_GENERAL_SETUP).build();
	public IMenuDefinition MENU_CLIENT = newMenu().id(MENU_CLIENT_ID).order(0).action(MenuType.WINDOW).name("Client")
			.description("Client Menu").parent(MENU_CLIENT_GROUP).window(CoreWindows.ClientWindow.ID).build();
	public IMenuDefinition MENU_ORGANIZATION = newMenu().id(MENU_ORGANIZATION_ID).order(1000).action(MenuType.WINDOW)
			.name("Organization").description("Organization Menu").parent(MENU_CLIENT_GROUP)
			.window(CoreWindows.OrganizationWindow.ID).build();
	public IMenuDefinition MENU_SECURITU = newMenu().id(MENU_SECURITY_ID).order(1000).action(MenuType.MENU).name("Security")
			.description("Security Menu").parent(MENU_GENERAL_SETUP).build();
	public IMenuDefinition MENU_USERS = newMenu().id(MENU_USERS_ID).order(0).action(MenuType.WINDOW).name("Users")
			.description("Users Menu").parent(MENU_SECURITU).window(CoreWindows.UserWindow.ID).build();

	public IMenuDefinition MENU_MASTER_DATA = newMenu().id(MENU_MASTER_DATA_ID).order(1000).action(MenuType.MENU)
			.name("Mater Data").description("Master Data Menu").build();
	public IMenuDefinition MENU_BUSINESS_PARTNER = newMenu().id(MENU_BUSINESS_PARTNER_ID).order(0).action(MenuType.WINDOW)
			.name("Business Partner").description("Business Partner Menu").parent(MENU_MASTER_DATA)
			.window(CoreWindows.BusinessPartnerWindow.ID).build();

	public IMenuDefinition MENU_CHANGE_PROFILE = newMenu().id(MENU_CHANGE_PROFILE_ID).parent(MENU_GENERAL_SETUP).order(-5000)
			.action(MenuType.CUSTOM).name("Change Profile").description("Change Profile Menu").customAction(ChangeProfileAction.ID)
			.build();

}
