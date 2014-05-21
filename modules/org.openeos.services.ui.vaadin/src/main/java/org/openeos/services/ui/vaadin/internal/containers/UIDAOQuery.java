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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.addons.lazyquerycontainer.Query;

import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import com.vaadin.data.Item;

public class UIDAOQuery implements Query {

	private transient UIDAOService uidaoService;
	private UIDAOQueryDefinition queryDefinition;
	private IVaadinContainerFactory containerFactory;

	public UIDAOQuery(UIDAOService uidaoService, UIDAOQueryDefinition queryDefinition, IVaadinContainerFactory containerFactory) {
		this.uidaoService = uidaoService;
		this.queryDefinition = queryDefinition;
		this.containerFactory = containerFactory;
	}

	@Override
	public Item constructItem() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean deleteAllItems() {
		return false;
	}

	@Override
	public int size() {
		return uidaoService.count(queryDefinition.getClassDefined());
	}

	@Override
	public List<Item> loadItems(int startIndex, int count) {
		List<UIBean> uibeanList = uidaoService.list(queryDefinition.getClassDefined(), startIndex, count, generateOrders());
		List<Item> itemList = new ArrayList<Item>(uibeanList.size());
		for (UIBean uiBean : uibeanList) {
			itemList.add(generateItem(uiBean));
		}
		return itemList;
	}

	private Order[] generateOrders() {
		Order[] orders = new Order[queryDefinition.getSortPropertyIds().length];
		for (int i = 0; i < queryDefinition.getSortPropertyIds().length; i++) {
			if (queryDefinition.getSortStates()[i]) {
				orders[i] = Order.asc((String) queryDefinition.getSortPropertyIds()[i]);
			} else {
				orders[i] = Order.desc((String) queryDefinition.getSortPropertyIds()[i]);
			}
		}
		return orders;
	}

	@Override
	@Transactional(readOnly = false)
	public void saveItems(List<Item> addedItems, List<Item> modifiedItems, List<Item> removedItems) {
		throw new UnsupportedOperationException();
	}

	private Item generateItem(UIBean bean) {
		return containerFactory.createItem(bean);
	}

}
