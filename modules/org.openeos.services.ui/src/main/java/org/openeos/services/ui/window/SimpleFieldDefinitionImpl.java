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

import org.openeos.services.ui.model.IFieldDefinition;

public class SimpleFieldDefinitionImpl implements IFieldDefinition {

	private String property;
	private String name;
	private String description;
	private String help;

	public SimpleFieldDefinitionImpl() {
	}

	public SimpleFieldDefinitionImpl(String property, String name, String description, String help) {
		this.property = property;
		this.name = name;
		this.description = description;
		this.help = help;
	}

	@Override
	public String getProperty() {
		return property;
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

	public void setProperty(String property) {
		this.property = property;
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

}
