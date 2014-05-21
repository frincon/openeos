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
package org.openeos.services.ui.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.openeos.services.ui.model.IMenuDefinition;
import org.openeos.services.ui.model.IMenuDefinition.MenuType;

public abstract class AbstractMenuProvider implements MenuProvider {

	private static final AtomicInteger counter = new AtomicInteger();

	private List<IMenuDefinition> menuDefinitionList = new ArrayList<IMenuDefinition>();

	private class MenuDefinitionImpl implements IMenuDefinition {

		private String name;
		private String description;
		private MenuType type;
		private String windowId;
		private String id;
		private int order;
		private String parentMenuId;
		private String customActionId;

		public MenuDefinitionImpl(String name, String description, MenuType type, String windowId, String id, int order,
				String parentMenuId, String customActionId) {
			this.name = name;
			this.description = description;
			this.type = type;
			this.windowId = windowId;
			this.id = id;
			this.order = order;
			this.parentMenuId = parentMenuId;
			this.customActionId = customActionId;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public MenuType getType() {
			return type;
		}

		@Override
		public String getWindowId() {
			return windowId;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public int getOrder() {
			return order;
		}

		@Override
		public String getParentMenuId() {
			return parentMenuId;
		}

		public String getCustomActionId() {
			return customActionId;
		}

	}

	private class MenuBuilderImpl implements MenuBuilder {

		private String id;
		private String parentId;
		private int order = DEFAULT_MENU_ORDER;
		private String name;
		private String description;
		private MenuType action;
		private String windowId;
		private String customActionId;

		@Override
		public MenuBuilder window(String windowId) {
			this.windowId = windowId;
			return this;
		}

		@Override
		public MenuBuilder parent(IMenuDefinition menuDefinition) {
			return parent(menuDefinition.getId());
		}

		@Override
		public MenuBuilder parent(String parentId) {
			this.parentId = parentId;
			return this;
		}

		@Override
		public MenuBuilder order(int order) {
			this.order = order;
			return this;
		}

		@Override
		public MenuBuilder name(String name) {
			this.name = name;
			return this;
		}

		@Override
		public MenuBuilder description(String description) {
			this.description = description;
			return this;
		}

		@Override
		public IMenuDefinition build() {
			if (action == null || name == null) {
				throw new UnsupportedOperationException();
			}
			if (id == null) {
				id = AbstractMenuProvider.class.getPackage().getName() + "." + counter.incrementAndGet();
			}
			IMenuDefinition def = new MenuDefinitionImpl(name, description, action, windowId, id, order, parentId, customActionId);
			menuDefinitionList.add(def);
			return def;
		}

		@Override
		public MenuBuilder action(MenuType action) {
			this.action = action;
			return this;
		}

		@Override
		public MenuBuilder id(String id) {
			this.id = id;
			return this;
		}

		@Override
		public MenuBuilder customAction(String customActionId) {
			this.customActionId = customActionId;
			return this;
		}
	};

	public final MenuBuilder newMenu() {
		return new MenuBuilderImpl();
	}

	@Override
	public List<IMenuDefinition> getMenuList() {
		return Collections.unmodifiableList(menuDefinitionList);
	}

}
