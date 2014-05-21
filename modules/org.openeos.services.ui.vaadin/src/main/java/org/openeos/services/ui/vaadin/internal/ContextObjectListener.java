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
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.openeos.services.ui.SessionObjectsService;
import org.openeos.services.ui.vaadin.ApplicationLifecycleListener;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

public class ContextObjectListener implements ApplicationLifecycleListener {

	private static final String SESSION_OBJECTS_KEY = ContextObjectListener.class.getName() + ".SESSION_OBJECTS";

	private SessionObjectsService sessionObjectService;

	public ContextObjectListener(SessionObjectsService sessionObjectService) {
		this.sessionObjectService = sessionObjectService;
	}

	@Override
	public void onInit(IUnoVaadinApplication application) {
		HttpSession session = ((WebApplicationContext) application.getMainWindow().getApplication().getContext()).getHttpSession();
		restoreSessionObjects(session, application);
		onTransactionStart(application);
	}

	@Override
	public void onClose(IUnoVaadinApplication application) {
		HttpSession session = ((WebApplicationContext) application.getMainWindow().getApplication().getContext()).getHttpSession();
		storeActualSessionObjectsForRestart(session, application);
	}

	private void storeActualSessionObjectsForRestart(HttpSession session, IUnoVaadinApplication application) {
		Map<String, Object> objectMap = new HashMap<String, Object>();
		for (String key : application.getContextObjectKeys()) {
			objectMap.put(key, application.getContextObject(key));
		}
		session.setAttribute(SESSION_OBJECTS_KEY, objectMap);
	}

	private void restoreSessionObjects(HttpSession session, IUnoVaadinApplication application) {
		@SuppressWarnings("unchecked")
		Map<String, Object> objectMap = (Map<String, Object>) session.getAttribute(SESSION_OBJECTS_KEY);
		if (objectMap != null) {
			for (Entry<String, Object> entry : objectMap.entrySet()) {
				application.setContextObject(entry.getKey(), entry.getValue());
			}
		}
		session.removeAttribute(SESSION_OBJECTS_KEY);
	}

	@Override
	public void onTransactionStart(IUnoVaadinApplication application) {
		for (String key : application.getContextObjectKeys()) {
			sessionObjectService.setSessionObject(key, application.getContextObject(key));
		}
	}

	@Override
	public void onTransactionEnd(IUnoVaadinApplication application) {
		sessionObjectService.clearSessionObjects();
	}

}
