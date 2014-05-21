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
package org.openeos.services.ui.vaadin.internal;

import java.util.HashMap;
import java.util.Map;

import org.openeos.services.ui.UIApplication;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class CachedApplicationFactoryDecorator implements UIVaadinApplicationFactory {

	private UIVaadinApplicationFactory delegate;

	private Map<IUnoVaadinApplication, UIApplication<IUnoVaadinApplication>> cacheMap = new HashMap<IUnoVaadinApplication, UIApplication<IUnoVaadinApplication>>();

	public CachedApplicationFactoryDecorator(UIVaadinApplicationFactory delegate) {
		this.delegate = delegate;
	}

	@Override
	public UIApplication<IUnoVaadinApplication> createApplication(final IUnoVaadinApplication application) {
		UIApplication<IUnoVaadinApplication> uiApplication = cacheMap.get(application);
		if (uiApplication == null) {
			uiApplication = delegate.createApplication(application);
			application.getMainWindow().addListener(new CloseListener() {

				private static final long serialVersionUID = 8932963545406877665L;

				@Override
				public void windowClose(CloseEvent e) {
					// TODO Check if this is called when the session expired
					cacheMap.remove(application);
				}

			});
			cacheMap.put(application, uiApplication);
		}
		return uiApplication;
	}

}
