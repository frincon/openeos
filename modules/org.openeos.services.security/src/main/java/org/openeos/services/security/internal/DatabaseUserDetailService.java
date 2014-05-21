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

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.openeos.erp.core.dao.UserDAO;
import org.openeos.erp.core.model.User;

public class DatabaseUserDetailService implements UserDetailsService {

	private UserDAO userDAO;
	private GrantedAuthorityManager grantedAuthorityManager;

	public DatabaseUserDetailService(UserDAO userDAO, GrantedAuthorityManager grantedAuthorityManager) {
		this.userDAO = userDAO;
		this.grantedAuthorityManager = grantedAuthorityManager;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDAO.findByUsernameLockedState(username, false);
		if (user == null) {
			throw new UsernameNotFoundException("The user name not found in database.");
		}
		InternalUser userDetail = new InternalUser(user.getId(), user.getUsername(), user.getPassword(),
				grantedAuthorityManager.getAuthorities(user));
		return userDetail;
	}

}
