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
package org.openeos.erp.acct.model.list;

import org.openeos.dao.AbstractListType;
import org.openeos.dao.ListTypeAdditions;

public class PeriodType extends AbstractListType implements ListTypeAdditions {

	private static final long serialVersionUID = 1L;

	public static final PeriodType STANDARD = new PeriodType("STANDARD", "Standard Calendar Period");
	public static final PeriodType ADJUSTEMENT = new PeriodType("ADJUSTEMENT", "Adjustement Period");

	public PeriodType(String value) {
		super(value);
	}

	public PeriodType(String value, String description) {
		super(value, description);
	}

}
