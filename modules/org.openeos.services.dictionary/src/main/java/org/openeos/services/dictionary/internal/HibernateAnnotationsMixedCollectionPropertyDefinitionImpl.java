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
import java.lang.reflect.Method;

import javax.persistence.OneToMany;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.CollectionType;

import org.openeos.services.dictionary.model.ICollectionPropertyDefinition;

public class HibernateAnnotationsMixedCollectionPropertyDefinitionImpl extends HibernateAnnotationsMixedPropertyDefinitionImpl
		implements ICollectionPropertyDefinition {

	private CollectionMetadata collectionMetaData;

	public HibernateAnnotationsMixedCollectionPropertyDefinitionImpl(ClassMetadata metaData, String name,
			CollectionMetadata collectionMetaData) {
		super(metaData, name);
		this.collectionMetaData = collectionMetaData;
	}

	@Override
	public Class<?> getElementClass() {
		return collectionMetaData.getElementType().getReturnedClass();
	}

	@Override
	public String getParentPropertyName() {
		try {
			Method method = new PropertyDescriptor(getName(), getMetaData().getMappedClass()).getReadMethod();
			OneToMany oneToMany = method.getAnnotation(OneToMany.class);
			return oneToMany.mappedBy();
		} catch (IntrospectionException e) {
			return null;
		}
	}

}
