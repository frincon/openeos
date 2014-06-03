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
package org.openeos.erp.core.ui.forms;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFSubForm;
import org.abstractform.binding.validation.Validator;
import org.abstractform.core.fluent.FField;
import org.openeos.erp.core.model.User;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.UIAbstractForm;

public abstract class UserForm extends UIAbstractForm<User> {

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_CLIENT = SUBFORM_MAIN.addField(0, 0, null, "Client", User.PROPERTY_CLIENT);
	public BFField FIELD_USERNAME = SUBFORM_MAIN.addField(0, 1, null, "User Name", User.PROPERTY_USERNAME);
	public BFField FIELD_PASSWORD = SUBFORM_MAIN.addField(1, 0, null, "Password", User.PROPERTY_PASSWORD)
			.type(FField.TYPE_PASSWORD);
	public FField FIELD_CONFIRM_PASSWORD = SUBFORM_MAIN.addField(1, 1, null, "Confirm Password").type(FField.TYPE_PASSWORD);
	public BFField FIELD_USERTYPE = SUBFORM_MAIN.addField(2, 0, null, "User Type", User.PROPERTY_USER_TYPE).readOnly(true);
	public BFField FIELD_LOCKED = SUBFORM_MAIN.addField(2, 1, null, "Locked", User.PROPERTY_LOCKED);

	public BFField FIELD_DEFAULT_CLIENT = SUBFORM_MAIN.addField(3, 0, null, "Default Client", User.PROPERTY_DEFAULT_CLIENT);
	public BFField FIELD_DEFAULT_ORG = SUBFORM_MAIN
			.addField(3, 1, null, "Default Organization", User.PROPERTY_DEFAULT_ORGANIZATION);

	public UserForm(String id, String name) {
		super(id, name, User.class);
		validator(new Validator<BFormInstance<UIBean, ?>>() {

			@Override
			public List<String> validate(BFormInstance<UIBean, ?> value) {
				String password1 = (String) value.getFieldValue(FIELD_PASSWORD.getId());
				String password2 = (String) value.getFieldValue(FIELD_CONFIRM_PASSWORD.getId());
				if (password1 != null) {
					if (!password1.equals(password2)) {
						return Arrays.asList("The password are not equals");
					}
				} else {
					if (password2 != null) {
						return Arrays.asList("The password are not equals");
					}
				}
				return null;
			}
		});
	}

	public static class UserEditForm extends UserForm {
		public static final String ID = UserEditForm.class.getName();
		public static final String NAME = "User Edit Form";

		public UserEditForm() {
			super(ID, NAME);
			this.FIELD_CLIENT.readOnly(true);
			this.FIELD_USERNAME.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

	public static class UserNewForm extends UserForm {
		public static final String ID = UserNewForm.class.getName();
		public static final String NAME = "User New Form";

		public UserNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}
	}

}
