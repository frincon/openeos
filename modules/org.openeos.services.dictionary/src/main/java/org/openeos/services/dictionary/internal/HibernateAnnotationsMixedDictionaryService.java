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

import java.util.HashMap;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.dictionary.EntityToStringService;
import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.dictionary.model.IPropertyDefinition;

public class HibernateAnnotationsMixedDictionaryService implements IDictionaryService {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(HibernateAnnotationsMixedDictionaryService.class);

	// TODO Make better cache implementation (with library)

	private HashMap<String, IClassDefinition> classDefinitions = new HashMap<String, IClassDefinition>();

	private SessionFactory sessionFactory;
	private EntityToStringService entityToStringService;

	public HibernateAnnotationsMixedDictionaryService(SessionFactory sessionFactory, EntityToStringService entityToStringService) {
		this.sessionFactory = sessionFactory;
		this.entityToStringService = entityToStringService;
	}

	@Override
	public IClassDefinition getClassDefinition(String className) {
		if (!classDefinitions.containsKey(className))
			loadClassDefinition(className);
		return classDefinitions.get(className);
	}

	private void loadClassDefinition(String className) {
		if (classDefinitions.containsKey(className))
			return; //For the flys

		// PersistentClass persistent = configuration.getClassMapping(className);
		ClassMetadata metadata = sessionFactory.getClassMetadata(className);
		if (metadata == null) {
			return;
		}
		HibernateAnnotationsMixedClassDefinitionImpl classDefImpl = new HibernateAnnotationsMixedClassDefinitionImpl(
				entityToStringService, sessionFactory, metadata.getMappedClass());

		String[] propertyNames = metadata.getPropertyNames();

		classDefImpl.setIdPropertyDefinition(createPropertyDefinition(metadata.getIdentifierPropertyName(),
				metadata.getIdentifierType(), metadata));
		for (String propertyName : propertyNames) {
			IPropertyDefinition propertyDef = createPropertyDefinition(propertyName, metadata.getPropertyType(propertyName),
					metadata);
			classDefImpl.addPropertyDefinition(propertyDef);
		}
		classDefinitions.put(className, classDefImpl);

	}

	private IPropertyDefinition createPropertyDefinition(String propertyName, Type propertyType, ClassMetadata metaData) {
		IPropertyDefinition propertyDef;
		if (propertyType.isCollectionType()) {
			CollectionType colType = (CollectionType) propertyType;
			CollectionMetadata colMetaData = sessionFactory.getCollectionMetadata(colType.getRole());
			propertyDef = new HibernateAnnotationsMixedCollectionPropertyDefinitionImpl(metaData, propertyName, colMetaData);
		} else {
			propertyDef = new HibernateAnnotationsMixedPropertyDefinitionImpl(metaData, propertyName);
		}
		return propertyDef;
	}

	@Override
	public IClassDefinition getClassDefinition(Class<?> persistentClass) {
		return getClassDefinition(persistentClass.getName());
	}

}
