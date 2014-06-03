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
package org.openeos.services.ui.form.abstractform;

import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.validation.Validator;

public class BFUIButton extends BFField {

	public static final String TYPE_UI_BUTTON = BFUIButton.class.getPackage().getName() + ".TYPE_UI_BUTTON";
	public static final String EXTRA_UI_BUTTON_CONTROLLER = BFUIButton.class.getPackage().getName() + ".EXTRA_UI_BUTTON_CONTROLLER";

	public BFUIButton(String id, String name, String propertyName) {
		super(id, name, propertyName);
		setType(TYPE_UI_BUTTON);
	}

	@Override
	public BFUIButton readOnly(boolean readOnly) {
		return (BFUIButton) super.readOnly(readOnly);
	}

	@Override
	public BFUIButton required(boolean required) {
		return (BFUIButton) super.required(required);
	}

	@Override
	public BFUIButton description(String description) {
		return (BFUIButton) super.description(description);
	}

	@Override
	public BFUIButton displayWidth(int displayWidth) {
		return (BFUIButton) super.displayWidth(displayWidth);
	}

	@Override
	public BFUIButton maxLength(int maxLength) {
		return (BFUIButton) super.maxLength(maxLength);
	}

	@Override
	public BFUIButton type(String type) {
		return (BFUIButton) super.type(type);
	}

	@Override
	public BFUIButton readOnlyPropertyName(String readOnlyPropertyName) {
		return (BFUIButton) super.readOnlyPropertyName(readOnlyPropertyName);
	}

	@Override
	public BFUIButton validator(Validator<?> validator) {
		return (BFUIButton) super.validator(validator);
	}

	@Override
	public BFUIButton extra(String key, Object extraObject) {
		return (BFUIButton) super.extra(key, extraObject);
	}

	public void setButtonController(UIButtonController buttonController) {
		setExtra(EXTRA_UI_BUTTON_CONTROLLER, buttonController);
	}

	public UIButtonController getUIButtonController() {
		return (UIButtonController) getExtra(EXTRA_UI_BUTTON_CONTROLLER);
	}

	public BFUIButton buttonController(UIButtonController buttonController) {
		setButtonController(buttonController);
		return this;
	}

}
