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
import org.abstractform.binding.fluent.BFDrawer;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFForm;
import org.abstractform.binding.fluent.BFSubForm;
import org.abstractform.binding.fluent.table.BFTable;
import org.abstractform.binding.fluent.table.BFTableField;

import org.openeos.erp.core.model.BPLocation;
import org.openeos.erp.core.model.BusinessPartner;
import org.openeos.erp.core.model.Location;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;
import org.openeos.services.ui.form.abstractform.BFUIButton;
import org.openeos.services.ui.form.abstractform.BFUITable;
import org.openeos.services.ui.form.abstractform.UIButtonController;

public abstract class BusinessPartnerForm extends BFForm<BusinessPartner> implements AbstractFormBindingForm<BusinessPartner> {

	public static final Integer RANKING = 100;

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGAINZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization", BusinessPartner.PROPERTY_ORGANIZATION);
	public BFField FIELD_VALUE = SUBFORM_MAIN.addField(1, 0, null, "Search Key", BusinessPartner.PROPERTY_VALUE);
	public BFField FIELD_TAXID = SUBFORM_MAIN.addField(1, 1, null, "Tax Id", BusinessPartner.PROPERTY_TAXID);

	public BFField FIELD_NAME = SUBFORM_MAIN.addField(3, 0, null, "Name", BusinessPartner.PROPERTY_NAME);
	public BFField FIELD_NAME2 = SUBFORM_MAIN.addField(3, 1, null, "Fiscal Name", BusinessPartner.PROPERTY_NAME2);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION
			.addField(0, 0, null, "Description", BusinessPartner.PROPERTY_DESCRIPTION);

	public BFDrawer DRAWER_LOCATION = addDrawer(null, "Locations");
	public BFSubForm SUBFORM_LOCATION = DRAWER_LOCATION.addSubForm(null, 1);
	public BFTable TABLE_LOCATION = SUBFORM_LOCATION
			.addField(0, 0, null, "Locations", BusinessPartner.PROPERTY__B_P_LOCATIONS, BFUITable.class).pageLenght(3)
			.elementsClass(BPLocation.class);
	public BFTableField FIELD_BPL_ORGANIZATION = TABLE_LOCATION.addTableField("Organization", BPLocation.PROPERTY_ORGANIZATION);
	public BFTableField FIELD_BPL_NAME = TABLE_LOCATION.addTableField("Name", BPLocation.PROPERTY_NAME);
	public BFTableField FIELD_BPL_ISBILLTO = TABLE_LOCATION.addTableField("Is Bill To", BPLocation.PROPERTY_BILL_TO);

	public BusinessPartnerForm(String id, String name) {
		super(id, name, BusinessPartner.class);
	}

	@Override
	public Integer getRanking() {
		return RANKING;
	}

	@Override
	public BForm<BusinessPartner> getBForm() {
		return this;
	}

	public static class BusinessPartnerNewForm extends BusinessPartnerForm {

		public static final String NAME = "New Business Partner Form";
		public static final String ID = BusinessPartnerForm.class.getPackage().getName() + ".FORM_NEW_BUSINESSPARTNER";

		public BusinessPartnerNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

	}

	public static class BusinessPartnerEditForm extends BusinessPartnerForm {

		public static final String NAME = "Edit Business Partner Form";
		public static final String ID = BusinessPartnerForm.class.getPackage().getName() + ".FORM_EDIT_BUSINESSPARTNER";

		public BusinessPartnerEditForm() {
			super(ID, NAME);
			FIELD_VALUE.readOnly(true);
			FIELD_ORGAINZATION.readOnly(true);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}
	}

	public static abstract class BPLocationForm extends BFForm<BPLocation> implements AbstractFormBindingForm<BPLocation> {

		public BFSubForm SUBFORM_MAIN = addSubForm(null, 1);
		public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization", BPLocation.PROPERTY_ORGANIZATION);
		public BFField FIELD_NAME = SUBFORM_MAIN.addField(1, 0, null, "Name", BPLocation.PROPERTY_NAME);
		public BFField FIELD_ISBILLTO = SUBFORM_MAIN.addField(2, 0, null, "Is Bill To", BPLocation.PROPERTY_BILL_TO);
		public BFField FIELD_LOCATION = SUBFORM_MAIN.addField(3, 0, null, "Location", BPLocation.PROPERTY_LOCATION,
				BFUIButton.class).buttonController(new UIButtonController<Location>() {

			@Override
			public void onClick(UIApplication application, Location model) {
				application.showEditFormDialog(Location.class, model);
			}
		});

		public BPLocationForm(String id, String name) {
			super(id, name, BPLocation.class);
		}

		@Override
		public Integer getRanking() {
			return RANKING;
		}

		@Override
		public BForm<BPLocation> getBForm() {
			return this;
		}

		public static class New extends BPLocationForm {
			public static final String NAME = "New Business Partner Location Form";
			public static final String ID = BusinessPartnerForm.class.getPackage().getName() + ".FORM_NEW_BLLOCATION";

			public New() {
				super(ID, NAME);
			}

			@Override
			public EnumSet<BindingFormCapability> getCapabilities() {
				return EnumSet.of(BindingFormCapability.NEW);
			}
		}

		public static class Edit extends BPLocationForm {
			public static final String NAME = "Edit Business Partner Location Form";
			public static final String ID = BusinessPartnerForm.class.getPackage().getName() + ".FORM_EDIT_BPLOCATION";

			public Edit() {
				super(ID, NAME);
				FIELD_ORGANIZATION.readOnly(true);
				FIELD_LOCATION.readOnly(true);
			}

			@Override
			public EnumSet<BindingFormCapability> getCapabilities() {
				return EnumSet.of(BindingFormCapability.EDIT);
			}
		}

	}

}
