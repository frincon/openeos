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

import org.abstractform.binding.BBindingToolkit;
import org.abstractform.binding.vaadin.VaadinBindingFormToolkit;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.action.UIEntityActionManager;
import org.openeos.services.ui.form.FormRegistryService;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.services.ui.vaadin.VaadinWindow;
import org.openeos.services.ui.vaadin.internal.DefaultVaadinERPWindowFactory;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormFieldFactory;

public class AbstractFormVaadinERPWindowFactory extends DefaultVaadinERPWindowFactory {

	private VaadinBindingFormToolkit vaadinToolkit;
	private BBindingToolkit bindingToolkit;
	private FormRegistryService formRegistryService;
	private UIEntityActionManager uiEntityActionManager;

	public AbstractFormVaadinERPWindowFactory(IDictionaryService dictionaryService, IVaadinContainerFactory containerFactory,
			FormFieldFactory formFieldFactory, VaadinBindingFormToolkit vaadinToolkit, BBindingToolkit bindingToolkit,
			FormRegistryService formRegistryService, UIEntityActionManager uiEntityActionManager) {
		super(dictionaryService, containerFactory, formFieldFactory);
		this.vaadinToolkit = vaadinToolkit;
		this.bindingToolkit = bindingToolkit;
		this.formRegistryService = formRegistryService;
		this.uiEntityActionManager = uiEntityActionManager;
	}

	@Override
	protected VaadinWindow createWindowImpl(IWindowDefinition windowDefinition, UIApplication<IUnoVaadinApplication> application) {
		return new AbstractFormVaadinWindow(windowDefinition, getContainerFactory(), getDictionaryService(), application,
				vaadinToolkit, bindingToolkit, formRegistryService);
	}

	@Override
	protected Component createEntityActionPanel(VaadinWindow vaadinWindow, UIApplication<IUnoVaadinApplication> application) {
		Class<?> entityClass = getDictionaryService().getClassDefinition(
				vaadinWindow.getWindowDefinition().getRootTab().getClassName()).getClassDefined();
		return new EntityActionPanelController(uiEntityActionManager, application, vaadinWindow, entityClass).getComponent();
	}
}
