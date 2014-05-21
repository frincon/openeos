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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.observable.IDiff;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.NativePropertyListener;

import org.openeos.services.ui.UIBean;

public abstract class UIBeanAbstractPropertyListener extends NativePropertyListener implements PropertyChangeListener {

	private String propertyName;

	public UIBeanAbstractPropertyListener(IProperty property, String propertyName, ISimplePropertyListener listener) {
		super(property, listener);
		this.propertyName = propertyName;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName() == null || evt.getPropertyName().equalsIgnoreCase(propertyName)) {
			Object oldValue = evt.getOldValue();
			Object newValue = evt.getNewValue();
			IDiff diff;
			if (evt.getPropertyName() == null || oldValue == null || newValue == null)
				diff = null;
			else
				diff = computeDiff(oldValue, newValue);
			fireChange(evt.getSource(), diff);
		}
	}

	protected abstract IDiff computeDiff(Object oldValue, Object newValue);

	@Override
	protected void doAddTo(Object source) {
		((UIBean) source).addPropertyChangeListener(propertyName, this);
	}

	@Override
	protected void doRemoveFrom(Object source) {
		((UIBean) source).removePropertyChangeListener(propertyName, this);
	}

}
