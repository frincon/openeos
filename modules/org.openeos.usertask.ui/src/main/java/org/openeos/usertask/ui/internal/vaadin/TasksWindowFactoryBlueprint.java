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

import org.osgi.service.blueprint.container.BlueprintContainer;

import org.openeos.services.ui.UIApplication;
import org.openeos.vaadin.main.IUnoVaadinApplication;

public class TasksWindowFactoryBlueprint implements TasksWindowFactory {

	private BlueprintContainer blueprintContainer;
	private String beanName;

	public TasksWindowFactoryBlueprint(BlueprintContainer blueprintContainer, String beanName) {
		this.blueprintContainer = blueprintContainer;
		this.beanName = beanName;
	}

	@Override
	public TasksWindow createTasksWindow(UIApplication<IUnoVaadinApplication> application) {
		TasksWindow tasksWindow = (TasksWindow) blueprintContainer.getComponentInstance(beanName);
		tasksWindow.setApplication(application);
		return tasksWindow;
	}
}
