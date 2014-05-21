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
package org.openeos.vaadin.main;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;

public interface IUnoVaadinApplication {

	public static final String VAADIN_APPLICATION_NAME = "org.openeos.vaadin.main.application";

	public void addOrSelectTab(Component c, String caption);

	public TabSheet getMainTabSheet();

	public Window getMainWindow();

	public void refreshNotifications();

	public void addCloseTabListener(CloseTabListener closeTabListener);

	public void removeCloseTabListener(CloseTabListener closeTabListener);

	public void setContextObject(String name, Object contextObject);

	public Object getContextObject(String name);

	public String[] getContextObjectKeys();

}
