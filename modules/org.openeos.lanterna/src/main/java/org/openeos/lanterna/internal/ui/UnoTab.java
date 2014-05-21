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
package org.openeos.lanterna.internal.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.lanterna.Field;
import org.openeos.lanterna.LanternaEditor;
import org.openeos.lanterna.ValueChangeEvent;
import org.openeos.lanterna.ValueChangeListener;
import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.IIdentificationCapable;
import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.ui.IWindowManagerService;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.model.IFieldDefinition;
import org.openeos.services.ui.model.ITab;
import org.openeos.services.ui.model.ITabDefinition;
import org.openeos.services.ui.model.SelectionChangeListener;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.VerticalLayout;

class UnoTab extends Panel implements ITab, ValueChangeListener {

	private static final Logger LOG = LoggerFactory.getLogger(UnoTab.class);

	private ITabDefinition tabDefinition;
	private View actualView;
	private IDictionaryService dictionaryService;
	private IWindowManagerService windowManagerService;
	private UIDAOService uiDAOService;
	private boolean trySelect = false;

	private List<UIBean> uiBeanList;
	private UIBean activeUiBean;
	
	private MultiColumnListBox listView;

	public UnoTab(ITabDefinition tabDefinition, IDictionaryService dictionaryService, UIDAOService uiDAOService,
			IWindowManagerService windowManagerService) {
		super(new Border.Invisible(), Orientation.VERTICAL);
		this.tabDefinition = tabDefinition;
		this.dictionaryService = dictionaryService;
		this.uiDAOService = uiDAOService;
		this.windowManagerService = windowManagerService;
		showView(View.LIST);
		refresh();
	}

	// TODO Make better with filter aproach
	@Transactional(readOnly = true)
	public void refresh() {
		switch (actualView) {
		case LIST:
			refreshList();
			break;
		case EDIT:
			// refreshForm(); // TODO
			break;
		}
	}

	private void refreshList() {
		getListView().removeAllRows();
		IClassDefinition classDef = dictionaryService.getClassDefinition(tabDefinition.getClassName());
		uiBeanList = uiDAOService.list(classDef.getClassDefined());
		int row = 0;
		for (UIBean obj : uiBeanList) {
			String[] columns = new String[tabDefinition.getFieldList().size()];
			int i = 0;
			for (IFieldDefinition field : tabDefinition.getFieldList()) {
				columns[i] = getStringProperty(obj, field);
				i++;
			}
			getListView().addRow(columns);
			if (trySelect && activeUiBean != null && obj.isSameIdentity(activeUiBean)) {
				LOG.trace("Row selected because trySelect is active.");
				getListView().setSelectedIndex(row);
				trySelect = false;
			}
			row++;
		}
	}

	private String getStringProperty(UIBean obj, IFieldDefinition field) {
		Object value = obj.get(field.getProperty());
		if (value instanceof IIdentificationCapable) {
			IIdentificationCapable ic = (IIdentificationCapable) value;
			return ic.getIdentifier();
		}
		return (value == null ? "" : value.toString());
	}

	@Override
	public void addSelectionChangeListener(SelectionChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSelectionChangeListener(SelectionChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public ITabDefinition getTabDefinition() {
		return tabDefinition;
	}

	@Override
	public UIBean getActiveObject() {
		switch (actualView) {
		case EDIT:
			return activeUiBean;
		case LIST:
			if (listView.getSelectedIndex() == -1) {
				return null;
			} else {
				return uiBeanList.get(listView.getSelectedIndex());
			}
		default:
			return null;
		}
	}

	@Override
	public List<UIBean> getSelectedObjects() {
		return Collections.unmodifiableList(Arrays.asList(getActiveObject()));
	}

	@Override
	public void setActiveObject(UIBean obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showView(View view) {
		this.removeAllComponents();
		switch (view) {
		case LIST:
			trySelect = false;
			if (this.actualView == View.EDIT && activeUiBean != null) {
				trySelect = true;
			}
			this.addComponent(getListView(), VerticalLayout.MAXIMIZES_HORIZONTALLY, VerticalLayout.MAXIMIZES_VERTICALLY);
			this.actualView = View.LIST;
			refresh();
			break;
		case EDIT:
			if (getActiveObject() == null) {
				showView(View.LIST);
			} else {
				this.activeUiBean = getActiveObject();
				this.actualView = view;
				this.addComponent(getForm(), VerticalLayout.MAXIMIZES_HORIZONTALLY, VerticalLayout.MAXIMIZES_VERTICALLY);
			}
			break;
		}
	}

	private Component getForm() {
		Panel panel = new Panel(new Border.Invisible(), Orientation.VERTICAL);
		for (IFieldDefinition field : tabDefinition.getFieldList()) {
			Panel panel2 = new Panel(new Border.Invisible(), Orientation.HORISONTAL);
			LanternaEditor editor = windowManagerService.getEditor(field, LanternaEditor.class);
			//			Label label = new Label(field.getName());
			//			TextBox textBox = new TextBox(getStringProperty(getActiveObject(), field));
			panel2.addComponent(editor.getLabel());
			Field fieldComponent = editor.getField();
			fieldComponent.setValue(getActiveObject().get(field.getProperty()));
			fieldComponent.addValueChangeListener(this);
			fieldComponent.setCustomObject(field);
			panel2.addComponent(editor.getField());
			panel.addComponent(panel2);
		}
		return panel;
	}

	@Override
	public void showView(View view, UIBean activeObject) {
		LOG.trace(String.format("Show view called with activeObject [%s]", activeObject));
		activeUiBean = activeObject;
		this.actualView = view;
		showView(view);
	}

	protected MultiColumnListBox getListView() {
		if (listView == null) {
			createListView();
		}
		return listView;
	}

	private void createListView() {
		listView = new MultiColumnListBox();
		for (IFieldDefinition field : tabDefinition.getFieldList()) {
			listView.addColumn(field.getName());
		}
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Object value = event.getNewValue();
		if (event.getSource().getCustomObject() instanceof IFieldDefinition) {
			IFieldDefinition fieldDef = (IFieldDefinition) event.getSource().getCustomObject();
			getActiveObject().set(fieldDef.getProperty(), value);
		} else {
			LOG.warn("ValueChange fired but custom object is not IFieldDefinition class");
		}
	}
}
