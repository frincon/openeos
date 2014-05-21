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
package org.openeos.services.ui.internal;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.ui.WindowContributor;
import org.openeos.services.ui.WindowManagerService;
import org.openeos.services.ui.model.IWindowDefinition;

public class WindowManagerServiceImpl implements WindowManagerService {

	private static final Logger LOG = LoggerFactory.getLogger(WindowManagerServiceImpl.class);

	private HashMap<String, IWindowDefinition> mapWindows = new HashMap<String, IWindowDefinition>();

	@Override
	public IWindowDefinition getWindowDefinition(String id) {
		return mapWindows.get(id);
	}

	public void bindWindowContributor(WindowContributor contributor) {
		LOG.debug("New window contributor of class {}", contributor.getClass().getName());
		List<IWindowDefinition> windowList = contributor.getWindowDefinitionList();
		for (IWindowDefinition window : windowList) {
			if (mapWindows.containsKey(window.getId())) {
				LOG.warn("Window {} already added by this or another contributor, overwrite", window.getId());
			}
			LOG.debug("Store new window: name-> {}", window.getName());
			mapWindows.put(window.getId(), window);
		}
	}

	@Override
	public void registerWindowDefinition(IWindowDefinition windowDefinition) {
		if (mapWindows.containsKey(windowDefinition.getId())) {
			LOG.warn("Window {} already added by this or another contributor, overwrite", windowDefinition.getId());
		} else {
			LOG.debug("Store new window: name-> {}", windowDefinition.getName());
			mapWindows.put(windowDefinition.getId(), windowDefinition);
		}
	}

	@Override
	public void unregisterWindowDefinition(IWindowDefinition windowDefinition) {
		if (mapWindows.remove(windowDefinition.getId()) == null) {
			LOG.warn("Window definition with id " + windowDefinition.getId() + " and name " + windowDefinition.getName()
					+ " has been unregistered without previous registry");
		}
		;
	}
}
