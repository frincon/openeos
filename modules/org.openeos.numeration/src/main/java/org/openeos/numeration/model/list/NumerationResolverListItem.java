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
package org.openeos.numeration.model.list;

import org.openeos.dao.AbstractListType;
import org.openeos.dao.ListTypeAdditions;
import org.openeos.numeration.internal.DefaultNumerationResolver;

public class NumerationResolverListItem extends AbstractListType implements ListTypeAdditions {

	private static final long serialVersionUID = 1075889991834330566L;

	public static final NumerationResolverListItem NUMERATION_RESOLVER_DEFAULT = new NumerationResolverListItem(
			DefaultNumerationResolver.class.getName(), "Default resolver");

	public NumerationResolverListItem(String value, String description) {
		super(value, description);
	}

	public NumerationResolverListItem(String value) {
		super(value);
	}

}
