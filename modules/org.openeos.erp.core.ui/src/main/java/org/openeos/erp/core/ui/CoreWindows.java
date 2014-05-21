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

import org.openeos.erp.core.model.BusinessPartner;
import org.openeos.erp.core.model.Client;
import org.openeos.erp.core.model.Organization;
import org.openeos.erp.core.model.User;
import org.openeos.services.ui.window.AbstractDictionaryBasedWindowDefinition;

public class CoreWindows {

	public static class UserWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = UserWindow.class.getName();

		public UserWindow() {
			super(ID, User.class);
			setName("User");
			addVisibleField(User.PROPERTY_CLIENT);
			addVisibleField(User.PROPERTY_USERNAME);
			addVisibleField(User.PROPERTY_USER_TYPE);
			addVisibleField(User.PROPERTY_LOCKED);
			addOrderAsc(User.PROPERTY_USERNAME);
		}

	}

	public static class ClientWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = ClientWindow.class.getName();

		public ClientWindow() {
			super(ID, Client.class);
			setName("Client");
			addVisibleField(Client.PROPERTY_VALUE);
			addVisibleField(Client.PROPERTY_NAME);
			addVisibleField(Client.PROPERTY_DESCRIPTION);
			addOrderAsc(Client.PROPERTY_VALUE);
		}
	}

	public static class OrganizationWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = OrganizationWindow.class.getName();

		public OrganizationWindow() {
			super(ID, Organization.class);
			setName("Organization");
			addVisibleField(Organization.PROPERTY_CLIENT);
			addVisibleField(Organization.PROPERTY_VALUE);
			addVisibleField(Organization.PROPERTY_NAME);
			addVisibleField(Organization.PROPERTY_DESCRIPTION);
			addOrderAsc(Organization.PROPERTY_VALUE);
		}
	}

	public static class BusinessPartnerWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = BusinessPartnerWindow.class.getName();

		public BusinessPartnerWindow() {
			super(ID, BusinessPartner.class);
			setName("Business Partner");
			addVisibleField(BusinessPartner.PROPERTY_ORGANIZATION);
			addVisibleField(BusinessPartner.PROPERTY_VALUE);
			addVisibleField(BusinessPartner.PROPERTY_NAME);
			addVisibleField(BusinessPartner.PROPERTY_NAME2);
			addVisibleField(BusinessPartner.PROPERTY_DESCRIPTION);
			addVisibleField(BusinessPartner.PROPERTY_TAXID);
			addOrderAsc(BusinessPartner.PROPERTY_VALUE);
		}
	}

}
