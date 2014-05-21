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
package org.openeos.services.dictionary.internal;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.dictionary.DefaultValue;
import org.openeos.services.dictionary.model.IPropertyDefinition;
import org.openeos.services.dictionary.validation.Validation;
import org.openeos.services.dictionary.validation.annotations.SQLValidation;

class HibernateAnnotationsMixedPropertyDefinitionImpl implements IPropertyDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(HibernateAnnotationsMixedPropertyDefinitionImpl.class);

	private ClassMetadata metaData;
	private Class<?> propertyClass;
	private String name;
	private Validation validation;
	private String defaultValue;
	private Boolean required = null;

	public HibernateAnnotationsMixedPropertyDefinitionImpl(ClassMetadata metaData, String name) {
		this.metaData = metaData;
		this.name = name;
		this.propertyClass = metaData.getPropertyType(name).getReturnedClass();
		processValidation();
		processDefaultValue();
	}

	private void processDefaultValue() {
		try {
			PropertyDescriptor descriptor = new PropertyDescriptor(name, metaData.getMappedClass());
			for (Annotation annotation : descriptor.getReadMethod().getAnnotations()) {
				if (annotation.annotationType().equals(DefaultValue.class)) {
					DefaultValue defaultValue = (DefaultValue) annotation;
					this.defaultValue = defaultValue.value();
				}
			}
		} catch (IntrospectionException ex) {
			LOG.error("Exception was thrown while get annotations for validation field", ex);
		}
	}

	private void processValidation() {
		try {
			PropertyDescriptor descriptor = new PropertyDescriptor(name, metaData.getMappedClass());
			for (Annotation annotation : descriptor.getReadMethod().getAnnotations()) {
				if (annotation.annotationType().equals(SQLValidation.class)) {
					SQLValidation sqlValidation = (SQLValidation) annotation;
					validation = new org.openeos.services.dictionary.validation.SQLValidation(sqlValidation.value());
				}
			}
		} catch (IntrospectionException ex) {
			LOG.error("Exception was thrown while get annotations for validation field", ex);
		}
	}

	@Override
	public Class<?> getPropertyClass() {
		return propertyClass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object get(Object persistentObject) {
		if (metaData.getIdentifierPropertyName().equals(getName())) {
			return metaData.getIdentifier(persistentObject, null);
		} else {
			return metaData.getPropertyValue(persistentObject, getName());
		}
	}

	@Override
	public void set(Object persistentObject, Object value) {
		if (metaData.getIdentifierPropertyName().equals(getName())) {
			metaData.setIdentifier(persistentObject, (Serializable) value, null);
		} else {
			metaData.setPropertyValue(persistentObject, getName(), value);
		}
	}

	protected ClassMetadata getMetaData() {
		return metaData;
	}

	@Override
	public Validation getValidation() {
		return validation;
	}

	@Override
	public boolean isRequired() {
		if (required == null) {
			resolveRequired();
		}
		return required;
	}

	private void resolveRequired() {
		String[] propertyNames = metaData.getPropertyNames();
		boolean[] propertyNullability = metaData.getPropertyNullability();
		for (int i = 0; i < propertyNames.length; i++) {
			if (propertyNames[i].equals(name)) {
				required = !propertyNullability[i];
				return;
			}
		}
		LOG.warn("Property '{}' not found in hibernate metadata", name);
		required = false;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

}