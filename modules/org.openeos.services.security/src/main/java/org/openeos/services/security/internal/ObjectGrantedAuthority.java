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

import org.springframework.security.core.GrantedAuthority;

public class ObjectGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = -346472526823039866L;

	public static final String PREFIX = "OBJECT_GRANTED_AUTHORITY_";

	private Class<?> objectClass;
	private String objectId;

	public ObjectGrantedAuthority(Class<?> objectClass, String objectId) {
		this.objectClass = objectClass;
		this.objectId = objectId;
	}

	@Override
	public String getAuthority() {
		return PREFIX + objectClass.getName() + "('" + objectId + "')";
	}

}
