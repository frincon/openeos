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
package org.openeos.numeration.ui;

import java.util.EnumSet;

import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFSubForm;
import org.openeos.numeration.model.Sequence;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.abstractform.UIAbstractForm;

public class SequenceForm extends UIAbstractForm<Sequence> {

	public static final String ID = SequenceForm.class.getName();
	public static final String NAME = "Sequence Form";

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 2);
	public BFField FIELD_SEARCH_KEY = SUBFORM_MAIN.addField(0, 0, null, "Search Key", Sequence.PROPERTY_VALUE);
	public BFField FIELD_NAME = SUBFORM_MAIN.addField(0, 1, null, "Name", Sequence.PROPERTY_NAME);
	public BFSubForm SUBFORM_DESCRIPTION = addSubForm(null, 1);
	public BFField FIELD_DESCRIPTION = SUBFORM_DESCRIPTION.addField(0, 0, null, "Description", Sequence.PROPERTY_DESCRIPTION);
	public BFSubForm SUBFORM_DETAIL = addSubForm(null, 2);
	public BFField FIELD_NUMERATION_RESOLVER = SUBFORM_DETAIL.addField(0, 0, null, "Numeration Resolver",
			Sequence.PROPERTY_NUMERATION_RESOLVER_ID);
	public BFField FIELD_PATTERN = SUBFORM_DETAIL.addField(0, 1, null, "Pattern", Sequence.PROPERTY_PATTERN);

	public SequenceForm() {
		super(ID, NAME, Sequence.class);
	}

	@Override
	public EnumSet<BindingFormCapability> getCapabilities() {
		return EnumSet.of(BindingFormCapability.EDIT, BindingFormCapability.NEW);
	}

}
