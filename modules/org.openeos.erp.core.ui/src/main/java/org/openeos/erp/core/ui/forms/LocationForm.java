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

import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.BPresenter;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFSubForm;
import org.openeos.erp.core.model.Location;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.UIAbstractForm;
import org.openeos.services.ui.form.abstractform.UIPresenter;

public class LocationForm extends UIAbstractForm<Location> {

	public static final String ID = LocationForm.class.getPackage().getName() + ".LOCATION_FORM";
	public static final String NAME = "Location Form";

	public BFSubForm SUBFORM_FIRST = addSubForm(null, 1);
	public BFField FIELD_COUNTRY = SUBFORM_FIRST.addField(0, 0, null, "Country", Location.PROPERTY_COUNTRY);
	public BFField FIELD_ADDRESS1 = SUBFORM_FIRST.addField(1, 0, null, "Address 1", Location.PROPERTY_ADDRESS1);
	public BFField FIELD_ADDRESS2 = SUBFORM_FIRST.addField(2, 0, null, "Address 2", Location.PROPERTY_ADDRESS2);
	public BFField FIELD_CITY = SUBFORM_FIRST.addField(3, 0, null, "City", Location.PROPERTY_CITY);
	public BFField FIELD_POSTAL = SUBFORM_FIRST.addField(4, 0, null, "ZIP", Location.PROPERTY_POSTAL);
	public BFField FIELD_REGION = SUBFORM_FIRST.addField(5, 0, null, "Region", Location.PROPERTY_REGION).readOnlyPropertyName(
			Presenter.PROPERTY_REGION_VISIBLE);
	public BFField FIELD_REGIONNAME = SUBFORM_FIRST.addField(6, 0, null, "Region Name", Location.PROPERTY_REGIONNAME)
			.readOnlyPropertyName(Presenter.PROPERTY_REGION_NAME_VISIBLE);

	// TODO This can be more easy whith expression language
	public class Presenter extends UIPresenter<Location> {

		/**
		 * @param beanClass
		 * @param uiBean
		 */
		public Presenter(UIBean uiBean) {
			super(Location.class, uiBean);
			checkCountryRegion();
		}

		private boolean regionVisible = false;

		private static final String PROPERTY_REGION_VISIBLE = "regionVisible";
		private static final String PROPERTY_REGION_NAME_VISIBLE = "regionNameVisible";

		public boolean isRegionVisible() {
			return regionVisible;
		}

		public boolean isRegionNameVisible() {
			return !isRegionVisible();
		}

		@Override
		public Object getPropertyValue(String propertyName) {
			if (PROPERTY_REGION_VISIBLE.equals(propertyName)) {
				return isRegionVisible();
			} else if (PROPERTY_REGION_NAME_VISIBLE.equals(propertyName)) {
				return isRegionNameVisible();
			} else {
				return super.getPropertyValue(propertyName);
			}
		}

		@Override
		public void setPropertyValue(String propertyName, Object value) {
			super.setPropertyValue(propertyName, value);
			if (Location.PROPERTY_COUNTRY.equals(propertyName)) {
				checkCountryRegion();
			}
		}

		private void checkCountryRegion() {
			boolean oldValue = regionVisible;
			if (getModel() != null && getBeanWrapped().getCountry() != null) {
				regionVisible = getBeanWrapped().getCountry().isRegionDefined();
				firePropertyChange(PROPERTY_REGION_VISIBLE, oldValue, regionVisible);
				firePropertyChange(PROPERTY_REGION_NAME_VISIBLE, !oldValue, !regionVisible);
			}
		}

	}

	public LocationForm() {
		super(ID, NAME, Location.class);
	}

	@Override
	public BPresenter createPresenter(BFormInstance<UIBean, ?> formInstance, UIBean model) {
		Presenter presenter = new Presenter(model);
		return presenter;
	}

	@Override
	public EnumSet<BindingFormCapability> getCapabilities() {
		return EnumSet.of(BindingFormCapability.NEW, BindingFormCapability.EDIT);
	}

}
