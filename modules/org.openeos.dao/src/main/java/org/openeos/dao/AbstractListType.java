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
package org.openeos.dao;

public abstract class AbstractListType implements ListType {

	private static final long serialVersionUID = 1L;

	private String value;
	private String description;

	public AbstractListType(String value) {
		this(value, null);
	}

	public AbstractListType(String value, String description) {
		if (value == null) {
			throw new IllegalArgumentException("The value can not be null");
		}
		this.value = value;
		this.description = description;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass().equals(this.getClass())) {
			return value.equals(((AbstractListType) obj).value);
		}
		return false;
	}

	@Override
	public String toString() {
		return getDescription();
	}

}
