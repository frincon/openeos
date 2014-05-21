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
package org.openeos.dao.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openeos.dao.ListType;
import org.openeos.dao.ListTypeService;
import org.openeos.dao.OrderedListType;

public class ListTypeServiceImpl implements ListTypeService {

	private Map<Class<? extends ListType>, SortedSet<? extends ListType>> mapListTypeSet = new HashMap<Class<? extends ListType>, SortedSet<? extends ListType>>();
	private Map<Class<? extends ListType>, Map<String, ? extends ListType>> mapListTypeMap = new HashMap<Class<? extends ListType>, Map<String, ? extends ListType>>();

	private Comparator<ListType> listTypeComparator = new Comparator<ListType>() {

		@Override
		public int compare(ListType o1, ListType o2) {
			if (o1 instanceof OrderedListType && o2 instanceof OrderedListType) {
				OrderedListType ol1 = (OrderedListType) o1;
				OrderedListType ol2 = (OrderedListType) o2;
				if (Integer.compare(ol1.getOrder(), ol2.getOrder()) == 0) {
					return ol1.getValue().compareTo(ol2.getValue());
				} else {
					return Integer.compare(ol1.getOrder(), ol2.getOrder());
				}
			} else {
				if (o1.getDescription() != null && o2.getDescription() != null) {
					return (o1.getDescription() + o1.getValue()).compareTo(o2.getDescription() + o2.getValue());
				} else if (o1.getDescription() == null) {
					return -1;
				} else if (o2.getDescription() == null) {
					return 1;
				} else {
					return o1.getValue().compareTo(o2.getValue());
				}
			}
		}
	};

	@Override
	public <T extends ListType> SortedSet<T> getAllElements(Class<T> elementClass) {
		return Collections.unmodifiableSortedSet(getSet(elementClass));
	}

	@Override
	public <T extends ListType> T getElement(Class<T> elementClass, String value) {
		return getMap(elementClass).get(value);
	}

	@Override
	public synchronized <T extends ListType> void registerElement(Class<T> elementClass, T element) {
		getSet(elementClass).add(element);
		getMap(elementClass).put(element.getValue(), element);
	}

	@Override
	public <T extends ListType> void unregisterElement(Class<T> elementClass, T element) {
		getSet(elementClass).remove(element);
		getMap(elementClass).remove(element.getValue());
	}

	private <T extends ListType> SortedSet<T> getSet(Class<T> elementClass) {
		SortedSet<T> set = (SortedSet<T>) mapListTypeSet.get(elementClass);
		if (set == null) {
			set = createAndAddSet(elementClass);
		}
		return set;
	}

	private <T extends ListType> SortedSet<T> createAndAddSet(Class<T> elementClass) {
		SortedSet<T> sortedSet = new TreeSet<T>(listTypeComparator);
		mapListTypeSet.put(elementClass, sortedSet);
		return sortedSet;
	}

	private <T extends ListType> Map<String, T> getMap(Class<T> elementClass) {
		Map<String, T> map = (Map<String, T>) mapListTypeMap.get(elementClass);
		if (map == null) {
			map = createAndAddMap(elementClass);
		}
		return map;
	}

	private <T extends ListType> Map<String, T> createAndAddMap(Class<T> elementClass) {
		Map<String, T> map = new HashMap<String, T>();
		mapListTypeMap.put(elementClass, map);
		return map;
	}

}
