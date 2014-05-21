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
package org.openeos.erp.core.model.list;

import org.openeos.dao.AbstractListType;
import org.openeos.dao.ListTypeAdditions;

public class UserType extends AbstractListType implements ListTypeAdditions {

	private static final long serialVersionUID = 1L;

	public static final UserType USERNAME_PASSWORD = new UserType("USERNAME_PASSWORD", "Common Username-Password User");

	public UserType(String value) {
		super(value);
	}

	public UserType(String value, String description) {
		super(value, description);
	}

}
