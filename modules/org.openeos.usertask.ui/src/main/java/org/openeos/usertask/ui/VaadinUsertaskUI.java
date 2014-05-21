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
package org.openeos.usertask.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIApplication;
import org.openeos.usertask.UserTask;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Component;

public abstract class VaadinUsertaskUI implements UsertaskUI {

	private static final Logger LOG = LoggerFactory.getLogger(VaadinUsertaskUI.class);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T createComponent(UserTask userTask, UIApplication<?> application, Class<T> componentClass) {
		if (IUnoVaadinApplication.class.isAssignableFrom(application.getConcreteApplicationClass())
				&& Component.class.isAssignableFrom(componentClass)) {
			return (T) createVaadinComponent(userTask, (UIApplication<IUnoVaadinApplication>) application);
		} else {
			application.showMessage(MessageType.WARNING, "This user task is not implemented for the graphical framework in use");
			LOG.debug("Requested user task UI than not implemented for GUI framework. "
					+ "Application concrete class is '{}', component requested is '{}', and user task id is '{}'", new Object[] {
					application.getConcreteApplicationClass(), componentClass, getId() });
			return null;
		}
	}

	protected abstract Component createVaadinComponent(UserTask userTask, UIApplication<IUnoVaadinApplication> application);

}
