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
package org.openeos.erp.core.model.aspects;

import org.openeos.erp.core.model.User;
import org.openeos.erp.core.model.Contact;
import org.openeos.erp.core.model.list.UserType;
import org.openeos.services.dictionary.DefaultValue;
import org.openeos.services.dictionary.validation.annotations.SQLValidation;

import org.hibernate.annotations.Cascade;

public aspect UserDefaultValues {

	declare @method: public * User.getClient() : @DefaultValue("contextObject('Client')");

	declare @method: public * User.getContact() : @Cascade(org.hibernate.annotations.CascadeType.ALL);
	
	declare @method: public * User.getDefaultOrganization() : @SQLValidation("{alias}.ad_client_id=@source.defaultClient.id@");

	after(User user) : execution(User.new(..)) && this(user) {
		if (user.getUserType() == null) {
			user.setUserType(UserType.USERNAME_PASSWORD);
		}
		if (user.getContact() == null) {
			user.setContact(new Contact());
		}
	}

}
