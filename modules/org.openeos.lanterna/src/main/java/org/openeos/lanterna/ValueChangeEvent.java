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
package org.openeos.lanterna;

import java.util.EventObject;

public class ValueChangeEvent extends EventObject {

	private static final long serialVersionUID = -8034735598764044375L;
	private Field source;
	private Object oldValue;
	private Object newValue;

	public ValueChangeEvent(Field source, Object oldValue, Object newValue) {
		super(source);
		this.source = source;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Field getSource() {
		return source;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

}
