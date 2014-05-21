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
package org.openeos.services.dictionary.model;

import org.openeos.services.dictionary.validation.Validation;

public interface IPropertyDefinition {

	/**
	 * Gets the class returned by this property definition
	 * 
	 * @return The class returned by this property definition
	 */
	public Class<?> getPropertyClass();

	public String getName();

	public Object get(Object persistentObject);

	public void set(Object persistentObject, Object value);

	public Validation getValidation();

	public boolean isRequired();

	/**
	 * Gets the default value defined for this property
	 * 
	 * @return The default value defined for this property
	 */
	public String getDefaultValue();

}
