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

import org.openeos.numeration.model.Sequence;
import org.openeos.services.ui.window.AbstractDictionaryBasedWindowDefinition;

public class NumerationWindows {

	public static class SequenceWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = SequenceWindow.class.getName();

		public SequenceWindow() {
			super(ID, Sequence.class);
			setName("Sequence");
			addVisibleField(Sequence.PROPERTY_VALUE);
			addVisibleField(Sequence.PROPERTY_NAME);
			addVisibleField(Sequence.PROPERTY_DESCRIPTION);
			addVisibleField(Sequence.PROPERTY_NUMERATION_RESOLVER_ID);
			addVisibleField(Sequence.PROPERTY_PATTERN);
			addOrderAsc(Sequence.PROPERTY_VALUE);
		}

	}

}
