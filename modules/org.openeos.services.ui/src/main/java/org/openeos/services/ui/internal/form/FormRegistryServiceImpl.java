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
package org.openeos.services.ui.internal.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.ui.form.BindingForm;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.FormRegistryService;

public class FormRegistryServiceImpl implements FormRegistryService {

	private static final Logger LOG = LoggerFactory.getLogger(FormRegistryServiceImpl.class);

	@SuppressWarnings("rawtypes")
	private Map<String, BindingForm> formMap = new HashMap<String, BindingForm>();

	@SuppressWarnings("rawtypes")
	@Override
	public void registerForm(String id, BindingForm form) {
		LOG.debug("Registering form with id " + id + "form capabilities " + form.getCapabilities());
		formMap.put(id, form);
	}

	@Override
	public void unregisterForm(String id) {
		LOG.debug("Unregistering form with id " + id);
		formMap.remove(id);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T, T2 extends BindingForm<T>> T2 getDefaultForm(Class<T> clazz, Class<T2> bindingFormClass,
			BindingFormCapability capability) {

		BindingForm foundForm = null;
		for (BindingForm form : formMap.values()) {
			// TODO Make especific class more priority than super classes
			if (bindingFormClass.isAssignableFrom(form.getClass()) && form.getBeanClass().isAssignableFrom(clazz)
					&& form.getCapabilities().contains(capability)) {
				if (foundForm == null || foundForm.getRanking() < form.getRanking()) {
					foundForm = form;
				}
			}
		}
		return (T2) foundForm;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T, T2 extends BindingForm<T>> T2[] getAllForms(Class<T> clazz, Class<T2> bindingFormClass,
			BindingFormCapability capability) {
		List<T2> formList = new ArrayList<T2>();
		for (BindingForm form : formMap.values()) {
			// TODO Make especific class more priority than super classes
			if (form.getBeanClass().isAssignableFrom(clazz) && form.getCapabilities().contains(capability)) {
				formList.add((T2) form);
			}
		}
		return (T2[]) formList.toArray();
	}

}
