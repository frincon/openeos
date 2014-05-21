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
package org.openeos.services.ui.window.action;

import org.springframework.transaction.annotation.Transactional;

import org.openeos.services.ui.ConfirmationCallback;
import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.model.ITab.View;
import org.openeos.services.ui.model.IWindow;
import org.openeos.services.ui.model.RefreshType;
import org.openeos.services.ui.model.WindowAction;

public class DeleteWindowAction implements WindowAction {

	UIDAOService uidaoService;

	public DeleteWindowAction(UIDAOService uidaoService) {
		this.uidaoService = uidaoService;
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run(final IWindow window) {
		window.getApplication().showConfirmation("Are you sure to delete items?", new ConfirmationCallback() {

			@Override
			public void onCloseConfirmation(boolean accepted) {
				if (accepted) {
					doDelete(window);
				}
			}
		});
	}

	@Transactional
	private void doDelete(IWindow window) {
		int count = window.getActiveTab().getSelectedObjects().size();
		uidaoService.deleteAll(window.getActiveTab().getSelectedObjects());
		window.getActiveTab().showView(View.LIST);
		window.refresh(RefreshType.ALL);
		// TODO i18n
		window.showMessage(MessageType.INFO, String.format("Elements deleted: %d", count));
	}

	@Override
	public String getCaption() {
		// TODO I18N
		return "Delete";
	}

	@Override
	public boolean isVisibeForWindow(IWindow window) {
		return true;
	}

	@Override
	public boolean isEnabledForWindow(IWindow window) {
		int count = window.getActiveTab().getSelectedObjects().size();
		return count > 0;
	}

}
