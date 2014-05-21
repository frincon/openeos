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
import org.openeos.services.ui.action.CustomAction;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class CustomActionCommand implements Command {

	// TODO Check for memory allocation, window component is not destroy when tab is closed

	private static final long serialVersionUID = 1L;
	private CustomAction customAction;
	private UIApplication<IUnoVaadinApplication> application;

	public CustomActionCommand(CustomAction customAction, UIApplication<IUnoVaadinApplication> application) {
		this.customAction = customAction;
		this.application = application;
	}

	@Override
	public void menuSelected(MenuItem selectedItem) {
		customAction.run(application);
	}

}
