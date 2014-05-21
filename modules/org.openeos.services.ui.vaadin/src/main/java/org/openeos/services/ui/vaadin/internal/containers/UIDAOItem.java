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
package org.openeos.services.ui.vaadin.internal.containers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openeos.services.ui.UIBean;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class UIDAOItem implements Item {

	private static final long serialVersionUID = 1455059670238764648L;

	private UIBean uiBean;

	private Map<String, Property> mapProperties = new HashMap<String, Property>();

	public UIDAOItem(UIBean uiBean) {
		this.uiBean = uiBean;
	}

	public UIBean getUiBean() {
		return uiBean;
	}

	@Override
	public Property getItemProperty(Object id) {
		if (id instanceof String) {
			String idString = (String) id;
			if (!uiBean.getPropertyNames().contains(idString)) {
				return null;
			}
			if (!mapProperties.containsKey(idString)) {
				mapProperties.put(idString, createProperty(idString));
			}
			return mapProperties.get(idString);
		} else {
			throw new IllegalArgumentException("The id of a property must be a String");
		}
	}

	private Property createProperty(String idString) {
		return new UIDAOProperty(uiBean, idString);
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		return uiBean.getPropertyNames();
	}

	@Override
	public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
