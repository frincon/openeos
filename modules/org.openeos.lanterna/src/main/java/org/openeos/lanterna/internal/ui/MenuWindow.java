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
package org.openeos.lanterna.internal.ui;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.IWindowManagerService;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.WindowActionService;
import org.openeos.services.ui.model.IMenuDefinition;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.GUIScreen.Position;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.ActionListBox;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;

public class MenuWindow extends Window {

	private class MenuAction implements Action {

		private IMenuDefinition actionMenu;

		private MenuAction(IMenuDefinition actionMenu) {
			this.actionMenu = actionMenu;
		}

		@Override
		public void doAction() {
			menuClick(actionMenu);
		}

	}

	private IMenuDefinition menu;
	private IWindowManagerService windowManagerService;
	private IDictionaryService dictionaryService;
	private UIDAOService uidaoService;
	private WindowActionService windowActionService;

	public MenuWindow(IMenuDefinition menu, IWindowManagerService windowManagerService, IDictionaryService dictionaryService,
			UIDAOService uidaoService, WindowActionService windowActionService) {
		super(menu.getName());
		this.menu = menu;
		this.windowManagerService = windowManagerService;
		this.dictionaryService = dictionaryService;
		this.uidaoService = uidaoService;
		this.windowActionService = windowActionService;
		createComponents();
		addGeneralShortcuts();
	}

	private void addGeneralShortcuts() {
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void onUnhandledKeyboardInteraction(Window window, Key key) {
				if (key.getKind() == Kind.Escape)
					close();
			}

		});
	}

	private void createComponents() {
		addComponent(createMainTable());
	}

	private Component createMainTable() {
		ActionListBox listBox = new ActionListBox();
		for (int i = 0; i < menu.getSubMenuList().size(); i++) {
			final IMenuDefinition subMenu = menu.getSubMenuList().get(i);
			listBox.addAction(subMenu.getName(), new MenuAction(subMenu));
		}
		return listBox;
	}

	protected void menuClick(IMenuDefinition subMenu) {
		switch (subMenu.getType()) {
		case MENU:
			MenuWindow window = new MenuWindow(subMenu, windowManagerService, dictionaryService, uidaoService, windowActionService);
			getOwner().showWindow(window);
			break;
		case WINDOW:
			UnoWindow unoWindow = new UnoWindow(windowManagerService.getWindowDefinition(subMenu.getWindowId()), dictionaryService,
					uidaoService, windowActionService, windowManagerService);
			getOwner().showWindow(unoWindow, Position.FULL_SCREEN);
			break;
		}
	}

}
