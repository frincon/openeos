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
package org.openeos.numeration.ui;

import org.openeos.erp.core.ui.CoreMenus;
import org.openeos.services.ui.menu.AbstractMenuProvider;
import org.openeos.services.ui.model.IMenuDefinition;
import org.openeos.services.ui.model.IMenuDefinition.MenuType;

public class NumerationMenus extends AbstractMenuProvider {

	public IMenuDefinition MENU_SEQUENCE = newMenu().parent(CoreMenus.MENU_ACCOUNTING_SETUP_ID).action(MenuType.WINDOW)
			.window(NumerationWindows.SequenceWindow.ID).name("Sequences").description("Sequences Menu").order(-100).build();

}
