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
package org.openeos.services.ui.internal.form.abstractform;

import org.abstractform.binding.BField;
import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFFieldFactory;
import org.abstractform.core.FormInstance;
import org.abstractform.core.selector.SelectorConstants;
import org.abstractform.core.selector.SelectorProvider;
import org.abstractform.core.selector.SelectorProviderFactory;
import org.hibernate.SessionFactory;

import org.openeos.dao.ListType;
import org.openeos.dao.ListTypeService;
import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.dictionary.model.IPropertyDefinition;
import org.openeos.services.dictionary.validation.SQLValidation;
import org.openeos.services.dictionary.validation.Validation;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.form.abstractform.BFUIButton;
import org.openeos.services.ui.form.abstractform.BFUITable;
import org.openeos.services.ui.form.abstractform.UIBeanSelectorProvider;

public class UIBeanBFFieldFactory implements BFFieldFactory {

	private IDictionaryService dictionaryService;
	private BFFieldFactory delegateFieldFactory;
	private SessionFactory sessionFactory;
	private UIDAOService uidaoService;
	private ListTypeService listTypeService;

	public void setDictionaryService(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setDelegateFieldFactory(BFFieldFactory delegateFieldFactory) {
		this.delegateFieldFactory = delegateFieldFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setUidaoService(UIDAOService uidaoService) {
		this.uidaoService = uidaoService;
	}

	public void setListTypeService(ListTypeService listTypeService) {
		this.listTypeService = listTypeService;
	}

	@Override
	public BFField buildBFField(String id, String name, Class<?> beanClass, String propertyName) {
		BFField field = delegateFieldFactory.buildBFField(id, name, beanClass, propertyName);
		IClassDefinition classDef = dictionaryService.getClassDefinition(beanClass);
		if (classDef != null) {
			IPropertyDefinition propertyDef = classDef.getPropertyDefinition(propertyName);
			field.setRequired(propertyDef.isRequired());
		}
		checkField(beanClass, field);
		return field;
	}

	@Override
	public <T extends BFField> T buildBFField(String id, String name, Class<?> beanClass, String propertyName, Class<T> fieldClass) {
		if (fieldClass.equals(BFUITable.class)) {
			return (T) new BFUITable(id, name, beanClass, propertyName, uidaoService);
		} else if (fieldClass.equals(BFUIButton.class)) {
			return (T) new BFUIButton(id, name, beanClass, propertyName);
		}
		T field = delegateFieldFactory.buildBFField(id, name, beanClass, propertyName, fieldClass);
		checkField(beanClass, field);
		return field;
	}

	private void checkField(Class<?> beanClass, BFField field) {
		if (field.getType() == null) {
			final Class<?> propertyClass = field.getPropertyDescriptor().getPropertyType();
			if (dictionaryService.getClassDefinition(propertyClass) != null) {
				field.setType(SelectorConstants.TYPE_SELECTOR);
				if (field.getExtra(SelectorConstants.EXTRA_SELECTOR_PROVIDER_FACTORY) == null) {
					final String sqlValidation = checkForSqlValidation(beanClass, field);
					SelectorProviderFactory factory = new SelectorProviderFactory() {

						@Override
						public SelectorProvider<?> createSelectorProvider(FormInstance formInstance) {
							return new UIBeanSelectorProvider(sessionFactory, propertyClass, formInstance, sqlValidation);
						}
					};
					field.setExtra(SelectorConstants.EXTRA_SELECTOR_PROVIDER_FACTORY, factory);
				}
			} else if (ListType.class.isAssignableFrom(propertyClass)) {
				field.setType(SelectorConstants.TYPE_SELECTOR);
				if (field.getExtra(SelectorConstants.EXTRA_SELECTOR_PROVIDER_FACTORY) == null) {
					SelectorProviderFactory factory = new SelectorProviderFactory() {

						@Override
						public SelectorProvider<?> createSelectorProvider(FormInstance formInstance) {
							return new ListTypeSelectorProvider<ListType>(listTypeService, (Class<ListType>) propertyClass);
						}
					};
					field.setExtra(SelectorConstants.EXTRA_SELECTOR_PROVIDER_FACTORY, factory);
				}
			}
		}
	}

	private String checkForSqlValidation(Class<?> beanClass, BField field) {
		IClassDefinition classDefinition = dictionaryService.getClassDefinition(beanClass);
		String sqlValidation = null;
		if (classDefinition != null) {
			IPropertyDefinition propertyDefinition = classDefinition.getPropertyDefinition(field.getPropertyName());
			Validation validation = propertyDefinition.getValidation();
			if (validation != null) {
				if (SQLValidation.TYPE.equals(validation.getValidationType())) {
					sqlValidation = ((SQLValidation) validation).getSqlText();
				}
			}
		}
		return sqlValidation;
	}

}
