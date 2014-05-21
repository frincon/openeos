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

import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.ui.ThrowableToMessageService;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import org.openeos.vaadin.main.UnoErrorHandler;
import com.vaadin.terminal.Terminal.ErrorEvent;
import com.vaadin.ui.Window.Notification;

public class DefaultErrorHandler implements UnoErrorHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultErrorHandler.class);

	private ThrowableToMessageService throwableToMessageService;

	public DefaultErrorHandler(ThrowableToMessageService throwableToMessageService) {
		this.throwableToMessageService = throwableToMessageService;
	}

	@Override
	public void handleError(IUnoVaadinApplication application, ErrorEvent errorEvent) {
		final Throwable t = errorEvent.getThrowable();

		// Copyed from Application.handleError()
		if (t instanceof SocketException) {
			// Most likely client browser closed socket
			LOG.info("SocketException in CommunicationManager." + " Most likely client (browser) closed socket.");
			return;
		}

		// Shows the error in AbstractComponent
		// TODO Check ErrorMessage class to show error to the user
		String message = throwableToMessageService.resolveMessage(t);
		application.getMainWindow().showNotification("An unexpected error has occured", message, Notification.TYPE_ERROR_MESSAGE);

		LOG.error("An unexpected error has occured in vaadin", t);
	}

}
