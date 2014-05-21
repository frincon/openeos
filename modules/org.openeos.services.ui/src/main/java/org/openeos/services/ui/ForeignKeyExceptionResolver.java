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
package org.openeos.services.ui;

import java.util.Map;

public class ForeignKeyExceptionResolver extends ConstraintViolationExceptionResolver {

	public static final Integer RANKING = 15000;

	private Map<String, String> foreignKeyToEntitiesMap;

	public void setForeignKeyToEntitiesMap(Map<String, String> foreignKeyToEntitiesMap) {
		this.foreignKeyToEntitiesMap = foreignKeyToEntitiesMap;
	}

	@Override
	protected String resolveMessageForConstraintName(String constraintName) {
		if (foreignKeyToEntitiesMap != null) {
			String tables = foreignKeyToEntitiesMap.get(constraintName);
			if (tables == null) {
				tables = foreignKeyToEntitiesMap.get(constraintName.toUpperCase());
			}
			if (tables == null) {
				tables = foreignKeyToEntitiesMap.get(constraintName.toLowerCase());
			}
			if (tables != null) {
				String[] table = tables.split(",");
				if (table.length == 2) {
					return "The action break the relationship beetwen " + table[0] + " and " + table[1];
				}
			}
		}
		return null;
	}

}
