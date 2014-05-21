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
package org.openeos.extender.ui.internal;

import java.util.List;

import org.osgi.framework.Bundle;

import org.openeos.extender.ui.Constants;
import org.openeos.services.ui.form.FormRegistryService;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;

@SuppressWarnings("rawtypes")
public class FormExtenderHandler extends RegisterClassListExtenderHandler<AbstractFormBindingForm, String> {

	private FormRegistryService formRegistryService;

	public void setFormRegistryService(FormRegistryService formRegistryService) {
		this.formRegistryService = formRegistryService;
	}

	@Override
	public String getHeaderName() {
		return Constants.HEADER_FORM_CLASSES;
	}

	@Override
	public void starting() {
		if (this.formRegistryService == null) {
			throw new UnsupportedOperationException("Not enought services");
		}

	}

	@Override
	public void stopping() {
		for (List<String> listToRemove : getAllRegisters()) {
			for (String key : listToRemove) {
				formRegistryService.unregisterForm(key);
			}
		}
	}

	@Override
	public void onBundleDeparture(Bundle bundle) {
		for (String key : getRegisters(bundle.getBundleId())) {
			formRegistryService.unregisterForm(key);
		}
	}

	@Override
	protected void processObject(Bundle bundle, AbstractFormBindingForm objectCreated) {
		formRegistryService.registerForm(objectCreated.getClass().toString(), objectCreated);
		addRegister(bundle.getBundleId(), objectCreated.getClass().toString());
	}

	@Override
	protected Class<AbstractFormBindingForm> getExpectedClass() {
		return AbstractFormBindingForm.class;
	}

}
