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

import org.openeos.services.ui.form.abstractform.UIButtonController;
import org.openeos.services.ui.vaadin.internal.UIApplicationImpl;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class UIVaadinButtonField extends CustomComponent implements Property {

	private Object value;

	private TextField textField;
	private Button button;
	private UIButtonController buttonController;
	private UIApplicationImpl application;

	public UIVaadinButtonField(String caption, UIButtonController buttonController, UIApplicationImpl application) {

		this.buttonController = buttonController;
		this.application = application;
		HorizontalLayout mainLayout = new HorizontalLayout();
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		mainLayout.setSizeFull();

		textField = new TextField(caption);
		mainLayout.addComponent(textField);

		button = new Button("...", new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				UIVaadinButtonField.this.buttonController.onClick(UIVaadinButtonField.this.application, value);
			}
		});
		;
		mainLayout.addComponent(button);

		setCompositionRoot(mainLayout);
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
		this.value = newValue;
	}

	@Override
	public Class<?> getType() {
		return Object.class;
	}

}
