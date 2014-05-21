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

import org.openeos.erp.core.INameValue;
import org.openeos.erp.core.dao.NameValueFinder;
import org.openeos.lanterna.Field;
import org.openeos.lanterna.ValueChangeEvent;
import org.openeos.lanterna.ValueChangeListener;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.InteractableComponent;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.gui.listener.ComponentListener;

public class NameValueField extends Panel implements Field {

	private static final int WIDTH_VALUE = 6;
	private static final int WIDTH_SPACE = 1;
	private static final int WIDTH_MIN_NAME = 10;

	private NameValueFinder nameValueFinder;

	private TextBox textValue;
	private TextBox textName;
	private int width;

	private INameValue value;
	private Class<INameValue> searchClass;
	private Object custom;
	private CopyOnWriteArraySet<ValueChangeListener> listenerSet = new CopyOnWriteArraySet<ValueChangeListener>();

	public NameValueField(NameValueFinder nameValueFinder, Class<INameValue> searchClass, int width) {
		super(new Border.Invisible(), Orientation.HORISONTAL);
		this.nameValueFinder = nameValueFinder;
		this.searchClass = searchClass;
		if (width < WIDTH_SPACE + WIDTH_VALUE + WIDTH_MIN_NAME) {
			this.width = WIDTH_SPACE + WIDTH_VALUE + WIDTH_MIN_NAME;
		} else {
			this.width = width;
		}

		createFields();
		invalidate();
	}

	private void createFields() {
		textValue = new TextBox("", WIDTH_VALUE);
		textValue.addComponentListener(new ComponentListener() {

			@Override
			public void onComponentReceivedFocus(InteractableComponent interactableComponent) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComponentLostFocus(InteractableComponent interactableComponent) {
				searchValue();
			}

			@Override
			public void onComponentInvalidated(Component component) {
				// TODO Auto-generated method stub

			}
		});
		addComponent(textValue);

		EmptySpace empty = new EmptySpace(WIDTH_SPACE, 1);
		addComponent(empty);

		textName = new TextBox("", width - WIDTH_SPACE - WIDTH_VALUE);
		addComponent(textName);
	}

	@Override
	public void setValue(Object value) {
		if (value != null && !(value instanceof INameValue)) {
			throw new IllegalArgumentException("The value must be INameValue instance.");
		}
		if (value == null) {
			this.value = null;
		} else {
			this.value = (INameValue) value;
		}
		refreshTextBoxes();
	}

	@Override
	public Object getValue() {
		return value;
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
		return custom;
	}

	private void searchValue() {
		String newValue = textValue.getText().trim();
		if (newValue.equals(value == null ? "" : value.getValue())) {
			refreshTextBoxes();
		} else if (newValue.length() == 0) {
			fireChangeEvent(null);
			refreshTextBoxes();
		} else {
			INameValue newObject = nameValueFinder.findByValue(searchClass, newValue);
			fireChangeEvent(newObject);
			refreshTextBoxes();
		}
	}

	private void refreshTextBoxes() {
		if (value == null) {
			this.textName.setText("");
			this.textValue.setText("");
		} else {
			this.textName.setText(value.getName());
			this.textValue.setText(value.getValue());
		}
	}

	private void fireChangeEvent(INameValue newValue) {
		INameValue oldValue = value;
		this.value = newValue;
		ValueChangeEvent evt = new ValueChangeEvent(this, oldValue, newValue);
		for (ValueChangeListener listener : listenerSet) {
			listener.valueChange(evt);
		}
	}

}
