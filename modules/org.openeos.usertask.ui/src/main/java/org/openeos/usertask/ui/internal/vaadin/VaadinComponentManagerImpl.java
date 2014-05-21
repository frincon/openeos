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

import java.util.HashMap;
import java.util.Map;

import org.openeos.services.ui.UIApplication;
import org.openeos.usertask.ui.internal.notification.UsertaskNotification;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Component;

public class VaadinComponentManagerImpl implements VaadinComponentManager {

	private Map<UsertaskNotification, TasksWindow> windowMap = new HashMap<UsertaskNotification, TasksWindow>();

	private TasksWindowFactory tasksWindowFactory;

	public VaadinComponentManagerImpl(TasksWindowFactory tasksWindowFactory) {
		this.tasksWindowFactory = tasksWindowFactory;
	}

	@Override
	public Component getComponent(UsertaskNotification usertaskNotification, UIApplication<IUnoVaadinApplication> vaadinApp) {
		TasksWindow window = windowMap.get(usertaskNotification);
		if (window == null) {
			window = createTasksWindow(vaadinApp);
			windowMap.put(usertaskNotification, window);
		}
		return window.getComponent();
	}

	private TasksWindow createTasksWindow(UIApplication<IUnoVaadinApplication> vaadinApp) {
		return tasksWindowFactory.createTasksWindow(vaadinApp);
	}

	@Override
	public void disposeComponent(UsertaskNotification usertaskNotification) {
		TasksWindow window = windowMap.remove(usertaskNotification);
		if (window != null) {
			window.dispose();
		}
	}

}
