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
package org.openeos.usertask.ui.internal.notification;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.erp.core.dao.UserDAO;
import org.openeos.services.security.Principal;
import org.openeos.services.security.SecurityManagerService;
import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.notifications.Notification;
import org.openeos.services.ui.notifications.NotificationManager;
import org.openeos.usertask.UserTaskService;
import org.openeos.usertask.eventadmin.Constants;
import org.openeos.usertask.ui.internal.vaadin.VaadinComponentManager;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer.ComponentDetachEvent;
import com.vaadin.ui.ComponentContainer.ComponentDetachListener;
import com.vaadin.ui.TabSheet.Tab;

public class UsertaskNotification implements Notification, EventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(UsertaskNotification.class);

	private NotificationManager notificationManager;
	private UserTaskService userTaskService;
	private UserDAO userDAO;
	private VaadinComponentManager vaadinComponentManager;

	private boolean visible;
	private String text;
	private int ranking = 0;
	private Integer number = null;

	private ServiceRegistration<EventHandler> eventHandlerRegistration;
	private SecurityManagerService securityManagerService;

	public UsertaskNotification(UserTaskService userTaskService, UserDAO userDAO, BundleContext bc,
			VaadinComponentManager vaadinComponentManager, SecurityManagerService securityManagerService) {
		this.userTaskService = userTaskService;
		this.userDAO = userDAO;
		this.vaadinComponentManager = vaadinComponentManager;
		this.securityManagerService = securityManagerService;
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, Constants.PREFIX_TOPIC + "*");
		eventHandlerRegistration = bc.registerService(EventHandler.class, this, properties);
	}

	public void setNotificationManager(NotificationManager notificationManager) {
		this.notificationManager = notificationManager;
	}

	@Override
	public void dispose() {
		eventHandlerRegistration.unregister();
		this.notificationManager = null;
		this.userTaskService = null;
		this.userDAO = null;
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void refresh() {
		LOG.debug("Refrshing user task notification");
		int userTaskCount = retrieveUserTaskCount();
		LOG.debug("User task actual count: {}", userTaskCount);
		setVisible(true);
		setNumber(userTaskCount);
		if (userTaskCount == 0) {
			setText("You don't have pending tasks");
		} else {
			setText(MessageFormat.format("You have {0} task{1} pending", userTaskCount, userTaskCount > 1 ? "s" : ""));
		}
	}

	private int retrieveUserTaskCount() {
		Principal principal = securityManagerService.getAuthenticatedPrincipal();
		if (principal == null) {
			LOG.warn("Calling refresh without authentication context.");
			return 0;
		} else {
			return userTaskService.countTasksByAssignedUser(principal.getId());
		}
	}

	@Override
	public URL getIcon48x48URL() {
		return getClass().getResource("task_48_48.png");
	}

	@Override
	public URL getIcon32x32URL() {
		return getClass().getResource("task_32_32.png");
	}

	@Override
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Override
	public void handleEvent(Event event) {
		if (this.notificationManager != null) {
			this.notificationManager.needRefresh(this);
		}
	}

	@Override
	public void onClick(UIApplication<?> application) {
		if (IUnoVaadinApplication.class.isAssignableFrom(application.getConcreteApplicationClass())) {
			final IUnoVaadinApplication vaadinApp = (IUnoVaadinApplication) application.getConcreteApplication();
			@SuppressWarnings("unchecked")
			final Component windowComponent = vaadinComponentManager.getComponent(this,
					(UIApplication<IUnoVaadinApplication>) application);
			final Tab tab = vaadinApp.getMainTabSheet().addTab(windowComponent, "Tasks");
			tab.setClosable(true);
			vaadinApp.getMainTabSheet().addListener(new ComponentDetachListener() {

				private static final long serialVersionUID = 703653779132116478L;

				@Override
				public void componentDetachedFromContainer(ComponentDetachEvent event) {
					if (event.getDetachedComponent() == windowComponent) {
						vaadinComponentManager.disposeComponent(UsertaskNotification.this);
						vaadinApp.getMainTabSheet().removeListener(this);
					}
				}
			});
			vaadinApp.getMainTabSheet().setSelectedTab(tab);
		} else {
			application.showMessage(MessageType.WARNING, "User task graphical user interface not ready for actual user interface");
		}
	}
}
