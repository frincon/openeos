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
package org.openeos.lanterna.internal;

import java.util.List;

import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.lanterna.internal.ui.MenuWindow;
import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.IWindowManagerService;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.WindowActionService;
import org.openeos.services.ui.model.IMenuDefinition;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.terminal.Terminal;

public class LanternaRunner implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(LanternaRunner.class);

	private Terminal terminal;
	private ServerSession session;
	private GUIScreen guiScreen;
	private IWindowManagerService windowManagerService;
	private IDictionaryService dictionaryService;
	private UIDAOService uidaoService;
	private WindowActionService windowActionService;

	private List<IMenuDefinition> rootMenuList;

	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public void setSession(ServerSession session) {
		this.session = session;
	}

	public void setRootMenuList(List<IMenuDefinition> rootMenuList) {
		this.rootMenuList = rootMenuList;
	}

	public void setWindowManagerService(IWindowManagerService windowManagerService) {
		this.windowManagerService = windowManagerService;
	}

	public void setDictionaryService(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setUidaoService(UIDAOService uidaoService) {
		this.uidaoService = uidaoService;
	}

	public void setWindowActionService(WindowActionService windowActionService) {
		this.windowActionService = windowActionService;
	}

	//TODO: This method is transactional because the problem of OpenSessionInView 
	@Override
	public void run() {
		LOG.debug("Running LanternaRunner");
		guiScreen = TerminalFacade.createGUIScreen(terminal);
		guiScreen.getScreen().startScreen();

		Window mainWindow = createMainWindow();
		guiScreen.showWindow(mainWindow);
		guiScreen.getScreen().stopScreen();
		terminal.clearScreen();
		terminal.flush();
		session.close(false);
		LOG.debug("Finished LanternaRunner");
	}

	private Window createMainWindow() {
		MenuWindow window = new MenuWindow(new IMenuDefinition() {

			@Override
			public String getName() {
				return "Main Menu"; //TODO i18n
			}

			@Override
			public String getDescription() {
				return "Main Manu"; //TODO i18n
			}

			@Override
			public MenuType getType() {
				return MenuType.MENU;
			}

			@Override
			public String getWindowId() {
				return null;
			}

			@Override
			public List<? extends IMenuDefinition> getSubMenuList() {
				return rootMenuList;
			}

			@Override
			public String getId() {
				return this.getClass().getPackage().getName() + "." + getId();
			}

		}, windowManagerService, dictionaryService, uidaoService, windowActionService);
		return window;
	}

}
