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
package org.openeos.services.ui.internal.form.abstractform;

import java.util.EnumSet;
import java.util.Map;

import org.abstractform.binding.BForm;
import org.osgi.framework.Constants;

import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.FormRegistryService;
import org.openeos.services.ui.form.abstractform.AbstractFormUtils;

public class AbstractFormRegistry {

	private static final String PREFIX = AbstractFormRegistry.class.getName() + ".";

	private FormRegistryService formRegistryService;

	public void setFormRegistryService(FormRegistryService formRegistryService) {
		this.formRegistryService = formRegistryService;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void bindAbstractBForm(BForm form, Map properties) {
		EnumSet<BindingFormCapability> capabilities = (EnumSet<BindingFormCapability>) properties
				.get(AbstractFormUtils.SERVICE_CAPABILITIES);
		Integer formRanking = (Integer) properties.get(Constants.SERVICE_RANKING);
		AbstractFormBindingFormImpl bindingForm = new AbstractFormBindingFormImpl(form, formRanking, capabilities);
		formRegistryService.registerForm(PREFIX + form.getId(), bindingForm);
	}

	@SuppressWarnings("rawtypes")
	public void unbindAbstractBForm(BForm form) {
		if (form != null) {
			formRegistryService.unregisterForm(PREFIX + form.getId());
		}
	}

}
