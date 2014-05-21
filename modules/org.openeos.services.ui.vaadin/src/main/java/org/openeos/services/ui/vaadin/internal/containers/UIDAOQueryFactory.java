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

import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;

public class UIDAOQueryFactory implements QueryFactory {

	private UIDAOService uidaoService;
	private UIDAOQueryDefinition queryDefinition;
	private IVaadinContainerFactory containerFactory;

	public UIDAOQueryFactory(UIDAOService uidaoService, IVaadinContainerFactory containerFactory) {
		this.uidaoService = uidaoService;
		this.containerFactory = containerFactory;
	}

	@Override
	public void setQueryDefinition(QueryDefinition queryDefinition) {
		this.queryDefinition = (UIDAOQueryDefinition) queryDefinition;
	}

	@Override
	public Query constructQuery(Object[] sortPropertyIds, boolean[] sortStates) {
		queryDefinition.setSortState(sortPropertyIds, sortStates);
		return new UIDAOQuery(uidaoService, queryDefinition, containerFactory);
	}

}
