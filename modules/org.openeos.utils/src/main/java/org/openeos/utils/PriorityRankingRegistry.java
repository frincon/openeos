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
package org.openeos.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class PriorityRankingRegistry<K, T> {

	private Map<K, Queue<Item>> elements = new HashMap<K, Queue<Item>>();

	private class Item implements Comparable<Item> {
		private T element;
		private int ranking;

		Item(T element, int ranking) {
			this.element = element;
			this.ranking = ranking;
		}

		@Override
		public int compareTo(Item o) {
			// Reverse is ok
			return Integer.compare(o.ranking, ranking);
		}

	}

	public synchronized void addElement(K key, int ranking, T element) {
		Queue<Item> queue = getQueue(key);
		queue.add(new Item(element, ranking));
	}

	private Queue<Item> getQueue(K key) {
		Queue<Item> queue = elements.get(key);
		if (queue == null) {
			queue = createQueue();
			elements.put(key, queue);
		}
		return queue;
	}

	private Queue<Item> createQueue() {
		return new PriorityQueue<Item>();
	}

	public synchronized boolean removeElement(K key, T element) {
		Queue<Item> queue = getQueue(key);
		Item itemToRemove = null;
		for (Item item : queue) {
			if (item.element.equals(element)) {
				itemToRemove = item;
				break;
			}
		}
		if (itemToRemove != null) {
			return queue.remove(itemToRemove);
		} else {
			return false;
		}
	}

	public synchronized T getActualElement(K key) {
		Item item = getQueue(key).peek();
		if (item != null) {
			return item.element;
		} else {
			return null;
		}
	}

	public int getActualRanking(K key) {
		Item item = getQueue(key).peek();
		if (item != null) {
			return item.ranking;
		} else {
			return 0;
		}
	}
}
