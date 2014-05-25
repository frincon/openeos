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
package org.openeos.erp.sales.ui;

import java.util.EnumSet;

import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.BPresenter;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFSection;
import org.abstractform.binding.fluent.BFSubForm;
import org.openeos.erp.core.model.Country;
import org.openeos.erp.sales.model.Tax;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.UIAbstractForm;
import org.openeos.services.ui.form.abstractform.UIPresenter;

public abstract class TaxForm extends UIAbstractForm<Tax> {

	public static final String PROPERTY_LINE_READ_ONLY = "lineReadOnly";
	public static final String PROPERTY_FROM_REGION_READ_ONLY = "fromRegionReadOnly";
	public static final String PROPERTY_TO_REGION_READ_ONLY = "toRegionReadOnly";

	public class Presenter extends UIPresenter<Tax> {

		private BFormInstance<UIBean> formInstance;

		private boolean lineReadOnly = true;
		private boolean fromRegionReadOnly = true;
		private boolean toRegionReadOnly = true;

		public Presenter(BFormInstance<UIBean> formInstance, UIBean uiBean) {
			super(Tax.class, uiBean);
			this.formInstance = formInstance;
			checkFromRegionReadOnly();
			checkToRegionReadOnly();
			checkLineReadOnly();
		}

		@Override
		public void fieldHasChanged(String fieldId) {
			if (fieldId.equals(FIELD_PARENT.getId())) {
				checkLineReadOnly();
			} else if (fieldId.equals(FIELD_FROM_COUNTRY.getId())) {
				checkFromRegionReadOnly();
			} else if (fieldId.equals(FIELD_TO_COUNTRY.getId())) {
				checkToRegionReadOnly();
			}
		}

		private void checkToRegionReadOnly() {
			Country country = (Country) formInstance.getFieldValue(FIELD_TO_COUNTRY.getId());
			firePropertyChange(PROPERTY_TO_REGION_READ_ONLY, toRegionReadOnly,
					toRegionReadOnly = (country == null || !country.isRegionDefined()));
		}

		private void checkFromRegionReadOnly() {
			Country country = (Country) formInstance.getFieldValue(FIELD_FROM_COUNTRY.getId());
			firePropertyChange(PROPERTY_FROM_REGION_READ_ONLY, fromRegionReadOnly,
					fromRegionReadOnly = (country == null || !country.isRegionDefined()));
		}

		private void checkLineReadOnly() {
			firePropertyChange(PROPERTY_LINE_READ_ONLY, lineReadOnly,
					lineReadOnly = formInstance.getFieldValue(FIELD_PARENT.getId()) == null);
		}

		public boolean isLineReadOnly() {
			return lineReadOnly;
		}

		public boolean isFromRegionReadOnly() {
			return fromRegionReadOnly;
		}

		public boolean isToRegionReadOnly() {
			return toRegionReadOnly;
		}

		@Override
		public Object getPropertyValue(String propertyName) {
			if (PROPERTY_FROM_REGION_READ_ONLY.equals(propertyName)) {
				return isFromRegionReadOnly();
			} else if (PROPERTY_TO_REGION_READ_ONLY.equals(propertyName)) {
				return isToRegionReadOnly();
			} else if (PROPERTY_LINE_READ_ONLY.equals(propertyName)) {
				return isLineReadOnly();
			} else {
				return super.getPropertyValue(propertyName);
			}
		}

	}

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(0, 0, null, "Organization", Tax.PROPERTY_ORGANIZATION);
	public BFField FIELD_NAME = SUBFORM_MAIN.addField(1, 0, null, "Name", Tax.PROPERTY_NAME);
	public BFField FIELD_PARENT = SUBFORM_MAIN.addField(2, 0, null, "Parent", Tax.PROPERTY_PARENT);
	public BFField FIELD_LINE = SUBFORM_MAIN.addField(2, 1, null, "Line", Tax.PROPERTY_LINE).readOnlyPresenterProperty(
			PROPERTY_LINE_READ_ONLY);
	public BFField FIELD_RATE = SUBFORM_MAIN.addField(3, 0, null, "Rate", Tax.PROPERTY_RATE);
	public BFField FIELD_VALID_FROM = SUBFORM_MAIN.addField(3, 1, null, "Valid From", Tax.PROPERTY_VALIDFROM);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description", Tax.PROPERTY_DESCRIPTION);
	public BFSection SECTION_DETAIL = addSection(null, "Detail");
	public BFSubForm SUBFORM_DETAIL = SECTION_DETAIL.addSubForm(null, 2);
	public BFField FIELD_FROM_COUNTRY = SUBFORM_DETAIL.addField(0, 0, null, "From Country", Tax.PROPERTY_COUNTRY_BY_C_COUNTRY_ID);
	public BFField FIELD_FROM_REGION = SUBFORM_DETAIL.addField(0, 1, null, "From Region", Tax.PROPERTY_REGION_BY_C_REGION_ID)
			.readOnlyPresenterProperty(PROPERTY_FROM_REGION_READ_ONLY);
	public BFField FIELD_TO_COUNTRY = SUBFORM_DETAIL.addField(1, 0, null, "To Country", Tax.PROPERTY_COUNTRY_BY_TO_C_COUNTRY_ID);
	public BFField FIELD_TO_REGION = SUBFORM_DETAIL.addField(1, 1, null, "To Region", Tax.PROPERTY_REGION_BY_TO_C_REGION_ID)
			.readOnlyPresenterProperty(PROPERTY_TO_REGION_READ_ONLY);
	public BFField FIELD_TAX_CATEGORY = SUBFORM_DETAIL.addField(2, 0, null, "Tax Category", Tax.PROPERTY_TAX_CATEGORY);
	public BFField FIELD_BP_TAX_CATEGORY = SUBFORM_DETAIL.addField(2, 1, null, "Business Partner Tax Category",
			Tax.PROPERTY__B_P_TAX_CATEGORY);

	public TaxForm(String id, String name) {
		super(id, name, Tax.class);
	}

	@Override
	public BPresenter createPresenter(BFormInstance<UIBean> formInstance, UIBean model) {
		return new Presenter(formInstance, model);
	}

	public static class TaxNewForm extends TaxForm {

		public static final String ID = TaxNewForm.class.getName();
		public static final String NAME = "Tax New Form";

		public TaxNewForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.NEW);
		}

	}

	public static class TaxEditForm extends TaxForm {

		public static final String ID = TaxEditForm.class.getName();
		public static final String NAME = "Tax Edit Form";

		public TaxEditForm() {
			super(ID, NAME);
		}

		@Override
		public EnumSet<BindingFormCapability> getCapabilities() {
			return EnumSet.of(BindingFormCapability.EDIT);
		}

	}

}
