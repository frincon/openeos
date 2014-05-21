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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.openeos.services.dictionary.IIdentificationCapable;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.model.IFieldDefinition;
import org.openeos.services.ui.model.ITab;
import org.openeos.services.ui.model.ITabDefinition;
import org.openeos.services.ui.model.RefreshType;
import org.openeos.services.ui.model.SelectionChangeEvent;
import org.openeos.services.ui.model.SelectionChangeListener;
import org.openeos.services.ui.model.ViewChangeEvent;
import org.openeos.services.ui.model.ViewChangeListener;
import org.openeos.services.ui.vaadin.IVaadinContainerFactory;
import org.openeos.services.ui.vaadin.internal.containers.UIDAOItem;
import org.openeos.services.ui.window.SelectionChangeSupport;
import org.openeos.services.ui.window.ViewChangeSupport;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractVaadinTabImpl implements ITab, ValueChangeListener {

	private static final long serialVersionUID = 6007283647042828004L;

	private AbstractOrderedLayout mainContainer;
	private Table table;
	private ITabDefinition tab;
	private UIBean activeObject;
	private Container container;
	private IVaadinContainerFactory containerFactory;
	private ArrayList<String> formPropertyList;
	private UIApplication<IUnoVaadinApplication> application;

	private View view = View.LIST;

	private SelectionChangeSupport selectionChangeSupport = new SelectionChangeSupport();
	private ViewChangeSupport viewChangeSupport = new ViewChangeSupport();

	public AbstractVaadinTabImpl(ITabDefinition tabDefinition, IVaadinContainerFactory containerFactory,
			UIApplication<IUnoVaadinApplication> application) {

		this.tab = tabDefinition;
		this.containerFactory = containerFactory;
		this.application = application;
		this.mainContainer = createMainContainer();

		formPropertyList = new ArrayList<String>(tabDefinition.getFieldList().size());
		for (IFieldDefinition field : tabDefinition.getFieldList()) {
			formPropertyList.add(field.getProperty());
		}
		table.focus();
		showView(view);

	}

	public Table getTable() {
		return table;
	}

	public Container getContainer() {
		return container;
	}

	public IVaadinContainerFactory getContainerFactory() {
		return containerFactory;
	}

	public ArrayList<String> getFormPropertyList() {
		return formPropertyList;
	}

	public ComponentContainer getMainContainer() {
		return mainContainer;
	}

	protected AbstractOrderedLayout createMainContainer() {
		VerticalLayout mainComponentContainer = new VerticalLayout();
		mainComponentContainer.setSizeFull();
		mainComponentContainer.setMargin(false);

		table = createTable();
		return mainComponentContainer;

	}

	@Override
	public void showView(View view) {
		// TODO Make more efficient
		getMainContainer().removeAllComponents();
		switch (view) {
		case EDIT:
			this.view = View.EDIT;
			showEdit();
			fireViewChange(new ViewChangeEvent(this));
			break;
		case LIST:
			this.view = View.LIST;
			showTable();
			fireViewChange(new ViewChangeEvent(this));
			break;
		}

	}

	protected abstract void showEdit();

	protected Table createTable() {
		Table table = new Table(null) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7081571334007358849L;

			@Override
			protected String formatPropertyValue(Object rowId, Object colId, Property property) {
				Object value = property.getValue();
				// TODO Maybe best in container
				if (value instanceof IIdentificationCapable) {
					return ((IIdentificationCapable) value).getIdentifier();
				}
				return super.formatPropertyValue(rowId, colId, property);
			}

		};

		table.setSelectable(true);
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);
		table.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);

		table.setSizeFull();
		table.setImmediate(true);
		table.addListener(this);

		return table;
	}

	@Override
	public List<UIBean> getSelectedObjects() {
		if (view == View.EDIT) {
			return Arrays.asList(getActiveObject());
		} else {
			return getSelectedObjectsInList();
		}
	}

	protected List<UIBean> getSelectedObjectsInList() {
		Object value = table.getValue();
		ArrayList<UIBean> result = new ArrayList<UIBean>();
		if (value instanceof Set) {
			Set<Object> selected = (Set<Object>) value;
			for (Object itemId : selected) {
				Item item = table.getItem(itemId);
				if (item instanceof UIDAOItem) {
					UIDAOItem uidaoItem = (UIDAOItem) item;
					result.add(uidaoItem.getUiBean());
				}
			}
		} else if (value != null) {
			Item item = table.getItem(value);
			if (item instanceof UIDAOItem) {
				UIDAOItem uidaoItem = (UIDAOItem) item;
				result.add(uidaoItem.getUiBean());
			}
		}
		return result;

	}

	@Override
	public void setActiveObject(UIBean obj) {
		Item activeItem = null;
		if (obj == null) {
			table.select(null);
			this.activeObject = null;
		} else {
			if (searchAndSelect(obj)) {
				activeItem = (Item) container.getItem(table.getValue());
				this.activeObject = ((UIDAOItem) activeItem).getUiBean();
			} else {
				table.select(null);
				activeItem = containerFactory.createItem(obj);
				this.activeObject = obj;
			}
		}
	}

	public boolean searchAndSelect(UIBean obj) {
		Object idInContainer = searchIdInContainer(obj);
		if (idInContainer != null) {
			table.select(idInContainer);
			return true;
		}
		return false;
	}

	protected Object searchIdInContainer(UIBean obj) {
		for (Object id : container.getItemIds()) {
			if (obj.isSameIdentity(((UIDAOItem) container.getItem(id)).getUiBean())) {
				return id;
			}
		}
		return null;
	}

	@Override
	public ITabDefinition getTabDefinition() {
		return tab;
	}

	@Override
	public UIBean getActiveObject() {
		return activeObject;
	}

	@Override
	public void showView(View view, UIBean activeObject) {
		this.setActiveObject(activeObject);
		this.showView(view);
	}

	protected void showTable() {
		List<UIBean> previousSelected = getSelectedObjects();
		refreshContainer();
		table.setContainerDataSource(container);
		table.setVisibleColumns(formPropertyList.toArray());
		this.setActiveObject(null);
		mainContainer.addComponent(table);
		mainContainer.setExpandRatio(table, 1.0f);
		if (previousSelected.size() == 1) {
			searchAndSelect(previousSelected.get(0));
		} else {
			// TODO
		}
	}

	protected void refreshContainer() {
		this.container = containerFactory.createContainer(tab.getClassName());
	}

	public UIApplication<IUnoVaadinApplication> getApplication() {
		return application;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		if (event.getProperty() == table) {
			fireSelectionChange(new SelectionChangeEvent(this, getSelectedObjects().toArray()));
		}
	}

	@Override
	public void refresh(RefreshType refreshType) {
		if (view == View.LIST) {
			showTable();
			fireSelectionChange(new SelectionChangeEvent(this, getSelectedObjects().toArray()));
		} else if (view == View.EDIT) {
			refreshContainer();
			Object id = searchIdInContainer(getActiveObject());
			if (id != null) {
				UIDAOItem item = (UIDAOItem) container.getItem(id);
				getMainContainer().removeAllComponents();
				setActiveObject(item.getUiBean());
				showEdit();
			} else {
				showView(View.LIST);
			}
			fireSelectionChange(new SelectionChangeEvent(this, getSelectedObjects().toArray()));
		}
	}

	@Override
	public View getActualView() {
		return view;
	}

	//////////////////////////////////////////
	// Listener Handling
	//////////////////////////////////////////
	@Override
	public void addSelectionChangeListener(SelectionChangeListener listener) {
		selectionChangeSupport.addListener(listener);
	}

	@Override
	public void removeSelectionChangeListener(SelectionChangeListener listener) {
		selectionChangeSupport.removeListener(listener);
	}

	protected void fireSelectionChange(SelectionChangeEvent event) {
		selectionChangeSupport.fireEvent(event);
	}

	@Override
	public void addViewChangeListener(ViewChangeListener listener) {
		viewChangeSupport.addListener(listener);
	}

	@Override
	public void removeViewChangeListener(ViewChangeListener listener) {
		viewChangeSupport.removeListener(listener);
	}

	protected void fireViewChange(ViewChangeEvent event) {
		viewChangeSupport.fireEvent(event);
	}

}
