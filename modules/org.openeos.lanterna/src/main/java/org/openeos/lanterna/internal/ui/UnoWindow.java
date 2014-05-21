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
import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.WindowActionService;
import org.openeos.services.ui.model.ITab;
import org.openeos.services.ui.model.ITabDefinition;
import org.openeos.services.ui.model.IWindow;
import org.openeos.services.ui.model.IWindowAction;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.model.SelectionChangeListener;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.layout.VerticalLayout;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;

public class UnoWindow extends Window implements IWindow {

	private IWindowDefinition windowDefinition;
	private Panel mainPanel;
	private Panel actionPanel;
	private Panel subTabPanel;
	private UnoTab activeTab;
	private IDictionaryService dictionaryService;
	private UIDAOService uidaoService;
	private WindowActionService windowActionService;
	private IWindowManagerService windowManagerService;

	public UnoWindow(IWindowDefinition windowDefinition, IDictionaryService dictionaryService, UIDAOService uidaoService,
			WindowActionService windowActionService, IWindowManagerService windowManagerService) {
		super(windowDefinition.getName());
		this.setBorder(new Border.Invisible());
		this.windowDefinition = windowDefinition;
		this.dictionaryService = dictionaryService;
		this.uidaoService = uidaoService;
		this.windowActionService = windowActionService;
		this.windowManagerService = windowManagerService;
		actionPanel = createActionPanel();
		mainPanel = createMainPanel();
		subTabPanel = createSubTabPanel();
		addComponent(actionPanel);
		addComponent(mainPanel, VerticalLayout.MAXIMIZES_HORIZONTALLY, VerticalLayout.MAXIMIZES_VERTICALLY);
		addComponent(subTabPanel);
		addGeneralShortcuts();

		UnoTab mainTab = new UnoTab(windowDefinition.getRootTab(), dictionaryService, uidaoService, windowManagerService);
		showTab(mainTab);
		setFocus(mainPanel.nextFocus(null));
	}

	private Panel createActionPanel() {
		Panel panel = new Panel(new Border.Invisible(), Orientation.HORISONTAL);
		for (final IWindowAction windowAction : windowActionService.getWindowActionList(this)) {
			Button button = new Button(windowAction.getCaption(), new Action() {

				@Override
				public void doAction() {
					windowAction.run(UnoWindow.this);
				}
			});
			panel.addComponent(button);
		}
		return panel;
	}

	private Panel createSubTabPanel() {
		Panel panel = new Panel(new Border.Invisible(), Orientation.HORISONTAL);
		for (final ITabDefinition tabDefinition : windowDefinition.getRootTab().getChildren()) {
			Label label = new Label(tabDefinition.getName());
			panel.addComponent(label);
		}
		return panel;
	}

	private void showTab(UnoTab unoTab) {
		mainPanel.removeAllComponents();
		mainPanel.addComponent(unoTab, VerticalLayout.MAXIMIZES_HORIZONTALLY, VerticalLayout.MAXIMIZES_VERTICALLY);
		this.activeTab = unoTab;
	}

	private Panel createMainPanel() {
		Panel panel = new Panel(new Border.Invisible(), Orientation.VERTICAL);
		return panel;
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

	@Override
	public void addSelectionChangeListener(SelectionChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSelectionChangeListener(SelectionChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public IWindowDefinition getWindowDefinition() {
		return windowDefinition;
	}

	@Override
	public ITab getActiveTab() {
		return activeTab;
	}

	@Override
	public void refresh() {
		activeTab.refresh();
	}

	@Override
	public void showMessage(MessageType type, String caption) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showMessage(MessageType type, String caption, String description) {
		// TODO Auto-generated method stub

	}

}
