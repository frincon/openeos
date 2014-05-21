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
package org.openeos.services.ui.vaadin.internal.containers;

import org.openeos.services.ui.UIBean;
import com.vaadin.data.util.AbstractProperty;

public class UIDAOProperty extends AbstractProperty {

	private UIBean bean;
	private String propertyName;

	public UIDAOProperty(UIBean bean, String propertyName) {
		this.bean = bean;
		this.propertyName = propertyName;
	}

	@Override
	public Object getValue() {
		return bean.get(propertyName);
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
		bean.set(propertyName, newValue);
	}

	@Override
	public Class<?> getType() {
		return bean.getType(propertyName);
	}
}
