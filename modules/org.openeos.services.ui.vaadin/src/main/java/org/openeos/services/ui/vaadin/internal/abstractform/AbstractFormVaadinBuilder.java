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
package org.openeos.services.ui.vaadin.internal.abstractform;

import org.abstractform.binding.vaadin.VaadinBindingFormToolkit;
import org.abstractform.core.FormToolkit;

import com.vaadin.ui.Component;

// This class is built because the blueprint container not find the constructor
public class AbstractFormVaadinBuilder {

	@SuppressWarnings("unchecked")
	public VaadinBindingFormToolkit createFormToolkit(Object formToolkitDelegate) {
		return new VaadinBindingFormToolkit((FormToolkit<Component>) formToolkitDelegate);
	}
}
