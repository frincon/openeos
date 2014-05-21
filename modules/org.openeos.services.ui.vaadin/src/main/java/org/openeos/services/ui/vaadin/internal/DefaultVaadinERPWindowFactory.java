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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.ConfirmationCallback;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.WindowActionContributor;
import org.openeos.services.ui.model.IWindow;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.model.SelectionChangeEvent;
import org.openeos.services.ui.model.SelectionChangeListener;
import org.openeos.services.ui.model.TabChangeEvent;
import org.openeos.services.ui.model.TabChangeListener;
import org.openeos.services.ui.model.ViewChangeEvent;
import org.openeos.services.ui.model.ViewChangeListener;
import org.openeos.services.ui.model.WindowAction;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.services.ui.vaadin.IVaadinERPWindowFactory;
import org.openeos.services.ui.vaadin.VaadinWindow;
import org.openeos.vaadin.main.CloseTabListener;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class DefaultVaadinERPWindowFactory implements IVaadinERPWindowFactory {

	private IDictionaryService dictionaryService;
	private IVaadinContainerFactory containerFactory;
	private FormFieldFactory formFieldFactory;
	private List<WindowActionContributor> windowActionContributiorList = new ArrayList<WindowActionContributor>();

	public DefaultVaadinERPWindowFactory(IDictionaryService dictionaryService, IVaadinContainerFactory containerFactory,
			FormFieldFactory formFieldFactory) {
		this.dictionaryService = dictionaryService;
		this.containerFactory = containerFactory;
		this.formFieldFactory = formFieldFactory;
	}

	@Override
	public Component createWindowComponent(IWindowDefinition window, final UIApplication<IUnoVaadinApplication> application) {

		final VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();

		verticalLayout.setMargin(false);

		final VaadinWindow vaadinWindow = createWindowImpl(window, application);
		Component toolbar = createToolBar(vaadinWindow);
		Component entityActionPanel = createEntityActionPanel(vaadinWindow, application);
		if (entityActionPanel != null) {
			verticalLayout.addComponent(toolbar);
			HorizontalLayout horizontal = new HorizontalLayout();
			horizontal.addComponent(vaadinWindow.getComponent());
			horizontal.addComponent(entityActionPanel);
			horizontal.setSizeFull();
			horizontal.setExpandRatio(vaadinWindow.getComponent(), 1.0f);
			verticalLayout.addComponent(horizontal);
			verticalLayout.setExpandRatio(toolbar, 0);
			verticalLayout.setExpandRatio(horizontal, 1);
		} else {
			verticalLayout.addComponent(toolbar);
			verticalLayout.addComponent(vaadinWindow.getComponent());
			verticalLayout.setExpandRatio(toolbar, 0);
			verticalLayout.setExpandRatio(vaadinWindow.getComponent(), 1);
		}

		application.getConcreteApplication().addCloseTabListener(new CloseTabListener() {
			@Override
			public boolean closeTab(final TabSheet tabSheet, final Component c) {
				if (c == verticalLayout && vaadinWindow.getActiveTab().isModified()) {
					application.showConfirmation("The changes will be lost, are you sure?", new ConfirmationCallback() {

						@Override
						public void onCloseConfirmation(boolean accepted) {
							if (accepted) {
								tabSheet.removeComponent(c);
								removeListener();
							}
						}
					});
					return true;
				}
				return false;
			}

			private void removeListener() {
				application.getConcreteApplication().removeCloseTabListener(this);
			}

		});

		return verticalLayout;

	}

	protected Component createEntityActionPanel(VaadinWindow vaadinWindow, UIApplication<IUnoVaadinApplication> uiApplication) {
		return null;
	}

	protected VaadinWindow createWindowImpl(IWindowDefinition windowDefinition, UIApplication<IUnoVaadinApplication> application) {
		return new VaadinWindowImpl(windowDefinition, containerFactory, dictionaryService, application, formFieldFactory);
	}

	protected Component createToolBar(final IWindow window) {
		HorizontalLayout horLayout = new HorizontalLayout();

		TreeMap<Integer, Integer> mapIndexes = new TreeMap<Integer, Integer>();

		for (WindowActionContributor contributor : windowActionContributiorList) {

			for (WindowAction action : contributor.getWindowActionList()) {
				if (action.isVisibeForWindow(window)) {
					Integer order = action.getOrder();
					Integer maxOrder = mapIndexes.floorKey(order);
					if (maxOrder == null) {
						//add to the last
						horLayout.addComponent(createToolBarButton(action, window));
						mapIndexes.put(order, horLayout.getComponentCount() - 1);
					} else {
						Integer index = mapIndexes.get(maxOrder);
						horLayout.addComponent(createToolBarButton(action, window), index + 1);
						mapIndexes.put(order, index + 1);
					}
				}
			}
		}

		return horLayout;
	}

	protected Component createToolBarButton(final WindowAction action, final IWindow window) {
		final Button button = new Button();
		button.setCaption(action.getCaption());
		button.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				action.run(window);
			}
		});

		button.setEnabled(action.isEnabledForWindow(window));
		window.addSelectionChangeListener(new SelectionChangeListener() {

			@Override
			public void selectionChange(SelectionChangeEvent selectionChangeEvent) {
				button.setEnabled(action.isEnabledForWindow(window));
			}

		});
		window.addTabChangeListener(new TabChangeListener() {

			@Override
			public void tabChange(TabChangeEvent tabChangeEvent) {
				button.setEnabled(action.isEnabledForWindow(window));
			}

		});
		window.addViewChangeListener(new ViewChangeListener() {

			@Override
			public void viewChange(ViewChangeEvent viewChangeEvent) {
				button.setEnabled(action.isEnabledForWindow(window));
			}
		});

		return button;
	}

	public void bindWindowActionContributor(WindowActionContributor contributor) {
		windowActionContributiorList.add(contributor);
	}

	public void unbindWindowActionContributor(WindowActionContributor contributor) {
		windowActionContributiorList.remove(contributor);
	}

	public IDictionaryService getDictionaryService() {
		return dictionaryService;
	}

	public IVaadinContainerFactory getContainerFactory() {
		return containerFactory;
	}

	public FormFieldFactory getFormFieldFactory() {
		return formFieldFactory;
	}

}
