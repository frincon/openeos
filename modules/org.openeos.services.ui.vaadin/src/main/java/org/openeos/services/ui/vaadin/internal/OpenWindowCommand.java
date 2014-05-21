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

import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.vaadin.IVaadinERPWindowFactory;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.ComponentContainer.ComponentDetachEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class OpenWindowCommand implements Command {

	// TODO Check for memory allocation, window component is not destroy when tab is closed

	private static final long serialVersionUID = 1L;
	private IWindowDefinition window;
	private UIApplication<IUnoVaadinApplication> application;
	private Component windowComponent = null;

	private IVaadinERPWindowFactory windowFactory;

	public OpenWindowCommand(IWindowDefinition window, UIApplication<IUnoVaadinApplication> application,
			IVaadinERPWindowFactory windowFactory) {
		this.window = window;
		this.application = application;
		this.windowFactory = windowFactory;
	}

	@Override
	public void menuSelected(MenuItem selectedItem) {
		if (windowComponent == null)
			windowComponent = windowFactory.createWindowComponent(window, application);
		final TabSheet tabSheet = application.getConcreteApplication().getMainTabSheet();
		tabSheet.addListener(new ComponentContainer.ComponentDetachListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void componentDetachedFromContainer(ComponentDetachEvent event) {
				if (event.getDetachedComponent() == windowComponent) {
					windowComponent = null;
					tabSheet.removeListener(this);
				}
			}
		});
		Tab tab = tabSheet.addTab(windowComponent, window.getName());
		tab.setClosable(true);
		tab.setDescription(window.getDescription());
		tabSheet.setSelectedTab(tab);
	}

}
