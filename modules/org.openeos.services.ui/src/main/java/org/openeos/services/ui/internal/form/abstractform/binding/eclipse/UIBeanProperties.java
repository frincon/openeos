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
package org.openeos.services.ui.internal.form.abstractform.binding.eclipse;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.ICollectionPropertyDefinition;

public class UIBeanProperties {

	private UIBeanProperties() {
	}

	public static IValueProperty value(Class beanClass, String propertyName, IDictionaryService dictionaryService) {
		String[] propertyNames = split(propertyName);

		PropertyDescriptor propertyDescriptor;
		IValueProperty property;
		propertyDescriptor = null;
		property = new UIBeanValueProperty(propertyName, dictionaryService.getClassDefinition(beanClass)
				.getPropertyDefinition(propertyName).getPropertyClass());

		return property;
		/*
		 * IBeanValueProperty beanProperty = new
		 * BeanValuePropertyDecorator(property, propertyDescriptor);
		 * 
		 * for (int i = 1; i < propertyNames.length; i++) {
		 * beanProperty = beanProperty.value(propertyNames[i]);
		 * }
		 * return beanProperty;
		 */
	}

	public static ISetProperty set(Class<?> beanClass, String propertyName, IDictionaryService dictionaryService) {
		ICollectionPropertyDefinition colDef = dictionaryService.getClassDefinition(beanClass).getCollectionPropertyDefinition(
				propertyName);
		return new UIBeanSetProperty(propertyName, colDef.getElementClass(), colDef.getParentPropertyName());
	}

	private static String[] split(String propertyName) {
		if (propertyName.indexOf('.') == -1)
			return new String[] { propertyName };
		List propertyNames = new ArrayList();
		int index;
		while ((index = propertyName.indexOf('.')) != -1) {
			propertyNames.add(propertyName.substring(0, index));
			propertyName = propertyName.substring(index + 1);
		}
		propertyNames.add(propertyName);
		return (String[]) propertyNames.toArray(new String[propertyNames.size()]);
	}

}
