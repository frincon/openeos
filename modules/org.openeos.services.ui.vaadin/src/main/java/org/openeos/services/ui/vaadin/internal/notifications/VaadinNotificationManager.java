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
package org.openeos.services.ui.vaadin.internal.notifications;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.overlay.TextOverlay;

import org.openeos.services.ui.notifications.Notification;
import org.openeos.services.ui.notifications.NotificationProvider;
import org.openeos.services.ui.vaadin.internal.UIVaadinApplicationFactory;
import org.openeos.services.ui.vaadin.internal.URLResource;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import org.openeos.vaadin.main.NotificationManager;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class VaadinNotificationManager implements NotificationManager,
		org.openeos.services.ui.notifications.NotificationManager {

	private static final Logger LOG = LoggerFactory.getLogger(VaadinNotificationManager.class);

	private long refreshIntervalInMillis = 1000 * 60;

	private Map<IUnoVaadinApplication, List<Notification>> mapNotificationsXApplication = new HashMap<IUnoVaadinApplication, List<Notification>>();
	private Map<IUnoVaadinApplication, HorizontalLayout> mapComponentXApplication = new HashMap<IUnoVaadinApplication, HorizontalLayout>();
	private Map<Notification, IUnoVaadinApplication> mapApplicationXNotification = new HashMap<Notification, IUnoVaadinApplication>();
	private Map<IUnoVaadinApplication, Long> mapLastRefreshXApplication = new HashMap<IUnoVaadinApplication, Long>();
	private Set<IUnoVaadinApplication> setApplicationNeedUpdate = new HashSet<IUnoVaadinApplication>();

	private Map<IUnoVaadinApplication, Map<URL, ApplicationResource>> mapResourceXURL = new HashMap<IUnoVaadinApplication, Map<URL, ApplicationResource>>();

	private List<NotificationProvider> notificationProviderList;
	private UIVaadinApplicationFactory applicationFactory;

	private VaadinNotificationManager(List<NotificationProvider> notificationProviderList,
			UIVaadinApplicationFactory applicationFactory) {
		this.notificationProviderList = notificationProviderList;
		this.applicationFactory = applicationFactory;
	}

	@Override
	public void registerApplication(IUnoVaadinApplication application) {
		if (notificationProviderList.size() == 0) {
			registerNotificationsForApplication(application, Collections.<Notification> emptyList());
		} else {
			for (NotificationProvider provider : notificationProviderList) {
				List<Notification> notificationList = provider.createNotifications(this);
				registerNotificationsForApplication(application, notificationList);
			}
		}
	}

	private void registerNotificationsForApplication(IUnoVaadinApplication application, List<Notification> notificationList) {
		List<Notification> notificationTotalList = mapNotificationsXApplication.get(application);
		if (notificationTotalList == null) {
			notificationTotalList = new ArrayList<Notification>();
			mapNotificationsXApplication.put(application, notificationTotalList);
		}
		notificationTotalList.addAll(notificationList);
		for (Notification notification : notificationList) {
			mapApplicationXNotification.put(notification, application);
		}
		mapLastRefreshXApplication.put(application, System.currentTimeMillis());
	}

	@Override
	public Component getComponent(IUnoVaadinApplication application) {
		return getComponentForApplication(application);
	}

	private HorizontalLayout getComponentForApplication(IUnoVaadinApplication application) {
		HorizontalLayout component = mapComponentXApplication.get(application);
		if (component == null) {
			component = createComponent(application);
			mapComponentXApplication.put(application, component);
		}
		return component;
	}

	private HorizontalLayout createComponent(IUnoVaadinApplication application) {
		HorizontalLayout layout = new HorizontalLayout();
		refreshNotifications(application);
		refreshLayout(application, layout);
		return layout;
	}

	private void refreshLayout(final IUnoVaadinApplication application, HorizontalLayout layout) {
		layout.removeAllComponents();
		for (final Notification notification : resolveAndSortNotifications(application)) {
			VerticalLayout vLayout = new VerticalLayout();
			vLayout.setMargin(false);
			vLayout.setSpacing(false);
			Button button = new Button();
			button.addListener(new Button.ClickListener() {

				private static final long serialVersionUID = 3202745949615598633L;

				@Override
				public void buttonClick(ClickEvent event) {
					notification.onClick(applicationFactory.createApplication(application));
					;
				}
			});
			button.setStyleName(Reindeer.BUTTON_LINK);
			button.setIcon(createIconResource(application, notification));
			button.setDescription(notification.getText());
			//createOverlay(application, notification, button, layout);
			vLayout.addComponent(button);
			if (notification.getNumber() != null) {
				Label label = new Label(notification.getNumber().toString());
				label.setSizeUndefined();
				vLayout.addComponent(label);
				vLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
			}
			layout.addComponent(vLayout);
		}
	}

	private Resource createIconResource(IUnoVaadinApplication application, Notification notification) {
		Map<URL, ApplicationResource> mapApplicationResource = mapResourceXURL.get(application);
		if (mapApplicationResource == null) {
			mapApplicationResource = new HashMap<URL, ApplicationResource>();
			mapResourceXURL.put(application, mapApplicationResource);
		}
		URL iconURL = notification.getIcon32x32URL();
		ApplicationResource resource = mapApplicationResource.get(iconURL);
		if (resource == null) {
			resource = new URLResource(iconURL, application.getMainWindow().getApplication());
			mapApplicationResource.put(iconURL, resource);
		}
		return resource;
	}

	private SortedSet<Notification> resolveAndSortNotifications(IUnoVaadinApplication application) {
		SortedSet<Notification> result = new TreeSet<Notification>(new Comparator<Notification>() {

			@Override
			public int compare(Notification o1, Notification o2) {
				int result = Integer.compare(o1.getRanking(), o2.getRanking());
				if (result == 0) {
					result = Integer.compare(System.identityHashCode(o1), System.identityHashCode(02));
				}
				return result;
			}
		});

		for (Notification notification : mapNotificationsXApplication.get(application)) {
			if (notification.isVisible()) {
				result.add(notification);
			}
		}
		return result;
	}

	private void createOverlay(IUnoVaadinApplication application, Notification notification, Button button, HorizontalLayout layout) {
		if (notification.getNumber() != null) {
			TextOverlay overlay = new TextOverlay(button, notification.getNumber().toString());
			overlay.setComponentAnchor(Alignment.BOTTOM_RIGHT);
			layout.addComponent(overlay);
		}
	}

	@Override
	public void updateNotifications(IUnoVaadinApplication application, boolean forceRefresh) {
		if (forceRefresh || needUpdate(application)) {
			LOG.debug("The notifications of application {} needs refresh.", application);
			refreshNotifications(application);
			mapLastRefreshXApplication.put(application, System.currentTimeMillis());
			setApplicationNeedUpdate.remove(application);
			refreshLayout(application, getComponentForApplication(application));
		}
	}

	private void refreshNotifications(IUnoVaadinApplication application) {
		for (Notification notification : mapNotificationsXApplication.get(application)) {
			notification.refresh();
		}

	}

	private boolean needUpdate(IUnoVaadinApplication application) {
		if (setApplicationNeedUpdate.contains(application)) {
			return true;
		}
		if (System.currentTimeMillis() - mapLastRefreshXApplication.get(application) > refreshIntervalInMillis) {
			return true;
		}
		return false;
	}

	@Override
	public void unregisterApplication(IUnoVaadinApplication application) {
		for (Notification notification : mapNotificationsXApplication.get(application)) {
			mapApplicationXNotification.remove(notification);
			notification.dispose();
		}
		setApplicationNeedUpdate.remove(application);
		mapLastRefreshXApplication.remove(application);
		mapNotificationsXApplication.remove(application);
		mapComponentXApplication.remove(application);
		mapResourceXURL.remove(application);
	}

	public long getRefreshIntervalInMillis() {
		return refreshIntervalInMillis;
	}

	public void setRefreshIntervalInMillis(long refreshIntervalInMillis) {
		this.refreshIntervalInMillis = refreshIntervalInMillis;
	}

	@Override
	public void needRefresh(Notification notification) {
		setApplicationNeedUpdate.add(mapApplicationXNotification.get(notification));
	}

}
