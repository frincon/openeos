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

import org.openeos.lanterna.Field;
import org.openeos.lanterna.ValueChangeListener;
import com.googlecode.lanterna.gui.component.Label;

public class LabelField extends Label implements Field {

	private Object custom;
	private Object value;
	
	@Override
	public void setValue(Object value) {
		this.value = value;
		setText(value==null?"NULL":value.toString());
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener valueChangeListener) {
		// Nothing to do (never change)
	}

	@Override
	public void removeValueChangeListener(ValueChangeListener valueChangeListener) {
		// Nothing to do (never change)
	}

	@Override
	public void setCustomObject(Object custom) {
		this.custom = custom;
	}

	@Override
	public Object getCustomObject() {
		return custom;
	}

}
