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
package org.openeos.lanterna.internal.ui;

import org.openeos.erp.core.INameValue;
import org.openeos.erp.core.dao.NameValueFinder;
import org.openeos.lanterna.Field;
import org.openeos.lanterna.LanternaEditor;
import org.openeos.lanterna.internal.ui.field.LabelField;
import org.openeos.lanterna.internal.ui.field.NameValueField;
import org.openeos.lanterna.internal.ui.field.TextBoxField;
import org.openeos.services.ui.Editor;
import org.openeos.services.ui.EditorProvider;
import org.openeos.services.ui.model.IFieldDefinition;
import com.googlecode.lanterna.gui.component.Label;

public class LanternaEditorProvider implements EditorProvider {

	private NameValueFinder nameValueFinder;

	public void setNameValueFinder(NameValueFinder nameValueFinder) {
		this.nameValueFinder = nameValueFinder;
	}

	private class GenericLanternaEditor implements LanternaEditor {

		private Label label;
		private Field field;

		@Override
		public Label getLabel() {
			return label;
		}

		@Override
		public Field getField() {
			return field;
		}

	}

	@Override
	public <T extends Editor> boolean canBuildEditor(Class<T> requiredType) {
		return requiredType.isAssignableFrom(LanternaEditor.class);
	}

	@Override
	public <T extends Editor> T buildEditor(IFieldDefinition fieldDefinition, Class<T> requiredType) {
		if (!canBuildEditor(requiredType)) {
			throw new IllegalArgumentException("The required type must by compatible to LanternaEditor.");
		}

		Label label = new Label(fieldDefinition.getName());
		Field field;
		// TODO
		/*
		Class<?> clazz = fieldDefinition.getPropertyDefinition().getPropertyClass();
		if (clazz.isAssignableFrom(String.class)) {
			field = new TextBoxField(10); //TODO With must be defined in the field definition
		} else if (INameValue.class.isAssignableFrom(clazz)) {
			// Make standard search value
			field = new NameValueField(nameValueFinder, (Class<INameValue>) clazz, 10);
		} else {
			field = new LabelField();
		}
		*/
		field = new LabelField();
		GenericLanternaEditor editor = new GenericLanternaEditor();
		editor.label = label;
		editor.field = field;
		return (T) editor;
	}

}
