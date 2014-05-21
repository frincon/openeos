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

import java.util.SortedSet;

import org.abstractform.core.selector.SelectorProvider;
import org.abstractform.core.selector.SelectorProviderListener;

import org.openeos.dao.ListType;
import org.openeos.dao.ListTypeService;

public class ListTypeSelectorProvider<T extends ListType> implements SelectorProvider<T> {

	private ListTypeService listTypeService;
	private Class<T> elementClass;

	public ListTypeSelectorProvider(ListTypeService listTypeService, Class<T> elementClass) {
		this.listTypeService = listTypeService;
		this.elementClass = elementClass;
	}

	@Override
	public SortedSet<T> getElements() {
		return listTypeService.getAllElements(elementClass);
	}

	@Override
	public String getText(T element) {
		return element.getDescription();
	}

	@Override
	public void addSelectorProviderListener(SelectorProviderListener listener) {
		// Nothing to do, the elements not change any time
	}

	@Override
	public void removeSelectorProviderListener(SelectorProviderListener listener) {
		// Nothing to do, the elements not change any time
	}

}
