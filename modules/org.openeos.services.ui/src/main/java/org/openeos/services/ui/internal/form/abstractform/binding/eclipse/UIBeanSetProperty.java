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

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IDiff;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.set.SimpleSetProperty;

import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.internal.UIBeanImpl;

public class UIBeanSetProperty extends SimpleSetProperty {

	private String propertyName;
	private String parentPropertyName;
	private Class<?> elementClass;

	public UIBeanSetProperty(String propertyName, Class<?> elementClass, String parentPropertyName) {
		this.propertyName = propertyName;
		this.elementClass = elementClass;
		this.parentPropertyName = parentPropertyName;
	}

	@Override
	public Object getElementType() {
		return elementClass;
	}

	@Override
	protected Set<?> doGetSet(Object source) {
		if (source instanceof UIBean) {
			UIBean uiBean = (UIBean) source;
			return (Set<?>) uiBean.get(propertyName);
		} else {
			throw new UnsupportedOperationException("The source is not a UIBean");
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void doSetSet(Object source, Set set, SetDiff diff) {
		for (Object obj : diff.getAdditions()) {
			BeanProperties.value(parentPropertyName).setValue(obj, ((UIBeanImpl) source).getBeanWrapped());
			System.out.println("Falta una cosa");
		}
		diff.applyTo(doGetSet(source));
	}

	@Override
	public INativePropertyListener adaptListener(ISimplePropertyListener listener) {
		return new UIBeanAbstractPropertyListener(this, propertyName, listener) {

			@Override
			protected IDiff computeDiff(Object oldValue, Object newValue) {
				return Diffs.computeSetDiff(asSet(oldValue), asSet(newValue));
			}
		};
	}

	private Set<?> asSet(Object propertyValue) {
		if (propertyValue == null)
			return Collections.EMPTY_SET;
		return (Set<?>) propertyValue;
	}

}
