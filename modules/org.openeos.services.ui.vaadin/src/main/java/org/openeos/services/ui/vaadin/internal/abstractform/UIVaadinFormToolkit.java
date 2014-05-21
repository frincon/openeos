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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.abstractform.binding.BField;
import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.vaadin.TableContainer;
import org.abstractform.core.Field;
import org.abstractform.core.FormInstance;
import org.abstractform.core.table.IFTable;
import org.abstractform.vaadin.VaadinDataObject;
import org.abstractform.vaadin.VaadinFieldValueAccessor;
import org.abstractform.vaadin.VaadinFormToolkit;

import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.form.abstractform.BFUIButton;
import org.openeos.services.ui.form.abstractform.BFUITable;
import org.openeos.services.ui.form.abstractform.UIButtonController;
import org.openeos.services.ui.form.abstractform.UITableController;
import org.openeos.services.ui.vaadin.internal.UIApplicationImpl;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class UIVaadinFormToolkit extends VaadinFormToolkit {

	public static final String EXTRA_OBJECT_APPLICATION = UIVaadinFormToolkit.class.getPackage().getName()
			+ ".EXTRA_OBJECT_APPLICATION";

	@Override
	protected AbstractComponent internalBuildField(org.abstractform.core.Field field, Map<String, Object> extraObjects) {
		AbstractComponent ret;
		if (BFUIButton.TYPE_UI_BUTTON.equals(field.getType())) {
			UIVaadinButtonField button = new UIVaadinButtonField(field.getName(),
					(UIButtonController) field.getExtra(BFUIButton.EXTRA_UI_BUTTON_CONTROLLER),
					(UIApplicationImpl) extraObjects.get(EXTRA_OBJECT_APPLICATION));
			ret = button;
		} else if (BFUITable.TYPE_UITABLE.equals(field.getType())) {
			final Table table = (Table) buildTableField(field, extraObjects);
			HorizontalLayout mainLayout = new HorizontalLayout();
			mainLayout.setSizeFull();
			//mainLayout.setSizeFull();
			mainLayout.setMargin(false);
			mainLayout.setSpacing(true);
			mainLayout.addComponent(table);

			VerticalLayout buttonLayout = new VerticalLayout();
			buttonLayout.setMargin(false);
			buttonLayout.setSpacing(true);
			//buttonLayout.setSizeFull();

			final UITableController controller = (UITableController) field.getExtra(BFUITable.EXTRA_TABLE_CONTROLLER);
			final UIApplication application = (UIApplication) extraObjects.get(EXTRA_OBJECT_APPLICATION);

			Button buttonNew = new Button("N", new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					controller.onNew(application, ((UITableContainer) table.getContainerDataSource()).getFormInstance(),
							((TableContainer) table.getContainerDataSource()).getValues());
				}
			});
			buttonNew.setWidth("100%");
			buttonLayout.addComponent(buttonNew);
			Button buttonEdit = new Button("E", new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					controller.onEdit(application, ((UITableContainer) table.getContainerDataSource()).getFormInstance(),
							((TableContainer) table.getContainerDataSource()).getValues(), table.getValue());
				}
			});
			buttonEdit.setWidth("100%");
			buttonLayout.addComponent(buttonEdit);
			Button buttonDelete = new Button("D", new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					controller.onDelete(application, ((UITableContainer) table.getContainerDataSource()).getFormInstance(),
							((TableContainer) table.getContainerDataSource()).getValues(), table.getValue());
				}
			});
			buttonDelete.setWidth("100%");
			buttonLayout.addComponent(buttonDelete);

			buttonLayout.setWidth("50px");

			mainLayout.addComponent(buttonLayout);
			mainLayout.setExpandRatio(table, 1.0f);
			ret = mainLayout;

		} else {
			ret = super.internalBuildField(field, extraObjects);
			if (field.getType().equals(Field.TYPE_NUMERIC)) {
				//TODO This normally can be many types of classes and can be with data conversion in binding tier
				((TextField) ret).setPropertyDataSource(new ObjectProperty<BigDecimal>(null, BigDecimal.class));
			}
		}
		return ret;
	}

	@Override
	protected VaadinFieldValueAccessor getValueAccessor(Field field, AbstractComponent component) {
		if (BFUITable.TYPE_UITABLE.equals(field.getType())) {
			return new VaadinFieldValueAccessor() {

				@Override
				public void setFieldValue(FormInstance formInstance, AbstractComponent field, Object value) {
					Table t = (Table) ((HorizontalLayout) field).getComponent(0);
					t.setContainerDataSource(new UITableContainer(
							(BFormInstance<?>) formInstance,
							(List<BField>) ((VaadinDataObject) field.getData()).getField().getExtra(IFTable.EXTRA_TABLE_FIELD_LIST),
							(Collection<Object>) value));

				}

				@Override
				public Object getFieldValue(FormInstance formInstance, AbstractComponent field) {
					Table t = (Table) ((HorizontalLayout) field).getComponent(0);
					TableContainer container = (TableContainer) t.getContainerDataSource();
					return container.getValues();
				}
			};
		} else {
			return super.getValueAccessor(field, component);
		}
	}

}
