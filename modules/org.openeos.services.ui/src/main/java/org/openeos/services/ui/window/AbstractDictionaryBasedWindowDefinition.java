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
package org.openeos.services.ui.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openeos.services.ui.window.WindowSortOrder.SortOrder;

public abstract class AbstractDictionaryBasedWindowDefinition implements DictionaryBasedWindowDefinition {

	private String id;
	private String name;
	private String description;
	private String help;
	private Class<?> objectClass;
	private List<String> visibleFields = new ArrayList<String>();
	private List<WindowSortOrder> sortOrderList = new ArrayList<WindowSortOrder>();

	public AbstractDictionaryBasedWindowDefinition(String id, Class<?> objectClass) {
		this.id = id;
		this.objectClass = objectClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openeos.services.ui.window.DictionaryBasedWindowDefinition#getId
	 * ()
	 */
	@Override
	public final String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openeos.services.ui.window.DictionaryBasedWindowDefinition#getName
	 * ()
	 */
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openeos.services.ui.window.DictionaryBasedWindowDefinition#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openeos.services.ui.window.DictionaryBasedWindowDefinition#getHelp
	 * ()
	 */
	@Override
	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openeos.services.ui.window.DictionaryBasedWindowDefinition#
	 * getObjectClass()
	 */
	@Override
	public Class<?> getObjectClass() {
		return objectClass;
	}

	public List<String> getVisibleFieldList() {
		return Collections.unmodifiableList(visibleFields);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openeos.services.ui.window.DictionaryBasedWindowDefinition#
	 * getWindowSortOrder()
	 */
	@Override
	public List<WindowSortOrder> getWindowSortOrderList() {
		return Collections.unmodifiableList(sortOrderList);
	}

	public void addVisibleField(String propertyName) {
		visibleFields.add(propertyName);
	}

	public void removeVisibleField(String propertyName) {
		visibleFields.remove(propertyName);
	}

	public void addOrderAsc(String propertyName) {
		sortOrderList.add(new WindowSortOrder(propertyName, SortOrder.ASC));
	}

	public void addOrderDesc(String propertyName) {
		sortOrderList.add(new WindowSortOrder(propertyName, SortOrder.DESC));
	}
}
