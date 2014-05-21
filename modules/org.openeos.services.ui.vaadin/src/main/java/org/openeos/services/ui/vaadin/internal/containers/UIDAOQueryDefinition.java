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

import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;

public class UIDAOQueryDefinition extends LazyQueryDefinition {

	private static final long serialVersionUID = 7897988611677283393L;

	private Class<?> classDefined;
	private Object[] sortPropertyIds;
	private boolean[] sortStates;

	public UIDAOQueryDefinition(int batchSize, Class<?> classDefined) {
		super(false, batchSize);
		this.classDefined = classDefined;
	}

	public Class<?> getClassDefined() {
		return classDefined;
	}

	public Object[] getSortPropertyIds() {
		return sortPropertyIds;
	}

	public boolean[] getSortStates() {
		return sortStates;
	}
	
    public final void setSortState(final Object[] sortPropertyIds, final boolean[] sortPropertyAscendingStates) {
        this.sortPropertyIds = sortPropertyIds;
        this.sortStates = sortPropertyAscendingStates;
    }
	
	

	
}
