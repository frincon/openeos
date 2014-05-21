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

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractEventSupport<T, E> {

	private CopyOnWriteArrayList<T> listenerList = new CopyOnWriteArrayList<T>();

	public void addListener(T listener) {
		listenerList.add(listener);
	}

	public void removeListener(T listener) {
		listenerList.remove(listener);
	}

	public void fireEvent(E event) {
		for (T listener : listenerList) {
			fireEvent(event, listener);
		}
	}

	protected abstract void fireEvent(E event, T listener);

}
