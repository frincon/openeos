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

import java.util.EnumSet;

import org.abstractform.binding.BForm;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFForm;
import org.abstractform.binding.fluent.BFSubForm;

import org.openeos.erp.core.model.Client;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;

public abstract class ClientForm extends BFForm<Client> implements AbstractFormBindingForm<Client> {

	public static final Integer RANKING = 100;

	// Form DefinitionF
	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_VALUE = SUBFORM_MAIN.addField(0, 0, null, "Search Key", Client.PROPERTY_VALUE);
	public BFField FIELD_NAME = SUBFORM_MAIN.addField(0, 1, null, "Name", Client.PROPERTY_NAME);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description", Client.PROPERTY_DESCRIPTION);

	public ClientForm(String id, String name) {
		super(id, name, Client.class);
	}

	@Override
	public Integer getRanking() {
		return RANKING;
	}

	@Override
	public BForm<Client> getBForm() {
		return this;
	}

	public static class ClientNewForm extends ClientForm {

		public static final String NAME = "New Client Form";
		public static final String ID = ClientForm.class.getPackage().getName() + ".FORM_NEW_CLIENT";

		public ClientNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

	}

	public static class ClientEditForm extends ClientForm {

		public static final String NAME = "Edit Client Form";
		public static final String ID = ClientEditForm.class.getPackage().getName() + ".FORM_EDIT_CLIENT";

		public ClientEditForm() {
			super(ID, NAME);
			FIELD_VALUE.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

}
