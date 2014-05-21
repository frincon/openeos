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

import org.openeos.services.ui.model.ITab.View;
import org.openeos.services.ui.model.IWindow;

public class ViewListWindowAction extends AbstractPreventLostChangesWindowAction {

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doRun(IWindow window) {
		window.getActiveTab().showView(View.LIST);
	}

	@Override
	public String getCaption() {
		// TODO i18n
		return "List";
	}

	@Override
	public boolean isVisibeForWindow(IWindow window) {
		return true;
	}

	@Override
	public boolean isEnabledForWindow(IWindow window) {
		return window.getActiveTab().getActualView() == View.EDIT;
	}

}
