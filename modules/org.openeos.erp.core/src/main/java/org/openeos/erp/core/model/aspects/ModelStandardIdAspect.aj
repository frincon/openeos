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
package org.openeos.erp.core.model.aspects;

import org.openeos.dao.IStandardId;
import org.openeos.erp.core.model.Client;

public aspect ModelStandardIdAspect {

	declare parents: org.openeos.erp.core.model.* implements IStandardId;

	public boolean IStandardId.equals(Object obj) {
		// TODO This is not good for hibernate, must be natural keys comparision 
		if (obj instanceof IStandardId) {
			IStandardId idStandard = (IStandardId) obj;
			if (idStandard.getId() == null && this.getId() == null) {
				return true;
			} else if (idStandard.getId() != null) {
				return idStandard.getId().equals(this.getId());
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

}
