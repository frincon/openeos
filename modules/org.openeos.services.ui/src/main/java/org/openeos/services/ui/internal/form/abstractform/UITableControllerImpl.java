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
package org.openeos.services.ui.internal.form.abstractform;

import java.util.Collection;
import java.util.Set;

import org.abstractform.binding.BFormInstance;
import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.UIDialog;
import org.openeos.services.ui.UIDialogCloseEvent;
import org.openeos.services.ui.UIDialogListener;
import org.openeos.services.ui.form.abstractform.UITableController;
import org.openeos.services.ui.internal.UIBeanImpl;

public class UITableControllerImpl<T> implements UITableController<T> {

	private UIDAOService uidaoService;

	private Class<T> elementsClass;
	private Class<?> parentClass;
	private String childPropertyName;

	public UITableControllerImpl(UIDAOService uidaoService, Class<?> parentClass, String childPropertyName) {
		this.uidaoService = uidaoService;
		this.parentClass = parentClass;
		this.childPropertyName = childPropertyName;
	}

	public Class<T> getElementsClass() {
		return elementsClass;
	}

	public void setElementsClass(Class<T> elementsClass) {
		this.elementsClass = elementsClass;
	}

	@Override
	public void onEdit(UIApplication<?> application, BFormInstance<?, ?> formInstance, Collection<T> collection, T model) {
		if (model instanceof Set<?>) {
			model = (T) ((Set<T>) model).iterator().next();
		}
		if (model instanceof UIBean) {
			UIBean uibean = (UIBean) model;
			application.showEditFormDialog(uibean);
		} else if (model instanceof Set<?>) {

		} else {
			application.showEditFormDialog(uidaoService.wrap(elementsClass, model));
		}
	}

	@Override
	public void onNew(UIApplication<?> application, BFormInstance<?, ?> formInstance, final Collection<T> collection) {
		final UIBean bean = uidaoService.create(elementsClass, new FormInstanceContext(application, formInstance));
		//bean.set(childPropertyName, parent);
		UIDialog dialog = application.showNewFormDialog(bean);
		dialog.setUIDialogListener(new UIDialogListener() {

			@Override
			public void onClose(UIDialogCloseEvent event) {
				if (event.getButtonId().equals(UIDialog.BUTTON_OK)) {
					collection.add((T) ((UIBeanImpl) bean).getBeanWrapped());
				}
			}
		});
	}

	@Override
	public void onDelete(UIApplication<?> application, BFormInstance<?, ?> formInstance, Collection<T> collection, T model) {
		application.showMessage(MessageType.INFO, "On Delete!!");
	}

}
