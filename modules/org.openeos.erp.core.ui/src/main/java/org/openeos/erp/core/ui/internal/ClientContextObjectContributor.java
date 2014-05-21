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
package org.openeos.erp.core.ui.internal;

import org.openeos.erp.core.dao.UserDAO;
import org.openeos.erp.core.model.User;
import org.openeos.services.security.SecurityManagerService;
import org.openeos.services.ui.ContextObjectContainer;
import org.openeos.services.ui.ContextObjectContributor;

public class ClientContextObjectContributor implements ContextObjectContributor {

	public static final String CONTEXT_OBJECT_CLIENT_NAME = "Client";

	private UserDAO userDAO;
	private SecurityManagerService securityManagerService;

	public ClientContextObjectContributor(UserDAO userDAO, SecurityManagerService securityManagerService) {
		this.userDAO = userDAO;
		this.securityManagerService = securityManagerService;
	}

	@Override
	public void contributeInitialContextObjects(ContextObjectContainer container) {
		User user = userDAO.read(securityManagerService.getAuthenticatedPrincipal().getId());
		if (user != null) {
			if (user.getDefaultClient() != null) {
				container.setContextObject(CONTEXT_OBJECT_CLIENT_NAME, user.getDefaultClient());
			} else {
				container.setContextObject(CONTEXT_OBJECT_CLIENT_NAME, user.getClient());
			}
		}
	}

}
