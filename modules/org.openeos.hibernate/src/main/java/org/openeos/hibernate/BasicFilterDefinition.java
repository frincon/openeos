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
package org.openeos.hibernate;

import java.util.Map;

public class BasicFilterDefinition {

	private String name;
	private String defaultCondition;
	private Map<String, String> parameterTypeMap;

	public BasicFilterDefinition() {
		// No-op
	}

	public BasicFilterDefinition(String name, String defaultCondition, Map<String, String> parameterTypeMap) {
		this.name = name;
		this.defaultCondition = defaultCondition;
		this.parameterTypeMap = parameterTypeMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultCondition() {
		return defaultCondition;
	}

	public void setDefaultCondition(String defaultCondition) {
		this.defaultCondition = defaultCondition;
	}

	public Map<String, String> getParameterTypeMap() {
		return parameterTypeMap;
	}

	public void setParameterTypeMap(Map<String, String> parameterTypeMap) {
		this.parameterTypeMap = parameterTypeMap;
	}

}
