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

import org.openeos.services.ui.UIApplication;
import org.openeos.usertask.ui.internal.notification.UsertaskNotification;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Component;

public interface VaadinComponentManager {

	Component getComponent(UsertaskNotification usertaskNotification, UIApplication<IUnoVaadinApplication> vaadinApp);

	void disposeComponent(UsertaskNotification usertaskNoticiation);

}
