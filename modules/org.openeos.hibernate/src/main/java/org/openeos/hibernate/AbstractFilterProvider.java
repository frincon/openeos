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
package org.openeos.hibernate;


public abstract class AbstractFilterProvider implements FilterProvider {

	private BasicFilterDefinition[] basicFilterDefinitionList;

	public AbstractFilterProvider(BasicFilterDefinition[] basicFilterDefinitionList) {
		this.basicFilterDefinitionList = basicFilterDefinitionList;
	}

	public BasicFilterDefinition[] getBasicFilterDefinitionList() {
		return basicFilterDefinitionList;
	}

	public void setBasicFilterDefinitionList(BasicFilterDefinition[] basicFilterDefinitionList) {
		this.basicFilterDefinitionList = basicFilterDefinitionList;
	}

	@Override
	public BasicFilterDefinition[] getFilterDefinitions() {
		return basicFilterDefinitionList;
	}

}
