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

import org.openeos.services.ui.ConfirmationCallback;
import org.openeos.services.ui.model.ITab.View;
import org.openeos.services.ui.model.IWindow;
import org.openeos.services.ui.model.WindowAction;

public abstract class AbstractPreventLostChangesWindowAction implements WindowAction {

	@Override
	public void run(final IWindow window) {
		if (window.getActiveTab().getActualView() == View.EDIT && window.getActiveTab().isModified()) {
			window.getApplication().showConfirmation("The changes will be lost, are you sure?", new ConfirmationCallback() {
				@Override
				public void onCloseConfirmation(boolean accepted) {
					if (accepted) {
						doRun(window);
					}
				}
			});
		} else {
			doRun(window);
		}

	}

	protected abstract void doRun(IWindow window);

}
