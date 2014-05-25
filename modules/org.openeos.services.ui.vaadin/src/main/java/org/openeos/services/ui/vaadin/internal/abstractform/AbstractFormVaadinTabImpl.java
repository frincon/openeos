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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.abstractform.binding.BBindingToolkit;
import org.abstractform.binding.vaadin.VaadinBindingFormInstance;
import org.abstractform.binding.vaadin.VaadinBindingFormToolkit;
import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.FormRegistryService;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;
import org.openeos.services.ui.model.ITabDefinition;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.services.ui.vaadin.internal.AbstractVaadinTabImpl;
import org.openeos.vaadin.main.IUnoVaadinApplication;

import com.vaadin.ui.Panel;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AbstractFormVaadinTabImpl extends AbstractVaadinTabImpl {

	private VaadinBindingFormToolkit vaadinToolKit;
	private BBindingToolkit bindingToolkit;
	private FormRegistryService formRegistryService;
	private IDictionaryService dictionaryService;

	private VaadinBindingFormInstance editForm;
	private VaadinBindingFormInstance newForm;

	private boolean modified = false;

	public AbstractFormVaadinTabImpl(ITabDefinition tabDefinition, IVaadinContainerFactory containerFactory,
			UIApplication<IUnoVaadinApplication> application, IDictionaryService dictionaryService,
			VaadinBindingFormToolkit vaadinToolKit, BBindingToolkit bindingToolkit, FormRegistryService formRegistryService) {
		super(tabDefinition, containerFactory, application);
		this.vaadinToolKit = vaadinToolKit;
		this.bindingToolkit = bindingToolkit;
		this.dictionaryService = dictionaryService;
		this.formRegistryService = formRegistryService;

		initDefaultForms();
	}

	private void initDefaultForms() {
		editForm = getDefaultForm(BindingFormCapability.EDIT);
		newForm = getDefaultForm(BindingFormCapability.NEW);
		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				modified = true;
			}
		};
		editForm.addFieldChangeListener(listener);
		newForm.addFieldChangeListener(listener);
	}

	private VaadinBindingFormInstance getDefaultForm(BindingFormCapability capability) {
		AbstractFormBindingForm bindingEditForm = formRegistryService.getDefaultForm(
				dictionaryService.getClassDefinition(getTabDefinition().getClassName()).getClassDefined(),
				AbstractFormBindingForm.class, capability);
		Map<String, Object> extraObjects = new HashMap<String, Object>();
		extraObjects.put(UIVaadinFormToolkit.EXTRA_OBJECT_APPLICATION, getApplication());
		return vaadinToolKit.buildForm(bindingEditForm.getAbstractBForm(), bindingToolkit, extraObjects);
	}

	@Override
	protected void showEdit() {
		if (getActiveObject() == null) {
			List<UIBean> selectedObjects = getSelectedObjectsInList();
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
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setStyleName("background-default");
		getMainContainer().addComponent(panel);
		if (getActiveObject().isNew()) {
			newForm.setValue(getActiveObject());
			panel.addComponent(newForm.getImplementation());
		} else {
			editForm.setValue(getActiveObject());
			editForm.getBindingContext().updateFields();
			panel.addComponent(editForm.getImplementation());
		}
		modified = false;
	}

	@Override
	public boolean isModified() {
		return modified;
	}

	@Override
	public boolean validate() {
		if (getActualView() == View.EDIT) {
			VaadinBindingFormInstance instance;
			if (getActiveObject().isNew()) {
				instance = newForm;
			} else {
				instance = editForm;
			}
			instance.updateModel();
			instance.refreshValidationSummary();
			return instance.getErrors().size() == 0;
		}
		return false;
	}

}
