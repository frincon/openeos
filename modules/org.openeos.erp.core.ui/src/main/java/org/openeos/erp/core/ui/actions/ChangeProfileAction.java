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
package org.openeos.erp.core.ui.actions;

import org.hibernate.SessionFactory;

import org.openeos.erp.core.model.Client;
import org.openeos.erp.core.ui.beans.ChangeProfileBean;
import org.openeos.erp.core.ui.forms.ChangeProfileForm;
import org.openeos.erp.core.ui.internal.ClientContextObjectContributor;
import org.openeos.erp.core.ui.internal.ClientFilterProvider;
import org.openeos.services.ui.SessionObjectsService;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.UIDialog;
import org.openeos.services.ui.UIDialogCloseEvent;
import org.openeos.services.ui.UIDialogListener;
import org.openeos.services.ui.action.CustomAction;

public class ChangeProfileAction implements CustomAction {

	public static final String ID = ChangeProfileAction.class.getName();

	private SessionObjectsService sessionObjectsService;
	private SessionFactory sessionFactory;
	private ClientFilterProvider clientFilterProvider;

	public ChangeProfileAction(SessionObjectsService sessionObjectsService, SessionFactory sessionFactory,
			ClientFilterProvider clientFilterProvider) {
		this.sessionObjectsService = sessionObjectsService;
		this.sessionFactory = sessionFactory;
		this.clientFilterProvider = clientFilterProvider;
	}

	@Override
	public void run(final UIApplication<?> application) {
		final ChangeProfileBean bean = new ChangeProfileBean();
		final Client oldClient = (Client) sessionObjectsService
				.getSessionObject(ClientContextObjectContributor.CONTEXT_OBJECT_CLIENT_NAME);

		bean.setActualClient(oldClient);
		ChangeProfileForm form = new ChangeProfileForm(sessionFactory, clientFilterProvider);
		UIDialog dialog = application.showGenericFormDialog(form, bean);
		dialog.setUIDialogListener(new UIDialogListener() {

			@Override
			public void onClose(UIDialogCloseEvent event) {
				if (event.getButtonId().equals(UIDialog.BUTTON_OK) && !oldClient.getId().equals(bean.getActualClient().getId())) {
					sessionObjectsService.setSessionObject(ClientContextObjectContributor.CONTEXT_OBJECT_CLIENT_NAME,
							bean.getActualClient());
					application.setContextObject(ClientContextObjectContributor.CONTEXT_OBJECT_CLIENT_NAME, bean.getActualClient());
					application.restart();
				}
			}
		});
	}

	@Override
	public String getId() {
		return ID;
	}

}
