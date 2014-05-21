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
package org.openeos.services.ui.vaadin.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.model.ITab;
import org.openeos.services.ui.model.ITabDefinition;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalSplitPanel;

public class VaadinWindowImpl extends AbstractVaadinWindowImpl {

	private static final Logger logger = LoggerFactory.getLogger(VaadinWindowImpl.class);

	private FormFieldFactory formFieldFactory;

	private VaadinTabImpl vaadinRootTab;
	private Panel firstContainer;
	private Panel secondContainer;
	private boolean multipleLevels = false;
	private Map<String, VaadinTabImpl> tabMap = new HashMap<String, VaadinTabImpl>();

	public VaadinWindowImpl(IWindowDefinition windowDefinition, IVaadinContainerFactory containerFactory,
			IDictionaryService dictionaryService, UIApplication<IUnoVaadinApplication> application,
			FormFieldFactory formFieldFactory) {
		super(windowDefinition, containerFactory, dictionaryService, application);
		this.formFieldFactory = formFieldFactory;
		this.multipleLevels = windowDefinition.getRootTab().getChildren().size() > 0;

		vaadinRootTab = createTabs(windowDefinition.getRootTab());

		firstContainer.setContent(vaadinRootTab.getMainContainer());
		if (multipleLevels) {
			secondContainer.setContent(tabMap.get(windowDefinition.getRootTab().getChildren().get(0).getId()).getMainContainer());
		}
	}

	protected VaadinTabImpl createTabs(ITabDefinition tabDefinition) {
		VaadinTabImpl tab = new VaadinTabImpl(tabDefinition, getContainerFactory(), getApplication(), formFieldFactory);
		List<ITabDefinition> children = tabDefinition.getChildren();
		for (ITabDefinition tabDef : children) {
			tabMap.put(tabDef.getId(), createTabs(tabDef));
		}
		return tab;
	}

	@Override
	public ITab getActiveTab() {
		return vaadinRootTab;
	}

	@Override
	protected ComponentContainer createMainContainer() {
		Panel mainPanel = new Panel();
		mainPanel.setSizeFull();
		if (!multipleLevels) {
			firstContainer = mainPanel;
		} else {
			VerticalSplitPanel vSplit = new VerticalSplitPanel();
			vSplit.setSizeFull();
			mainPanel.setContent(vSplit);
			firstContainer = new Panel();
			firstContainer.setSizeFull();
			vSplit.setFirstComponent(firstContainer);
			secondContainer = new Panel();
			secondContainer.setSizeFull();
			vSplit.setSecondComponent(secondContainer);
			vSplit.setLocked(false);
		}
		return mainPanel;
	}

}