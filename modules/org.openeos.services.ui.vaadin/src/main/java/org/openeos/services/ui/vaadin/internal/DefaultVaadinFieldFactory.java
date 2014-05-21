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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.services.ui.vaadin.internal.components.EntitySelect;
import org.openeos.services.ui.vaadin.internal.components.EntitySelectIdProvider;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Select;
import com.vaadin.ui.TableFieldFactory;

public class DefaultVaadinFieldFactory implements FormFieldFactory, TableFieldFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5003406833754162037L;
	private static final Logger LOG = LoggerFactory.getLogger(DefaultVaadinFieldFactory.class);

	private IDictionaryService dictionaryService;
	private IVaadinContainerFactory containerFactory;

	public DefaultVaadinFieldFactory(IDictionaryService dictionaryService, IVaadinContainerFactory containerFactory) {
		this.dictionaryService = dictionaryService;
		this.containerFactory = containerFactory;
	}

	@Override
	public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
		Property containerProperty = container.getContainerProperty(itemId, propertyId);
		Class<?> type = containerProperty.getType();
		Field field = createFieldByPropertyType(type);
		field.setCaption(createCaptionByPropertyId(propertyId));
		return field;
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		Class<?> type = item.getItemProperty(propertyId).getType();
		Field field = createFieldByPropertyType(type);
		field.setCaption(createCaptionByPropertyId(propertyId));
		return field;
	}

	private String createCaptionByPropertyId(Object propertyId) {
		return DefaultFieldFactory.createCaptionByPropertyId(propertyId);
	}

	private Field createFieldByPropertyType(Class<?> type) {
		final IClassDefinition classDefinition = dictionaryService.getClassDefinition(type.getName());
		if (classDefinition != null) {
			LOG.debug("Found field with dictionary in dictionary service for class {}, create new Select", type.getName());
			Select select = new EntitySelect(new EntitySelectIdProvider() {

				@Override
				public Object getId(Object entityObject) {
					return classDefinition.getId(entityObject);
				}
			});
			select.setItemCaptionMode(Select.ITEM_CAPTION_MODE_ITEM);
			select.setContainerDataSource(containerFactory.createContainer(classDefinition.getClassDefined().getName()));
			return select;
		}
		return DefaultFieldFactory.createFieldByPropertyType(type);
	}
}
