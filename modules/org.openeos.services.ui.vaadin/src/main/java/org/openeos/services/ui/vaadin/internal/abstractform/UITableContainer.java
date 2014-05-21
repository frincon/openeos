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
package org.openeos.services.ui.vaadin.internal.abstractform;

import java.util.Collection;
import java.util.List;

import org.abstractform.binding.BField;
import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.vaadin.TableContainer;

public class UITableContainer extends TableContainer {

	private static final long serialVersionUID = -6469658317746309917L;

	private BFormInstance<?> formInstance;

	public UITableContainer(BFormInstance<?> formInstance, List<BField> fields, Collection<Object> values) {
		super(fields, values);
		this.formInstance = formInstance;
	}

	public BFormInstance<?> getFormInstance() {
		return formInstance;
	}

}
