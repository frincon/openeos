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

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.model.RefreshType;
import org.openeos.services.ui.model.SelectionChangeEvent;
import org.openeos.services.ui.model.SelectionChangeListener;
import org.openeos.services.ui.model.TabChangeEvent;
import org.openeos.services.ui.model.TabChangeListener;
import org.openeos.services.ui.model.ViewChangeEvent;
import org.openeos.services.ui.model.ViewChangeListener;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.services.ui.vaadin.VaadinWindow;
import org.openeos.services.ui.window.SelectionChangeSupport;
import org.openeos.services.ui.window.TabChangeSupport;
import org.openeos.services.ui.window.ViewChangeSupport;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

public abstract class AbstractVaadinWindowImpl implements VaadinWindow {

	private IWindowDefinition windowDefinition;
	private IDictionaryService dictionaryService;
	private IVaadinContainerFactory containerFactory;
	private UIApplication<IUnoVaadinApplication> application;

	private ComponentContainer mainContainer;

	private SelectionChangeSupport selectionChangeSupport = new SelectionChangeSupport();
	private TabChangeSupport tabChangeSupport = new TabChangeSupport();
	private ViewChangeSupport viewChangeSupport = new ViewChangeSupport();

	public AbstractVaadinWindowImpl(IWindowDefinition windowDefinition, IVaadinContainerFactory containerFactory,
			IDictionaryService dictionaryService, UIApplication<IUnoVaadinApplication> application) {
		this.windowDefinition = windowDefinition;
		this.containerFactory = containerFactory;
		this.dictionaryService = dictionaryService;
		this.application = application;
		this.mainContainer = createMainContainer();
	}

	public IDictionaryService getDictionaryService() {
		return dictionaryService;
	}

	public IVaadinContainerFactory getContainerFactory() {
		return containerFactory;
	}

	protected abstract ComponentContainer createMainContainer();

	@Override
	public IWindowDefinition getWindowDefinition() {
		return windowDefinition;
	}

	@Override
	public void refresh(RefreshType refreshType) {
		getActiveTab().refresh(refreshType);
	}

	@Override
	public void showMessage(MessageType type, String caption) {
		application.showMessage(type, caption);
	}

	@Override
	public void showMessage(MessageType type, String caption, String description) {
		application.showMessage(type, caption, description);
	}

	@Override
	public void addSelectionChangeListener(SelectionChangeListener listener) {
		selectionChangeSupport.addListener(listener);
	}

	@Override
	public void removeSelectionChangeListener(SelectionChangeListener listener) {
		selectionChangeSupport.removeListener(listener);
	}

	protected void fireSelectionChange(SelectionChangeEvent event) {
		selectionChangeSupport.fireEvent(event);
	}

	@Override
	public void addTabChangeListener(TabChangeListener listener) {
		tabChangeSupport.addListener(listener);
	}

	@Override
	public void removeTabChangeListener(TabChangeListener listener) {
		tabChangeSupport.removeListener(listener);
	}

	protected void fireTabChange(TabChangeEvent event) {
		tabChangeSupport.fireEvent(event);
	}

	@Override
	public void addViewChangeListener(ViewChangeListener listener) {
		viewChangeSupport.addListener(listener);
	}

	@Override
	public void removeViewChangeListener(ViewChangeListener listener) {
		viewChangeSupport.removeListener(listener);
	}

	protected void fireViewChange(ViewChangeEvent event) {
		viewChangeSupport.fireEvent(event);
	}

	@Override
	public Component getComponent() {
		return mainContainer;
	}

	public ComponentContainer getMainContainer() {
		return this.mainContainer;
	}

	public UIApplication<IUnoVaadinApplication> getApplication() {
		return application;
	}

}
