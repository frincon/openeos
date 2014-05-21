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

import org.openeos.services.dictionary.EntityToStringResolver;
import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.IClassDefinition;

public class ClassAndIdStringResolver implements EntityToStringResolver {

	private IDictionaryService dictionaryService;

	public ClassAndIdStringResolver(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public String tryResolveString(Object entity) {
		IClassDefinition classDef = dictionaryService.getClassDefinition(entity.getClass());
		if (classDef != null) {
			return classDef.getSingularEntityName() + " [" + classDef.getId(entity).toString() + "]";
		}
		return null;
	}
}
