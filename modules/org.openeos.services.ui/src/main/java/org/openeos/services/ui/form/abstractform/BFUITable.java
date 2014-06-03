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

import org.abstractform.binding.fluent.table.BFTable;
import org.abstractform.binding.validation.Validator;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.internal.form.abstractform.UITableControllerImpl;

public class BFUITable extends BFTable {

	public static final String TYPE_UITABLE = BFUITable.class.getPackage().getName() + ".BFUITable";
	public static final String EXTRA_TABLE_CONTROLLER = BFUITable.class.getPackage().getName() + ".EXTRA_TABLE_CONTROLLER";

	public BFUITable(String id, String name, Class<?> beanClass, String propertyName, UIDAOService uidaoService) {
		super(id, name, propertyName);
		setType(TYPE_UITABLE);
		setTableController(new UITableControllerImpl(uidaoService, beanClass, null));
	}

	@Override
	public void setElementsClass(Class<?> elementsClass) {
		super.setElementsClass(elementsClass);
		if (getTableController() instanceof UITableControllerImpl) {
			((UITableControllerImpl) getTableController()).setElementsClass(elementsClass);
		}
	}

	@Override
	public BFUITable elementsClass(Class<?> elementClass) {
		return (BFUITable) super.elementsClass(elementClass);
	}

	@Override
	public BFUITable pageLenght(int pageLenght) {
		return (BFUITable) super.pageLenght(pageLenght);
	}

	@Override
	public BFUITable readOnly(boolean readOnly) {
		return (BFUITable) super.readOnly(readOnly);
	}

	@Override
	public BFUITable required(boolean required) {
		return (BFUITable) super.required(required);
	}

	@Override
	public BFUITable description(String description) {
		return (BFUITable) super.description(description);
	}

	@Override
	public BFUITable displayWidth(int displayWidth) {
		return (BFUITable) super.displayWidth(displayWidth);
	}

	@Override
	public BFUITable maxLength(int maxLength) {
		return (BFUITable) super.maxLength(maxLength);
	}

	@Override
	public BFUITable type(String type) {
		return (BFUITable) super.type(type);
	}

	@Override
	public BFUITable readOnlyPropertyName(String readOnlyPropertyName) {
		return (BFUITable) super.readOnlyPropertyName(readOnlyPropertyName);
	}

	@Override
	public BFUITable validator(Validator<?> validator) {
		return (BFUITable) super.validator(validator);
	}

	public void setTableController(UITableController tableController) {
		setExtra(EXTRA_TABLE_CONTROLLER, tableController);
	}

	public UITableController getTableController() {
		return (UITableController) getExtra(EXTRA_TABLE_CONTROLLER);
	}

	public BFUITable tableController(UITableController tableController) {
		setTableController(tableController);
		return this;
	}
}
