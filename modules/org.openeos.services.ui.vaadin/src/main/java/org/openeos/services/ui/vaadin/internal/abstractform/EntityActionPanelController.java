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

import java.util.List;

import org.openeos.services.ui.ConfirmationCallback;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.action.EntityAction;
import org.openeos.services.ui.action.UIEntityActionManager;
import org.openeos.services.ui.model.ITab.View;
import org.openeos.services.ui.model.IWindow;
import org.openeos.services.ui.model.RefreshType;
import org.openeos.services.ui.model.SelectionChangeEvent;
import org.openeos.services.ui.model.SelectionChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Reindeer;

public class EntityActionPanelController implements SelectionChangeListener {

	private UIEntityActionManager uiEntityActionManager;
	private IWindow window;
	private Class<?> entityClass;
	private UIApplication uiApplication;

	private Panel mainPanel;

	public EntityActionPanelController(UIEntityActionManager uiEntityActionManager, UIApplication uiApplication, IWindow window,
			Class<?> entityClass) {
		this.uiEntityActionManager = uiEntityActionManager;
		this.uiApplication = uiApplication;
		this.window = window;
		this.entityClass = entityClass;
		createComponent();
		window.addSelectionChangeListener(this);
		updateActions(window.getActiveTab().getSelectedObjects());
	}

	private void updateActions(List<UIBean> selectedObjects) {
		List<EntityAction<UIBean>> entityActionList = uiEntityActionManager.getEnabledActionsOrdered(entityClass, selectedObjects);
		mainPanel.removeAllComponents();
		String actualGroup = null;
		for (EntityAction<UIBean> action : entityActionList) {
			String group = action.getGroup();
			if (group != null && !group.equals(actualGroup)) {
				createHeader(group);
			}
			createAction(action);
		}
	}

	private void createAction(final EntityAction<UIBean> action) {
		ClickListener listener = new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (window.getActiveTab().getActualView() == View.EDIT && window.getActiveTab().isModified()) {
					window.getApplication().showConfirmation("The changes may be lost, are you sure?", new ConfirmationCallback() {
						@Override
						public void onCloseConfirmation(boolean accepted) {
							if (accepted) {
								action.execute(window.getActiveTab().getSelectedObjects(), uiApplication);
								window.refresh(RefreshType.SELECTED);
							}
						}
					});
				} else {
					action.execute(window.getActiveTab().getSelectedObjects(), uiApplication);
					window.refresh(RefreshType.SELECTED);
				}

			}
		};
		HorizontalLayout horLayout = new HorizontalLayout();
		horLayout.setMargin(false);
		horLayout.setSpacing(false);
		horLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
		Button button = new Button(action.getName(), listener);
		button.addStyleName(Reindeer.BUTTON_LINK);
		horLayout.addComponent(button);
		mainPanel.addComponent(horLayout);
	}

	private void createHeader(String group) {
		Label label = new Label(group);
		//label.addStyleName(Reindeer.LABEL_SMALL);
		mainPanel.addComponent(label);
	}

	private void createComponent() {
		mainPanel = new Panel();
		mainPanel.setStyleName("background-default");
		mainPanel.setWidth(12, Component.UNITS_EM);
		mainPanel.setHeight(100, Component.UNITS_PERCENTAGE);
	}

	@Override
	public void selectionChange(SelectionChangeEvent selectionChangeEvent) {
		updateActions(window.getActiveTab().getSelectedObjects());
	}

	public Component getComponent() {
		return mainPanel;
	}

}
