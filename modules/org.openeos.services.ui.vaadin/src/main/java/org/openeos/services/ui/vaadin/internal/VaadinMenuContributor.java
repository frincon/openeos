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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.ui.CustomActionManagerService;
import org.openeos.services.ui.WindowManagerService;
import org.openeos.services.ui.action.CustomAction;
import org.openeos.services.ui.menu.MenuContributor;
import org.openeos.services.ui.model.IMenuDefinition;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.vaadin.IVaadinERPWindowFactory;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import org.openeos.vaadin.main.IVaadinMenuContributor;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class VaadinMenuContributor implements IVaadinMenuContributor {

	private static final Logger LOG = LoggerFactory.getLogger(VaadinMenuContributor.class);

	//TODO Need to unbind

	private WindowManagerService windowManagerService;
	private CustomActionManagerService customActionManagerService;
	private IVaadinERPWindowFactory windowFactory;
	private UIVaadinApplicationFactory applicationFactory;

	private List<MenuContributor> menuList = new ArrayList<MenuContributor>();

	public VaadinMenuContributor(WindowManagerService windowManagerService, CustomActionManagerService customActionManagerService,
			IVaadinERPWindowFactory windowFactory, UIVaadinApplicationFactory applicationFactory) {
		this.windowManagerService = windowManagerService;
		this.customActionManagerService = customActionManagerService;
		this.windowFactory = windowFactory;
		this.applicationFactory = applicationFactory;
	}

	@Override
	public void contribute(MenuBar menuBar, IUnoVaadinApplication application) {
		Collections.sort(menuList, new Comparator<MenuContributor>() {
			@Override
			public int compare(MenuContributor o1, MenuContributor o2) {
				return new Integer(o1.getOrder()).compareTo(o2.getOrder());
			}
		});

		for (MenuContributor contributor : menuList) {
			for (IMenuDefinition menu : contributor.getRootMenuDefinitionList()) {
				MenuItem menuItem = menuBar.addItem(menu.getName(), createAction(menu, application));
				addRecursiveMenus(contributor, contributor.getChildMenuDefinitionList(menu), menuItem, application);
			}
		}
	}

	private Command createAction(IMenuDefinition menu, IUnoVaadinApplication application) {
		switch (menu.getType()) {
		case MENU:
		case SEPARATOR:
			return null;
		case WINDOW:
			return createWindowAction(menu, application);
		case CUSTOM:
			return createCustomAction(menu, application);
		}
		return null;
	}

	private Command createWindowAction(IMenuDefinition menu, IUnoVaadinApplication application) {
		IWindowDefinition window = windowManagerService.getWindowDefinition(menu.getWindowId());
		if (window == null) {
			LOG.warn("Window not found: '{}'", menu.getWindowId());
			return null;
		} else {
			// TODO Make with service factory or prototype bean blueprint
			return new OpenWindowCommand(window, applicationFactory.createApplication(application), windowFactory);
		}
	}

	private Command createCustomAction(IMenuDefinition menu, IUnoVaadinApplication application) {
		CustomAction customAction = customActionManagerService.getCustomAction(menu.getCustomActionId());
		if (customAction == null) {
			LOG.warn("Custom action not found: '{}'", menu.getCustomActionId());
			return null;
		} else {
			return new CustomActionCommand(customAction, applicationFactory.createApplication(application));
		}
	}

	private void addRecursiveMenus(MenuContributor contributor, List<? extends IMenuDefinition> subMenuList, MenuItem parent,
			IUnoVaadinApplication application) {
		if (subMenuList == null)
			return;
		for (IMenuDefinition subMenu : subMenuList) {
			MenuItem child = parent.addItem(subMenu.getName(), createAction(subMenu, application));
			addRecursiveMenus(contributor, contributor.getChildMenuDefinitionList(subMenu), child, application);
		}
	}

	public void bindMenuContributor(MenuContributor menuContributor) {
		menuList.add(menuContributor);
	}

	public void unbindMenuContributor(MenuContributor menuContributor) {
		if (menuContributor != null) {
			menuList.remove(menuContributor);
		}
	}
}
