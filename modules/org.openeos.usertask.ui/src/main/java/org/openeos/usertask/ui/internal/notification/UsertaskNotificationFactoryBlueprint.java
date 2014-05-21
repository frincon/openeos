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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.ui.notifications.Notification;
import org.openeos.services.ui.notifications.NotificationManager;

public class UsertaskNotificationFactoryBlueprint implements UsertaskNotificationFactory {

	private static final Logger LOG = LoggerFactory.getLogger(UsertaskNotificationFactoryBlueprint.class);

	private BlueprintContainer blueprintContainer;
	private String beanName;

	public UsertaskNotificationFactoryBlueprint(BlueprintContainer blueprintContainer, String beanName) {
		this.blueprintContainer = blueprintContainer;
		this.beanName = beanName;
	}

	@Override
	public Notification createUsertaskNotification(NotificationManager notificationManager) {
		Notification notification = (Notification) blueprintContainer.getComponentInstance(beanName);
		try {
			PropertyUtils.setProperty(notification, "notificationManager", notificationManager);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LOG.warn("Impossible set notification manager to notification object", e);
		}
		return notification;

	}

}
