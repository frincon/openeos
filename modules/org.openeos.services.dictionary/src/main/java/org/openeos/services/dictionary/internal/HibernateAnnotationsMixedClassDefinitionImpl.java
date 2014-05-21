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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.dictionary.EntityToStringService;
import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.dictionary.model.ICollectionPropertyDefinition;
import org.openeos.services.dictionary.model.IPropertyDefinition;

public class HibernateAnnotationsMixedClassDefinitionImpl implements IClassDefinition {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(HibernateAnnotationsMixedDictionaryService.class);

	private ClassMetadata classMetadata;
	private Class<?> classDefined;
	private HashMap<String, IPropertyDefinition> propertyDefinitionList = new HashMap<String, IPropertyDefinition>();
	private IPropertyDefinition idPropertyDefinition;
	private SessionFactory sessionFactory;
	private EntityToStringService entityToStringService;

	public HibernateAnnotationsMixedClassDefinitionImpl(EntityToStringService entityToStringService, SessionFactory sessionFactory,
			Class<?> classDefined) {
		this.entityToStringService = entityToStringService;
		this.sessionFactory = sessionFactory;
		this.classDefined = classDefined;
		this.classMetadata = sessionFactory.getClassMetadata(classDefined);
	}

	@Override
	public IPropertyDefinition getPropertyDefinition(String fieldName) {
		return propertyDefinitionList.get(fieldName);
	}

	@Override
	public ICollectionPropertyDefinition getCollectionPropertyDefinition(String fieldName) {
		return (ICollectionPropertyDefinition) this.propertyDefinitionList.get(fieldName);
	}

	@Override
	public Collection<IPropertyDefinition> getAllPropertyDefinitionList() {
		return propertyDefinitionList.values();
	}

	@Override
	public Class<?> getClassDefined() {
		return classDefined;
	}

	@Override
	public IPropertyDefinition getIdPropertyDefinition() {
		return idPropertyDefinition;
	}

	public void setIdPropertyDefinition(IPropertyDefinition idPropertyDefinition) {
		this.idPropertyDefinition = idPropertyDefinition;
	}

	@Override
	public Object getId(Object persistentObject) {
		if (classDefined.isInstance(persistentObject)) {
			return classMetadata.getIdentifier(persistentObject, null);
		} else {
			throw new IllegalArgumentException(MessageFormat.format("The persistentObject is not instance of {0}",
					classDefined.getName()));
		}
	}

	public void addPropertyDefinition(IPropertyDefinition propertyDef) {
		this.propertyDefinitionList.put(propertyDef.getName(), propertyDef);
	}

	@Override
	public String getSingularEntityName() {
		// TODO
		return classDefined.getSimpleName();
	}

	@Override
	public String getStringRepresentation(Object entity) {
		return entityToStringService.resolveStringRepresentation(entity);
	}

}