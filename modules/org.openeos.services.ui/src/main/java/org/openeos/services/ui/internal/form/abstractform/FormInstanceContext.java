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

import org.abstractform.binding.BFormInstance;

import org.openeos.services.ui.UIContext;
import org.openeos.services.ui.internal.UIBeanImpl;

public class FormInstanceContext implements UIContext {

	private static final String PARENT_NAME = "parent";

	private UIContext parentContextObject;
	private BFormInstance<?> formInstance;

	public FormInstanceContext(UIContext parentContextObject, BFormInstance<?> formInstance) {
		this.parentContextObject = parentContextObject;
		this.formInstance = formInstance;
	};

	@Override
	public Object contextObject(String name) {
		if (name.equals(PARENT_NAME)) {
			Object value = formInstance.getValue();
			if (value instanceof UIBeanImpl) {
				return ((UIBeanImpl) value).getBeanWrapped();
			} else {
				return value;
			}
		} else {
			return parentContextObject.contextObject(name);
		}
	}

}
