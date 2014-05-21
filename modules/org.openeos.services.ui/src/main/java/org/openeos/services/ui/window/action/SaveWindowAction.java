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

import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIDAOCoinstraintValidationException;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.model.ITab.View;
import org.openeos.services.ui.model.IWindow;
import org.openeos.services.ui.model.RefreshType;
import org.openeos.services.ui.model.WindowAction;

public class SaveWindowAction implements WindowAction {

	private UIDAOService uidaoService;

	public SaveWindowAction(UIDAOService uidaoService) {
		this.uidaoService = uidaoService;
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run(IWindow window) {
		try {
			if (window.getActiveTab().validate()) {
				uidaoService.save(window.getActiveTab().getActiveObject());
				uidaoService.refresh(window.getActiveTab().getActiveObject());
				window.refresh(RefreshType.SELECTED);
				window.showMessage(MessageType.INFO, "Item saved");
			}
		} catch (UIDAOCoinstraintValidationException e) {
			window.showMessage(MessageType.ERROR, "Error saving object", "Can't save object because constraint exception");
		}
	}

	@Override
	public String getCaption() {
		// TODO i18n
		return "Save";
	}

	@Override
	public boolean isVisibeForWindow(IWindow window) {
		return true;
	}

	@Override
	public boolean isEnabledForWindow(IWindow window) {
		// TODO Check if is in stale state
		return window.getActiveTab().getActualView() == View.EDIT;
	}

}
