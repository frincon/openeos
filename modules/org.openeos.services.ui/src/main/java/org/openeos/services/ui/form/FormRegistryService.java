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
package org.openeos.services.ui.form;

public interface FormRegistryService {

	/**
	 * Return the default form for class and capabilities based on ranking.
	 * 
	 * @param clazz
	 * @param capability
	 * @return The default form. null if not found
	 */
	public <T, T2 extends BindingForm<T>> T2 getDefaultForm(Class<T> beanClass, Class<T2> bindingFormClass,
			BindingFormCapability capability);

	public <T, T2 extends BindingForm<T>> T2[] getAllForms(Class<T> beanClass, Class<T2> bindingFormClass,
			BindingFormCapability capability);

	void registerForm(String id, BindingForm<?> form);

	void unregisterForm(String id);

}
