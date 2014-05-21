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

import org.openeos.services.ui.model.IMenuDefinition;
import org.openeos.services.ui.model.IMenuDefinition.MenuType;

public interface MenuBuilder {

	public MenuBuilder id(String id);

	public MenuBuilder action(MenuType menuType);

	public MenuBuilder parent(String parentId);

	public MenuBuilder parent(IMenuDefinition menuDefinition);

	public MenuBuilder name(String name);

	public MenuBuilder description(String description);

	public MenuBuilder order(int order);

	public IMenuDefinition build();

	public MenuBuilder window(String windowId);

	public MenuBuilder customAction(String id);

}
