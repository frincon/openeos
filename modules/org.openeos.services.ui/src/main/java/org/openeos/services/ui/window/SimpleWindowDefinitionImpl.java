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
package org.openeos.services.ui.window;

import org.openeos.services.ui.model.ITabDefinition;
import org.openeos.services.ui.model.IWindowDefinition;

public class SimpleWindowDefinitionImpl implements IWindowDefinition {

	private String id;
	private String name;
	private String description;
	private String help;
	private ITabDefinition rootTab;

	
	
	public SimpleWindowDefinitionImpl() {
	}

	public SimpleWindowDefinitionImpl(String id, String name, String description, String help, ITabDefinition rootTab) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.help = help;
		this.rootTab = rootTab;
	}

	@Override
	public String getId() {
		return id;
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
	public String getHelp() {
		return help;
	}

	@Override
	public ITabDefinition getRootTab() {
		return rootTab;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public void setRootTab(ITabDefinition rootTab) {
		this.rootTab = rootTab;
	}

}
