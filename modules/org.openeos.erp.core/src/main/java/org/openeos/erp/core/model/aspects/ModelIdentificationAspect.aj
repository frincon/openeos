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
package org.openeos.erp.core.model.aspects;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.persistence.Transient;
import javax.persistence.Entity;

import org.openeos.dao.IStandardId;
import org.openeos.erp.core.INameValue;
import org.openeos.services.dictionary.IIdentificationCapable;

public aspect ModelIdentificationAspect {

	declare parents : (@Entity * && !IIdentificationCapable) implements IIdentificationCapable;

	@Transient
	public String IIdentificationCapable.getIdentifier() {
		if (this instanceof INameValue) {
			INameValue nm = (INameValue) this;
			return nm.getValue() + " - " + nm.getName();
		} else {
			String standardName = getStandardName(this);
			if (standardName != null) {
				return standardName;
			} else if (this instanceof IStandardId) {
				IStandardId id = (IStandardId) this;
				return this.getClass().getCanonicalName() + "[" + id.getId() + "]";
			} else {
				return this.toString();
			}
		}
	}

	private static String getStandardName(Object object) {
		try {
			PropertyDescriptor descriptor = new PropertyDescriptor("name", object.getClass());
			String result = (String) descriptor.getReadMethod().invoke(object);
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
