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
package org.openeos.services.security.internal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.openeos.erp.core.dao.UserDAO;
import org.openeos.services.security.Principal;
import org.openeos.services.security.SecurityManagerService;

public class SecurityManagerServiceImpl implements SecurityManagerService {

	private UserDAO userDAO;

	private class PrincipalImpl implements Principal {

		private String id;

		public PrincipalImpl(String id) {
			this.id = id;
		}

		@Override
		public String getId() {
			return id;
		}

	}

	public SecurityManagerServiceImpl(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public Principal getAuthenticatedPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			// TODO
			throw new RuntimeException("Actually not user is logged in");
		}

		Object principal = auth.getPrincipal();
		if (principal instanceof InternalUser) {
			InternalUser userDetails = (InternalUser) principal;
			return new PrincipalImpl(userDetails.getId());
		} else {
			throw new RuntimeException("The class of principal is unknown");
		}
	}

}
