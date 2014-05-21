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
package org.openeos.extender.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;

import org.openeos.extender.ui.Constants;
import org.openeos.services.ui.menu.MenuManagerService;
import org.openeos.services.ui.menu.MenuProvider;
import org.openeos.services.ui.model.IMenuDefinition;
import org.openeos.utils.ClassListExtenderHandler;

public class MenuExtenderHandler extends ClassListExtenderHandler<MenuProvider> {

	private MenuManagerService menuManagerService;
	private Map<Long, List<IMenuDefinition>> mapMenuRegistered = new HashMap<Long, List<IMenuDefinition>>();

	public void setMenuManagerService(MenuManagerService menuManagerService) {
		this.menuManagerService = menuManagerService;
	}

	@Override
	public String getHeaderName() {
		return Constants.HEADER_MENU_CLASSES;
	}

	@Override
	public void starting() {
		if (this.menuManagerService == null) {
			throw new UnsupportedOperationException("Not enought services");
		}
	}

	@Override
	public void stopping() {
		for (List<IMenuDefinition> menuDefList : mapMenuRegistered.values()) {
			menuManagerService.unregisterAllMenus(menuDefList);
		}
		mapMenuRegistered.clear();
	}

	@Override
	public void onBundleDeparture(Bundle bundle) {
		List<IMenuDefinition> menuDefList = mapMenuRegistered.get(bundle.getBundleId());
		if (menuDefList != null) {
			menuManagerService.unregisterAllMenus(menuDefList);
		}
		mapMenuRegistered.remove(bundle.getBundleId());
	}

	@Override
	protected void processObject(Bundle bundle, MenuProvider objectCreated) {
		List<IMenuDefinition> menuDefList = objectCreated.getMenuList();
		menuManagerService.registerAllMenus(menuDefList);
		mapMenuRegistered.put(bundle.getBundleId(), new ArrayList<IMenuDefinition>(menuDefList));
	}

	@Override
	protected Class<MenuProvider> getExpectedClass() {
		return MenuProvider.class;
	}

}
