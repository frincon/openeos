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
package org.openeos.lanterna.internal.ui.field;

import java.util.concurrent.CopyOnWriteArraySet;

import org.openeos.lanterna.Field;
import org.openeos.lanterna.ValueChangeEvent;
import org.openeos.lanterna.ValueChangeListener;
import com.googlecode.lanterna.gui.component.TextBox;

public class TextBoxField extends TextBox implements Field {

	private String value;
	private Object custom;
	private CopyOnWriteArraySet<ValueChangeListener> listenerSet = new CopyOnWriteArraySet<ValueChangeListener>();

	public TextBoxField(int width) {
		super("", width);
	}

	@Override
	public void setValue(Object value) {
		if (value != null && !(value instanceof String)) {
			throw new IllegalArgumentException("The value must be a String");
		}
		if (value != null) {
			this.setText((String) value);
		} else {
			this.setText("");
		}
		this.value = (String) value;
	}

	@Override
	public Object getValue() {
		return this.getText();
	}

	@Override
	public void addValueChangeListener(ValueChangeListener valueChangeListener) {
		this.listenerSet.add(valueChangeListener);
	}

	@Override
	public void removeValueChangeListener(ValueChangeListener valueChangeListener) {
		this.listenerSet.remove(valueChangeListener);
	}

	@Override
	public void setCustomObject(Object custom) {
		this.custom = custom;
	}

	@Override
	public Object getCustomObject() {
		return this.custom;
	}

	@Override
	protected void afterLeftFocus(FocusChangeDirection direction) {
		super.afterLeftFocus(direction);

		//Check if value has changed
		String actualText = this.getText().trim();
		if (actualText.length() == 0) {
			if (value != null && value.trim().length() != 0) {
				fireChangeEvent(null);
			}
		} else {
			if (value == null || !(value.trim().equals(actualText))) {
				fireChangeEvent(actualText);
			}
		}
	}

	private void fireChangeEvent(String newValue) {
		String oldValue = value;
		this.value = newValue;
		ValueChangeEvent evt = new ValueChangeEvent(this, oldValue, newValue);
		for (ValueChangeListener listener : listenerSet) {
			listener.valueChange(evt);
		}
	}

}
