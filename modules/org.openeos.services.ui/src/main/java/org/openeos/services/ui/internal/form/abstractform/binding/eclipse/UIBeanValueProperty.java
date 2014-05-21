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
package org.openeos.services.ui.internal.form.abstractform.binding.eclipse;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;

import org.openeos.services.ui.UIBean;

public class UIBeanValueProperty extends SimpleValueProperty {

	private String propertyName;
	private Class valueClass;

	public UIBeanValueProperty(String propertyName, Class valueClass) {
		this.propertyName = propertyName;
		this.valueClass = valueClass;
	}

	@Override
	public Object getValueType() {
		return valueClass;
	}

	@Override
	protected Object doGetValue(Object source) {
		if (source instanceof UIBean) {
			UIBean uiBean = (UIBean) source;
			return uiBean.get(propertyName);
		} else {
			throw new UnsupportedOperationException("The source is not a UIBean");
		}
	}

	@Override
	protected void doSetValue(Object source, Object value) {
		if (source instanceof UIBean) {
			UIBean uiBean = (UIBean) source;
			uiBean.set(propertyName, value);
		} else {
			throw new UnsupportedOperationException("The source is not a UIBean");
		}
	}

	@Override
	public INativePropertyListener adaptListener(ISimplePropertyListener listener) {
		return new UIBeanAbstractPropertyListener(this, propertyName, listener) {
			@Override
			protected IDiff computeDiff(Object oldValue, Object newValue) {
				return Diffs.createValueDiff(oldValue, newValue);
			}
		};
	}

}
