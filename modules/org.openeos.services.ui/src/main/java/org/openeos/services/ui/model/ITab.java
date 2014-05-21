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
package org.openeos.services.ui.model;

import java.util.List;

import org.openeos.services.ui.UIBean;

public interface ITab extends SelectionChangeNotifier, ViewChangeNotifier {

	public enum View {
		LIST, EDIT
	}

	public void refresh(RefreshType refreshType);

	public ITabDefinition getTabDefinition();

	public UIBean getActiveObject();

	public List<UIBean> getSelectedObjects();

	public void setActiveObject(UIBean obj);

	public void showView(View view);

	public void showView(View view, UIBean activeObject);

	public View getActualView();

	public boolean isModified();

	public boolean validate();

}
