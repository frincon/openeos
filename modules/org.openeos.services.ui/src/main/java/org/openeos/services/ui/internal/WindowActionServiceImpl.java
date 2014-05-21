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
package org.openeos.services.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openeos.services.ui.WindowActionContributor;
import org.openeos.services.ui.WindowActionService;
import org.openeos.services.ui.model.IWindow;
import org.openeos.services.ui.model.WindowAction;

public class WindowActionServiceImpl implements WindowActionService {

	private ArrayList<WindowActionContributor> contributorList = new ArrayList<WindowActionContributor>();

	@Override
	public List<WindowAction> getWindowActionList(IWindow window) {
		ArrayList<WindowAction> windowActionList = new ArrayList<WindowAction>();
		for (WindowActionContributor windowActionContributor : contributorList) {
			windowActionList.addAll(windowActionContributor.getWindowActionList());
		}
		Collections.sort(windowActionList, new Comparator<WindowAction>() {

			@Override
			public int compare(WindowAction o1, WindowAction o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		return Collections.unmodifiableList(windowActionList);
	}

	public void addActionContributor(WindowActionContributor windowActionContributor) {
		contributorList.add(windowActionContributor);
	}

	public void removeActionContributor(WindowActionContributor windowActionContributor) {
		contributorList.remove(windowActionContributor);
	}

}
