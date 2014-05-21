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
package org.openeos.extender.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;

import org.openeos.extender.ui.Constants;
import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.WindowManagerService;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.window.DictionaryBasedWindowBuilder;
import org.openeos.services.ui.window.DictionaryBasedWindowDefinition;
import org.openeos.utils.ClassListExtenderHandler;

public class WindowExtenderHandler extends ClassListExtenderHandler<DictionaryBasedWindowDefinition> {

	private IDictionaryService dictionaryService;
	private WindowManagerService windowManagerService;
	private DictionaryBasedWindowBuilder dictionaryBasedWindowBuilder;

	private Map<Long, List<IWindowDefinition>> registeredDefinitionsMap = new HashMap<Long, List<IWindowDefinition>>();

	public void setDictionaryService(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setWindowManagerService(WindowManagerService windowManagerService) {
		this.windowManagerService = windowManagerService;
	}

	public void setDictionaryBasedWindowBuilder(DictionaryBasedWindowBuilder dictionaryBasedWindowBuilder) {
		this.dictionaryBasedWindowBuilder = dictionaryBasedWindowBuilder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openeos.extender.window.internal.ExtenderHandler#starting()
	 */
	@Override
	public void starting() {
		if (this.windowManagerService == null || this.dictionaryService == null || this.dictionaryBasedWindowBuilder == null) {
			throw new UnsupportedOperationException("There are any services than its not initialized yet.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openeos.extender.window.internal.ExtenderHandler#stopping()
	 */
	@Override
	public void stopping() {
		for (List<IWindowDefinition> definitionList : registeredDefinitionsMap.values()) {
			for (IWindowDefinition definition : definitionList) {
				windowManagerService.unregisterWindowDefinition(definition);
			}
		}
	}

	private void registerDefinition(Bundle bundle, IWindowDefinition definition) {
		windowManagerService.registerWindowDefinition(definition);
		if (!registeredDefinitionsMap.containsKey(bundle.getBundleId())) {
			registeredDefinitionsMap.put(bundle.getBundleId(), new ArrayList<IWindowDefinition>());
		}
		registeredDefinitionsMap.get(bundle.getBundleId()).add(definition);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openeos.extender.window.internal.ExtenderHandler#onBundleDeparture
	 * (org.osgi.framework.Bundle)
	 */
	@Override
	public void onBundleDeparture(Bundle bundle) {
		if (registeredDefinitionsMap.containsKey(bundle.getBundleId())) {
			for (IWindowDefinition definition : registeredDefinitionsMap.get(bundle.getBundleId())) {
				windowManagerService.unregisterWindowDefinition(definition);
			}
			registeredDefinitionsMap.remove(bundle.getBundleId());
		}
	}

	@Override
	public String getHeaderName() {
		return Constants.HEADER_WINDOW_CLASSES;
	}

	@Override
	protected void processObject(Bundle bundle, DictionaryBasedWindowDefinition objectCreated) {
		IWindowDefinition definition = dictionaryBasedWindowBuilder.buildWindowDefinition(objectCreated);
		registerDefinition(bundle, definition);

	}

	@Override
	protected Class<DictionaryBasedWindowDefinition> getExpectedClass() {
		return DictionaryBasedWindowDefinition.class;
	}
}
