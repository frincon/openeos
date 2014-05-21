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
package org.openeos.services.ui.internal;

import java.util.AbstractList;
import java.util.List;

import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.ui.UIBean;

public class UIBeanWrapperList extends AbstractList<UIBean> {

	private List<Object> sourceList;
	private IClassDefinition classDefinition;
	private UIBean[] uiBeanCache;

	public UIBeanWrapperList(List<Object> sourceList, IClassDefinition classDefinition) {
		this.sourceList = sourceList;
		this.classDefinition = classDefinition;
		uiBeanCache = new UIBean[sourceList.size()];
	}

	@Override
	public int size() {
		return sourceList.size();
	}

	@Override
	public UIBean get(int index) {
		if (uiBeanCache[index] == null) {
			uiBeanCache[index] = wrapBean(sourceList.get(index));
		}
		return uiBeanCache[index];
	}

	private UIBean wrapBean(Object object) {
		UIBeanImpl uiBean = new UIBeanImpl(object, classDefinition);
		return uiBean;
	}
}
