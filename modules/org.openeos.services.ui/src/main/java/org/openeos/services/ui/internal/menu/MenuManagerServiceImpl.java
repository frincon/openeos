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
package org.openeos.services.ui.internal.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.ui.menu.MenuContributor;
import org.openeos.services.ui.menu.MenuManagerService;
import org.openeos.services.ui.model.IMenuDefinition;

public class MenuManagerServiceImpl implements MenuManagerService, MenuContributor {

	private static final int ORDER = 100;
	private static final Logger LOG = LoggerFactory.getLogger(MenuManagerService.class);

	private Map<String, Set<IMenuDefinition>> childMenuMap = new HashMap<String, Set<IMenuDefinition>>();

	private Comparator<IMenuDefinition> menuComparator = new Comparator<IMenuDefinition>() {

		@Override
		public int compare(IMenuDefinition o1, IMenuDefinition o2) {
			int result = -Integer.compare(o2.getOrder(), o1.getOrder());
			if (result == 0) {
				result = o1.getName().compareTo(o2.getName());
			}
			if (result == 0) {
				result = o1.getId().compareTo(o2.getId());
			}
			if (result == 0) {
				result = Integer.compare(o1.hashCode(), o2.hashCode());
			}
			return result;
		}

	};

	private Set<IMenuDefinition> rootMenus = new TreeSet<IMenuDefinition>(menuComparator);

	private IMenuDefinition orphanMenu = new IMenuDefinition() {

		@Override
		public String getName() {
			return "Whithout Parennullt";
		}

		@Override
		public String getDescription() {
			return "Orphan menus";
		}

		@Override
		public MenuType getType() {
			return MenuType.MENU;
		}

		@Override
		public String getWindowId() {
			return null;
		}

		@Override
		public String getId() {
			return this.getClass().getName();
		}

		@Override
		public String getParentMenuId() {
			return null;
		}

		@Override
		public int getOrder() {
			return Integer.MAX_VALUE;
		}

		@Override
		public String getCustomActionId() {
			return null;
		}

	};

	@Override
	public synchronized void registerMenu(IMenuDefinition menuDefinition) {
		// TODO Check duplicates
		if (menuDefinition.getParentMenuId() == null) {
			rootMenus.add(menuDefinition);
			LOG.debug("Added new root menu definition with id '{}' and name '{}'", new Object[] { menuDefinition.getId(),
					menuDefinition.getName() });
			childMenuMap.put(menuDefinition.getId(), new TreeSet<IMenuDefinition>(menuComparator));
			checkForOrphan(menuDefinition);

		} else {
			if (childMenuMap.containsKey(menuDefinition.getParentMenuId())) {
				childMenuMap.get(menuDefinition.getParentMenuId()).add(menuDefinition);
				LOG.debug("Added new submenu whith id '{}' and name '{}' to a found parent menu",
						new Object[] { menuDefinition.getId(), menuDefinition.getName() });
				childMenuMap.put(menuDefinition.getId(), new TreeSet<IMenuDefinition>(menuComparator));
			} else {
				addOrphanMenu(menuDefinition);
			}
		}
	}

	private void checkForOrphan(IMenuDefinition menuDefinition) {
		Set<IMenuDefinition> orphanMenus = childMenuMap.get(orphanMenu.getId());
		if (orphanMenus != null) {
			List<IMenuDefinition> foundList = new ArrayList<IMenuDefinition>();
			for (IMenuDefinition orphanMenu : orphanMenus) {
				if (menuDefinition.getId().equals(orphanMenu.getParentMenuId())) {
					foundList.add(orphanMenu);
				}
			}
			if (foundList.size() > 0) {
				orphanMenus.removeAll(foundList);
				childMenuMap.get(menuDefinition.getId()).addAll(foundList);
				LOG.debug("Found {} menu definition/s in orphan than the parent was added", new Object[] { foundList.size() });
				removeOrphanIfEmpty();
			}
		}
	}

	private void removeOrphanIfEmpty() {
		Set<IMenuDefinition> childOrphanMenu = childMenuMap.get(orphanMenu.getId());
		if (childOrphanMenu != null && childOrphanMenu.size() == 0) {
			childMenuMap.remove(orphanMenu.getId());
		}
	}

	private void addOrphanMenu(IMenuDefinition menuDefinition) {
		if (!childMenuMap.containsKey(this.orphanMenu)) {
			childMenuMap.put(orphanMenu.getId(), new TreeSet<IMenuDefinition>(menuComparator));
		}
		childMenuMap.get(orphanMenu.getId()).add(menuDefinition);
		LOG.debug("Put menu definition whith id '{}' and name '{}' in orphan menus because the parent not found", new Object[] {
				menuDefinition.getId(), menuDefinition.getName() });
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	@Override
	public List<IMenuDefinition> getRootMenuDefinitionList() {
		return Collections.unmodifiableList(new ArrayList<IMenuDefinition>(rootMenus));
	}

	@Override
	public List<IMenuDefinition> getChildMenuDefinitionList(IMenuDefinition parent) {
		return Collections.unmodifiableList(new ArrayList<IMenuDefinition>(childMenuMap.get(parent.getId())));
	}

	@Override
	public void unregisterAllMenus(Collection<IMenuDefinition> menuDefinitions) {
		for (IMenuDefinition menuDef : menuDefinitions) {
			unregisterMenu(menuDef);
		}
	}

	@Override
	public void unregisterMenu(IMenuDefinition menuDefinition) {
		// Remove from root menus collection
		rootMenus.remove(menuDefinition);

		// Remove the children if any, and add to orphan.
		Set<IMenuDefinition> child = childMenuMap.get(menuDefinition.getId());
		for (IMenuDefinition def : child) {
			addOrphanMenu(def);
		}

		//Remove from parent
		Set<IMenuDefinition> childOfParent = childMenuMap.get(menuDefinition.getParentMenuId());
		if (childOfParent != null) {
			childOfParent.remove(menuDefinition);
		}

		// Remove from orphan
		Set<IMenuDefinition> childOfOrphan = childMenuMap.get(orphanMenu.getId());
		if (childOfOrphan != null) {
			childOfOrphan.remove(menuDefinition);
			removeOrphanIfEmpty();
		}
	}

	@Override
	public void registerAllMenus(Collection<IMenuDefinition> menuDefinitions) {
		for (IMenuDefinition menuDef : menuDefinitions) {
			registerMenu(menuDef);
		}
	}

}
