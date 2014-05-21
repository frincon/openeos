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
import org.openeos.services.ui.form.FormRegistryService;
import org.openeos.services.ui.model.ITab;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.model.SelectionChangeEvent;
import org.openeos.services.ui.model.SelectionChangeListener;
import org.openeos.services.ui.model.ViewChangeEvent;
import org.openeos.services.ui.model.ViewChangeListener;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.services.ui.vaadin.internal.AbstractVaadinWindowImpl;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

public class AbstractFormVaadinWindow extends AbstractVaadinWindowImpl implements SelectionChangeListener, ViewChangeListener {

	private AbstractFormVaadinTabImpl tab;
	private VaadinBindingFormToolkit vaadinToolkit;
	private BBindingToolkit bindingToolkit;
	private FormRegistryService formRegistryService;

	public AbstractFormVaadinWindow(IWindowDefinition windowDefinition, IVaadinContainerFactory containerFactory,
			IDictionaryService dictionaryService, UIApplication<IUnoVaadinApplication> application,
			VaadinBindingFormToolkit vaadinToolkit, BBindingToolkit bindingToolkit, FormRegistryService formRegistryService) {
		super(windowDefinition, containerFactory, dictionaryService, application);
		this.vaadinToolkit = vaadinToolkit;
		this.bindingToolkit = bindingToolkit;
		this.formRegistryService = formRegistryService;

		createTabs();
		getMainContainer().addComponent(tab.getMainContainer());
	}

	@Override
	public ITab getActiveTab() {
		return tab;
	}

	private void createTabs() {
		tab = new AbstractFormVaadinTabImpl(getWindowDefinition().getRootTab(), getContainerFactory(), getApplication(),
				getDictionaryService(), vaadinToolkit, bindingToolkit, formRegistryService);
		tab.addSelectionChangeListener(this);
		tab.addViewChangeListener(this);
	}

	@Override
	protected ComponentContainer createMainContainer() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(false);
		return layout;
	}

	@Override
	public void selectionChange(SelectionChangeEvent selectionChangeEvent) {
		fireSelectionChange(new SelectionChangeEvent(this, selectionChangeEvent.getSelectedObjectList()));
	}

	@Override
	public void viewChange(ViewChangeEvent viewChangeEvent) {
		fireViewChange(new ViewChangeEvent(this));
	}

}
