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
package org.openeos.services.ui.vaadin.internal;

import java.util.List;

import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.model.ITabDefinition;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Window.Notification;

public class VaadinTabImpl extends AbstractVaadinTabImpl {

	private FormFieldFactory formFieldFactory;
	private Form form;

	public VaadinTabImpl(ITabDefinition tabDefinition, IVaadinContainerFactory containerFactory,
			UIApplication<IUnoVaadinApplication> application, FormFieldFactory formFieldFactory) {
		super(tabDefinition, containerFactory, application);
		this.formFieldFactory = formFieldFactory;
	}

	@Override
	protected AbstractOrderedLayout createMainContainer() {
		AbstractOrderedLayout main = super.createMainContainer();
		form = createForm();
		return main;
	}

	private Form createForm() {
		Form form = new Form();
		form.setFormFieldFactory(this.formFieldFactory);
		return form;
	}

	@Override
	protected void showEdit() {
		if (getActiveObject() == null) {
			List<UIBean> selectedObjects = getSelectedObjects();
			if (selectedObjects.size() > 0) {
				this.setActiveObject(selectedObjects.get(0));
			} else {
				//TODO i18n
				getMainContainer().getWindow().showNotification("Select an intem", "Please select an item to edit",
						Notification.TYPE_HUMANIZED_MESSAGE);
				showView(View.LIST);
				return;
			}
		}
		getMainContainer().addComponent(form);

	}

	@Override
	public void setActiveObject(UIBean obj) {
		super.setActiveObject(obj);
		Item activeItem = null;
		if (obj != null) {
			if (searchAndSelect(obj)) {
				activeItem = (Item) getContainer().getItem(getTable().getValue());
			} else {
				activeItem = getContainerFactory().createItem(obj);
			}
			this.form.setItemDataSource(activeItem, getFormPropertyList());
			this.form.setImmediate(true);
		}
	}

	@Override
	public boolean isModified() {
		return true;
	}

	@Override
	public boolean validate() {
		return true;
	}

}