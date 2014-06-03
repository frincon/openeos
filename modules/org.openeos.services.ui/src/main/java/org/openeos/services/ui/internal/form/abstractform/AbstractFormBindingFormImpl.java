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
package org.openeos.services.ui.internal.form.abstractform;

import java.util.EnumSet;

import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;
import org.openeos.services.ui.form.abstractform.UIBForm;

public class AbstractFormBindingFormImpl<T> implements AbstractFormBindingForm<T> {

	private UIBForm<T> bForm;
	private Integer ranking;
	private EnumSet<BindingFormCapability> capabilities;

	public AbstractFormBindingFormImpl(UIBForm<T> bForm, Integer ranking, EnumSet<BindingFormCapability> capabilities) {
		this.bForm = bForm;
		this.capabilities = capabilities;
	}

	@Override
	public Class<T> getBeanClass() {
		return bForm.getBeanClass();
	}

	@Override
	public Integer getRanking() {
		return ranking;
	}

	@Override
	public EnumSet<BindingFormCapability> getCapabilities() {
		return capabilities;
	}

	@Override
	public UIBForm<T> getAbstractBForm() {
		return bForm;
	}

}
