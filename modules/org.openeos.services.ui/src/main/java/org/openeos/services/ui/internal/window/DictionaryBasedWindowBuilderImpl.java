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
package org.openeos.services.ui.internal.window;

import java.util.ArrayList;
import java.util.List;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.ui.model.IFieldDefinition;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.window.DictionaryBasedWindowBuilder;
import org.openeos.services.ui.window.DictionaryBasedWindowDefinition;
import org.openeos.services.ui.window.SimpleFieldDefinitionImpl;
import org.openeos.services.ui.window.SimpleTabDefinitionImpl;
import org.openeos.services.ui.window.SimpleWindowDefinitionImpl;

public class DictionaryBasedWindowBuilderImpl implements DictionaryBasedWindowBuilder {

	private IDictionaryService dictionaryService;

	public void setDictionaryService(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public IWindowDefinition buildWindowDefinition(DictionaryBasedWindowDefinition dictionaryBasedWindowDefinition) {
		IClassDefinition def = dictionaryService.getClassDefinition(dictionaryBasedWindowDefinition.getObjectClass());

		List<IFieldDefinition> fieldList = new ArrayList<IFieldDefinition>();
		for (String fieldName : dictionaryBasedWindowDefinition.getVisibleFieldList()) {
			SimpleFieldDefinitionImpl field = new SimpleFieldDefinitionImpl();
			field.setProperty(fieldName);
			field.setName(fieldName);
			field.setProperty(fieldName);
			fieldList.add(field);
		}

		SimpleTabDefinitionImpl tabDef = new SimpleTabDefinitionImpl();
		tabDef.setClassName(dictionaryBasedWindowDefinition.getObjectClass().getName());
		tabDef.setId(dictionaryBasedWindowDefinition.getId());
		tabDef.setName(dictionaryBasedWindowDefinition.getName());
		tabDef.setFieldList(fieldList);

		SimpleWindowDefinitionImpl windowDef = new SimpleWindowDefinitionImpl();
		windowDef.setId(dictionaryBasedWindowDefinition.getId());
		windowDef.setName(dictionaryBasedWindowDefinition.getName());
		windowDef.setRootTab(tabDef);

		return windowDef;
	}
}
