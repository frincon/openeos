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
package org.openeos.services.ui.vaadin.internal;

import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.IPropertyDefinition;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.services.ui.vaadin.internal.containers.UIDAOItem;
import org.openeos.services.ui.vaadin.internal.containers.UIDAOQueryDefinition;
import org.openeos.services.ui.vaadin.internal.containers.UIDAOQueryFactory;
import com.vaadin.data.Container;
import com.vaadin.data.Item;

public class DefaultVaadinContainerFactory implements IVaadinContainerFactory {

	private IDictionaryService dictionaryService;
	private UIDAOService uidaoService;

	public DefaultVaadinContainerFactory(IDictionaryService dictionaryService, UIDAOService uidaoService) {
		this.dictionaryService = dictionaryService;
		this.uidaoService = uidaoService;
	}

	@Override
	public Container createContainer(String persistentClass) {
		return new LazyQueryContainer(generateQueryDefinition(persistentClass), new UIDAOQueryFactory(uidaoService, this));
	}

	private QueryDefinition generateQueryDefinition(String persistentClass) {
		UIDAOQueryDefinition queryDefinition = new UIDAOQueryDefinition(50, dictionaryService.getClassDefinition(persistentClass)
				.getClassDefined());
		for (IPropertyDefinition propDef : dictionaryService.getClassDefinition(persistentClass).getAllPropertyDefinitionList()) {
			// TODO: Make better
			queryDefinition.addProperty(propDef.getName(), propDef.getPropertyClass(), null, false, true);
		}
		return queryDefinition;
	}

	@Override
	public Item createItem(UIBean bean) {
		return new UIDAOItem(bean);
	}

}
