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
package org.openeos.usertask.ui.internal.vaadin;

import java.util.List;

import org.openeos.services.security.Principal;
import org.openeos.services.security.SecurityManagerService;
import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.vaadin.Resources;
import org.openeos.usertask.UserTask;
import org.openeos.usertask.UserTaskService;
import org.openeos.usertask.model.list.TaskStatus;
import org.openeos.usertask.ui.UsertaskUI;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class TasksWindow {

	private UserTaskService userTaskService;
	private SecurityManagerService securityManagerService;
	private UsertaskUIManager usertaskUIManager;
	private UIApplication<IUnoVaadinApplication> application;

	private Component mainComponent;
	private HorizontalSplitPanel mainSplitPanel;
	private Table taskTable;

	private boolean initialized = false;

	//private BeanTableDefinition beanTableDefinition;

	public TasksWindow(UserTaskService userTaskService, SecurityManagerService securityManagerService,
			UsertaskUIManager usertaskUIManager) {
		this.userTaskService = userTaskService;
		this.securityManagerService = securityManagerService;
		this.usertaskUIManager = usertaskUIManager;
	}

	public Component getComponent() {
		if (!initialized) {
			init();
			initialized = true;
		}
		return mainComponent;
	}

	private void init() {
		createMainComponent();
		refresh();
	}

	public void dispose() {
		mainComponent = null;
	}

	private void createMainComponent() {
		VerticalLayout main = new VerticalLayout();
		main.setMargin(false);
		main.setSizeFull();
		main.addComponent(createToolbar());

		mainSplitPanel = new HorizontalSplitPanel();
		mainSplitPanel.setMargin(false);
		mainSplitPanel.setMaxSplitPosition(80f, HorizontalSplitPanel.UNITS_PERCENTAGE);
		mainSplitPanel.setMinSplitPosition(20f, HorizontalSplitPanel.UNITS_PERCENTAGE);
		mainSplitPanel.setSizeFull();
		mainSplitPanel.setSplitPosition(30f, HorizontalSplitPanel.UNITS_PERCENTAGE);

		taskTable = createTable();
		mainSplitPanel.setFirstComponent(taskTable);

		displayEmptyTask();

		main.addComponent(mainSplitPanel);
		main.setExpandRatio(mainSplitPanel, 1.0f);
		mainComponent = main;
	}

	private Table createTable() {
		Table table = new Table() {

			private static final long serialVersionUID = 4959270991891720440L;

			@Override
			protected String formatPropertyValue(Object rowId, Object colId, Property property) {
				if (TaskStatus.class.isAssignableFrom(property.getType())) {
					return ((TaskStatus) property.getValue()).getDescription();
				} else {
					return super.formatPropertyValue(rowId, colId, property);
				}
			}

		};
		table.setSizeFull();
		table.setSelectable(true);
		table.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = -6661908225374989099L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				selectionChanged();
			}
		});
		table.setImmediate(true);
		return table;
	}

	private Component createToolbar() {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setMargin(false);
		hLayout.setWidth("100%");
		hLayout.setSpacing(false);
		hLayout.setStyleName(Reindeer.LAYOUT_BLACK);
		addToolbarButtons(hLayout);
		return hLayout;
	}

	private void addToolbarButtons(HorizontalLayout hLayout) {
		HorizontalLayout newLayout = new HorizontalLayout();
		newLayout.setMargin(false);
		Button buttonRefresh = new Button();
		buttonRefresh.setStyleName(Reindeer.BUTTON_LINK);
		buttonRefresh.setIcon(Resources.ICON_32_REFRESH);
		buttonRefresh.setDescription("Refresh");
		buttonRefresh.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				refresh();
			}
		});
		newLayout.addComponent(buttonRefresh);

		Button buttonNew = new Button();
		buttonNew.setStyleName(Reindeer.BUTTON_LINK);
		buttonNew.setIcon(Resources.ICON_32_NEW);
		buttonNew.setDescription("New task");
		buttonNew.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				newTask();
			}
		});
		newLayout.addComponent(buttonNew);

		hLayout.addComponent(newLayout);
	}

	private void refresh() {
		Principal principal = securityManagerService.getAuthenticatedPrincipal();
		List<UserTask> userTaskList = userTaskService.findTasksByAssignedUser(principal.getId());
		BeanItemContainer<UserTask> container = new BeanItemContainer<UserTask>(UserTask.class, userTaskList);
		taskTable.setContainerDataSource(container);
		taskTable.setVisibleColumns(new Object[] { "name", "priority", "status" });
	}

	private void newTask() {

	}

	private void selectionChanged() {
		Object objSelected = taskTable.getValue();
		if (objSelected instanceof UserTask) {
			displayTask((UserTask) objSelected);
		} else {
			displayEmptyTask();
		}
	}

	private void displayTask(UserTask task) {
		Panel mainPanel = new Panel();
		mainPanel.setStyleName("background-default");
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setWidth(100f, VerticalLayout.UNITS_PERCENTAGE);
		mainPanel.setContent(layout);
		mainPanel.setSizeFull();

		mainPanel.addComponent(createTaskTitle(task));
		mainPanel.addComponent(createTaskSummary(task));

		Component customComponent = createCustomComponent(task);
		if (customComponent != null) {
			mainPanel.addComponent(customComponent);
		}

		mainSplitPanel.setSecondComponent(mainPanel);

	}

	private Component createCustomComponent(UserTask task) {
		String uiId = task.getMetaData(UsertaskUI.META_UI_ID);
		if (uiId != null) {
			return usertaskUIManager.getUsertaskUIById(uiId).createComponent(task, application, Component.class);
		}
		return null;
	}

	private Component createTaskTitle(UserTask task) {
		Label label = new Label(task.getName());
		label.setStyleName(Reindeer.LABEL_H2);
		return label;
	}

	private Component createTaskSummary(UserTask task) {

		//		TextField name = new TextField("Name");
		//		name.setValue(task.getName());
		//		name.setReadOnly(true);
		//		name.setWidth("100%");

		TextField priority = new TextField("Priority");
		priority.setValue(Integer.toString(task.getPriority()));
		priority.setReadOnly(true);

		TextField status = new TextField("Status");
		status.setValue(task.getStatus().getDescription());
		status.setReadOnly(true);

		TextArea description = new TextArea("Description");
		description.setSizeFull();
		description.setValue(task.getDescription());
		description.setReadOnly(true);
		description.setRows(3);

		ComponentContainer buttons = createSummaryButtons(task);

		VerticalLayout secondColumnFields = new VerticalLayout();
		secondColumnFields.setMargin(false);
		secondColumnFields.setSizeFull();

		secondColumnFields.addComponent(priority);
		secondColumnFields.addComponent(status);

		HorizontalLayout fieldsLayout = new HorizontalLayout();
		fieldsLayout.setSizeFull();
		fieldsLayout.setMargin(false);
		fieldsLayout.setSpacing(false);
		fieldsLayout.addComponent(description);
		fieldsLayout.addComponent(secondColumnFields);
		fieldsLayout.setExpandRatio(description, 4.0f);
		fieldsLayout.setExpandRatio(secondColumnFields, 1.0f);

		HorizontalLayout mainLayout = new HorizontalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(false);
		mainLayout.setSizeFull();
		mainLayout.addComponent(fieldsLayout);
		mainLayout.addComponent(buttons);
		mainLayout.setComponentAlignment(buttons, Alignment.TOP_LEFT);
		mainLayout.setExpandRatio(fieldsLayout, 1.0f);

		Panel panel = new Panel("Summary");
		panel.setStyleName("background-transparent");
		panel.setContent(mainLayout);

		return panel;
	}

	private ComponentContainer createSummaryButtons(final UserTask task) {

		Button completeButton = new Button("Task completed");
		completeButton.setWidth("100%");
		completeButton.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				completeTask(task);
			}
		});

		VerticalLayout buttonsLayout = new VerticalLayout();
		buttonsLayout.addComponent(completeButton);
		buttonsLayout.setWidth(16, VerticalLayout.UNITS_EM);
		return buttonsLayout;

	}

	private void completeTask(UserTask task) {
		userTaskService.completeTask(task);
		refresh();
		application.refreshNotifications();
		application.showMessage(MessageType.INFO, "Task completed");
	}

	private void displayEmptyTask() {
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setStyleName("background-default");
		mainSplitPanel.setSecondComponent(panel);
	}

	public void setApplication(UIApplication<IUnoVaadinApplication> application) {
		this.application = application;
	}
}
