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
import java.util.Map;

import org.abstractform.binding.BBindingToolkit;
import org.abstractform.binding.BForm;
import org.abstractform.binding.vaadin.VaadinBindingFormInstance;
import org.abstractform.binding.vaadin.VaadinBindingFormToolkit;
import org.vaadin.dialogs.ConfirmDialog;

import org.openeos.services.ui.ConfirmationCallback;
import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.UIDialog;
import org.openeos.services.ui.UIDialogCloseEvent;
import org.openeos.services.ui.UIDialogListener;
import org.openeos.services.ui.form.BindingFormCapability;
import org.openeos.services.ui.form.FormRegistryService;
import org.openeos.services.ui.form.abstractform.AbstractFormBindingForm;
import org.openeos.services.ui.vaadin.internal.abstractform.UIVaadinFormToolkit;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class UIApplicationImpl implements UIApplication<IUnoVaadinApplication> {

	private IUnoVaadinApplication vaadinApplication;
	private FormRegistryService formRegistryService;
	private BBindingToolkit uiBeanBindingToolkit;
	private BBindingToolkit commonBindingToolkit;
	private VaadinBindingFormToolkit vaadinToolkit;
	private UIDAOService uidaoService;

	public UIApplicationImpl(FormRegistryService formRegistryService, BBindingToolkit uiBeanBindingToolkit,
			BBindingToolkit commonBindingToolkit, VaadinBindingFormToolkit vaadinToolkit, UIDAOService uidaoService) {
		this.formRegistryService = formRegistryService;
		this.uiBeanBindingToolkit = uiBeanBindingToolkit;
		this.commonBindingToolkit = commonBindingToolkit;
		this.vaadinToolkit = vaadinToolkit;
		this.uidaoService = uidaoService;
	}

	public void setVaadinApplication(IUnoVaadinApplication vaadinApplication) {
		this.vaadinApplication = vaadinApplication;
	}

	@Override
	public void showMessage(MessageType type, String caption) {
		Notification notif = createNotification(type, caption);
		vaadinApplication.getMainWindow().showNotification(notif);

	}

	@Override
	public void showMessage(MessageType type, String caption, String description) {
		Notification notif = createNotification(type, caption);
		notif.setDescription(description);
		vaadinApplication.getMainWindow().showNotification(notif);
	}

	protected Notification createNotification(MessageType type, String caption) {
		Notification notif;
		switch (type) {
		case ERROR:
			notif = new Notification(caption, Notification.TYPE_ERROR_MESSAGE);
			break;
		case WARNING:
			notif = new Notification(caption, Notification.TYPE_WARNING_MESSAGE);
			break;
		default:
			notif = new Notification(caption, Notification.TYPE_HUMANIZED_MESSAGE);
			break;
		}
		return notif;
	}

	@Override
	public UIDialog showEditFormDialog(UIBean model) {
		return showFormDialog(model.getModelClass(), model, BindingFormCapability.EDIT);
	}

	@Override
	public UIDialog showNewFormDialog(UIBean model) {
		return showFormDialog(model.getModelClass(), model, BindingFormCapability.NEW);
	}

	@Override
	public <U> UIDialog showGenericFormDialog(BForm<U> form, U bean) {
		Map<String, Object> extraObjects = new HashMap<String, Object>();
		extraObjects.put(UIVaadinFormToolkit.EXTRA_OBJECT_APPLICATION, this);
		VaadinBindingFormInstance formInstance = vaadinToolkit.buildForm(form, commonBindingToolkit, extraObjects, false);

		Window window = new Window(form.getName());
		window.setModal(true);
		window.addComponent((ComponentContainer) formInstance.getImplementation());
		formInstance.setValue(bean);

		HorizontalLayout footer = new HorizontalLayout();
		footer.setWidth(100, HorizontalLayout.UNITS_PERCENTAGE);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		Button buttonOk = new Button("Ok");
		buttons.addComponent(buttonOk);
		Button buttonCancel = new Button("Cancel");
		buttons.addComponent(buttonCancel);

		footer.addComponent(buttons);
		footer.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
		window.addComponent(footer);
		((VerticalLayout) window.getContent()).setComponentAlignment(footer, Alignment.BOTTOM_CENTER);
		window.getContent().setSizeFull();
		vaadinApplication.getMainWindow().addWindow(window);

		return new UIDialogImpl(window, buttonOk, buttonCancel, formInstance);

	}

	private <T> UIDialog showFormDialog(Class<T> modelClass, UIBean model, BindingFormCapability capability) {
		AbstractFormBindingForm<T> form = formRegistryService.getDefaultForm(modelClass, AbstractFormBindingForm.class, capability);
		Map<String, Object> extraObjects = new HashMap<String, Object>();
		extraObjects.put(UIVaadinFormToolkit.EXTRA_OBJECT_APPLICATION, this);
		VaadinBindingFormInstance formInstance = vaadinToolkit
				.buildForm(form.getBForm(), uiBeanBindingToolkit, extraObjects, false);

		Window window = new Window(form.getBForm().getName());
		window.setModal(true);
		window.addComponent((ComponentContainer) formInstance.getImplementation());
		formInstance.setValue(model);

		HorizontalLayout footer = new HorizontalLayout();
		footer.setWidth(100, HorizontalLayout.UNITS_PERCENTAGE);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		Button buttonOk = new Button("Ok");
		buttons.addComponent(buttonOk);
		Button buttonCancel = new Button("Cancel");
		buttons.addComponent(buttonCancel);

		footer.addComponent(buttons);
		footer.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
		window.addComponent(footer);
		((VerticalLayout) window.getContent()).setComponentAlignment(footer, Alignment.BOTTOM_CENTER);
		window.getContent().setSizeFull();
		vaadinApplication.getMainWindow().addWindow(window);

		return new UIDialogImpl(window, buttonOk, buttonCancel, formInstance);
	}

	@Override
	public <T> UIDialog showEditFormDialog(Class<T> beanClass, T model) {
		return showEditFormDialog(uidaoService.wrap(beanClass, model));
	}

	@Override
	public <T> UIDialog showNewFormDialog(Class<T> beanClass, T model) {
		return showNewFormDialog(uidaoService.wrap(beanClass, model));
	}

	private class UIDialogImpl implements UIDialog, Button.ClickListener, Window.CloseListener {

		private UIDialogListener dialogListener;
		private Window window;
		private Button buttonOk;
		private Button buttonCancel;
		private VaadinBindingFormInstance<?> formInstance;
		private String buttonIdPressed = BUTTON_CANCEL;

		public UIDialogImpl(Window window, Button buttonOk, Button buttonCancel, VaadinBindingFormInstance<?> formInstance) {
			this.window = window;
			this.buttonOk = buttonOk;
			this.buttonCancel = buttonCancel;
			this.formInstance = formInstance;
			hookListeners();
		}

		private void hookListeners() {
			window.addListener(this);
			buttonOk.addListener(this);
			buttonCancel.addListener(this);
		}

		@Override
		public void close() {
			window.getParent().removeWindow(window);
		}

		private void fireOnCloseEvent() {
			if (dialogListener != null) {
				dialogListener.onClose(new UIDialogCloseEvent(this, buttonIdPressed));
			}
		}

		@Override
		public void setUIDialogListener(UIDialogListener dialogListener) {
			this.dialogListener = dialogListener;
		}

		@Override
		public void buttonClick(ClickEvent event) {
			if (event.getButton() == buttonOk) {
				buttonOkClick();
			} else if (event.getButton() == buttonCancel) {
				buttonCancelClick();

			}
		}

		private void buttonOkClick() {
			formInstance.updateModel();
			if (formInstance.getErrors().size() > 0) {
				showMessage(MessageType.WARNING, "There are errors in form");
			} else {
				buttonIdPressed = BUTTON_OK;
				close();
			}
		}

		private void buttonCancelClick() {
			close();
		}

		@Override
		public void windowClose(CloseEvent e) {
			fireOnCloseEvent();
		}

	}

	@Override
	public void refreshNotifications() {
		vaadinApplication.refreshNotifications();
	}

	@Override
	public Class<IUnoVaadinApplication> getConcreteApplicationClass() {
		return IUnoVaadinApplication.class;
	}

	@Override
	public IUnoVaadinApplication getConcreteApplication() {
		return this.vaadinApplication;
	}

	@Override
	public void showConfirmation(String caption, final ConfirmationCallback callBack) {
		ConfirmDialog.show(this.vaadinApplication.getMainWindow(), caption, new ConfirmDialog.Listener() {

			private static final long serialVersionUID = -632551512286442148L;

			@Override
			public void onClose(ConfirmDialog dialog) {
				callBack.onCloseConfirmation(dialog.isConfirmed());
			}
		});
	}

	@Override
	public Object contextObject(String name) {
		return vaadinApplication.getContextObject(name);
	}

	@Override
	public void setContextObject(String name, Object contextObject) {
		vaadinApplication.setContextObject(name, contextObject);
	}

	@Override
	public Object getContextObject(String key) {
		return contextObject(key);
	}

	@Override
	public String[] getContextObjectKeys() {
		return vaadinApplication.getContextObjectKeys();
	}

	@Override
	public boolean restart() {
		vaadinApplication.getMainWindow().getApplication().close();
		return true;
	}

}
