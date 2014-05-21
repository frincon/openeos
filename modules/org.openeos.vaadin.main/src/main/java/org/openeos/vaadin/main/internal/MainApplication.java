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
package org.openeos.vaadin.main.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.vaadin.main.ApplicationLifecycleManager;
import org.openeos.vaadin.main.CloseTabListener;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import org.openeos.vaadin.main.IVaadinMenuContributor;
import org.openeos.vaadin.main.NotificationManager;
import org.openeos.vaadin.main.UnoErrorHandler;
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class MainApplication extends Application implements IUnoVaadinApplication, HttpServletRequestListener, TransactionListener {

	private static final long serialVersionUID = -2191085989973275065L;

	private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

	private Window main;
	private VerticalLayout mainLayout;

	private boolean initialized = false;
	private MenuBar menubar;
	private List<IVaadinMenuContributor> menuContributorsList = new ArrayList<IVaadinMenuContributor>();
	private TabSheet tabSheet;

	private NotificationManager notificationManager;
	private ApplicationLifecycleManager applicationLifecycleManager;
	private UnoErrorHandler unoErrorHandler;

	private List<CloseTabListener> closeTabListenerList = new CopyOnWriteArrayList<CloseTabListener>();

	private Map<String, Object> mapContextObject = new HashMap<String, Object>();

	private TabSheet.CloseHandler closeHandler = new TabSheet.CloseHandler() {

		@Override
		public void onTabClose(TabSheet tabsheet, Component tabContent) {
			boolean handled = false;
			for (CloseTabListener listener : closeTabListenerList) {
				handled = handled || listener.closeTab(tabsheet, tabContent);
				if (handled) {
					return;
				}
			}
			tabsheet.removeComponent(tabContent);
		}
	};

	@Override
	public void init() {
		// TODO Make better implementation, not literal
		if (notificationManager == null) {
			throw new IllegalStateException();
		}
		if (applicationLifecycleManager == null) {
			throw new IllegalStateException();
		}
		logger.info("Initializing Main Application");

		// setTheme(Reindeer.THEME_NAME);
		// setTheme(Runo.THEME_NAME);
		// setTheme("demo");
		setTheme("uno");

		main = new Window("Dynamic Vaadin OSGi Demo");
		mainLayout = (VerticalLayout) main.getContent();
		mainLayout.setMargin(false);
		//mainLayout.setStyleName(Reindeer.L "blue");
		setMainWindow(main);

		mainLayout.setSizeFull();
		mainLayout.addComponent(getMenu());

		mainLayout.addComponent(getHeader());

		CssLayout margin = new CssLayout();
		margin.setMargin(false, true, true, true);
		margin.setSizeFull();
		tabSheet = new TabSheet();
		tabSheet.setSizeFull();
		tabSheet.setCloseHandler(closeHandler);
		margin.addComponent(tabSheet);
		mainLayout.addComponent(margin);
		mainLayout.setExpandRatio(margin, 1);

		for (IVaadinMenuContributor contributor : menuContributorsList) {
			contributor.contribute(menubar, this);
		}

		if (getContext() != null) {
			getContext().addTransactionListener(this);
		}

		initialized = true;

		applicationLifecycleManager.init(this);
	}

	private Layout getHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setMargin(true);
		header.setSpacing(true);
		// header.setStyleName(Reindeer.LAYOUT_BLACK);

		CssLayout titleLayout = new CssLayout();
		H2 title = new H2("Dynamic Vaadin OSGi Demo");
		titleLayout.addComponent(title);
		SmallText description = new SmallText("Select the \"Bundle View\" tab and activate/stop OSGi bundles dynamically.");
		description.setSizeUndefined();
		titleLayout.addComponent(description);

		header.addComponent(titleLayout);
		Component notificationArea = notificationManager.getComponent(this);
		header.addComponent(notificationArea);
		header.setComponentAlignment(notificationArea, Alignment.MIDDLE_RIGHT);

		return header;
	}

	private MenuBar getMenu() {
		menubar = new MenuBar();
		menubar.setWidth("100%");
		//		actionMenu = menubar.addItem("Action", null);
		//
		//		actionMenu.addItem("Built-in Action...", new Command() {
		//			@Override
		//			public void menuSelected(MenuItem selectedItem) {
		//				main.showNotification("Built-in Action executed!");
		//			}
		//		});
		//		actionMenu.addSeparator();

		return menubar;
	}

	class H2 extends Label {
		public H2(String caption) {
			super(caption);
			setSizeUndefined();
			setStyleName(Reindeer.LABEL_H2);
		}
	}

	class SmallText extends Label {
		public SmallText(String caption) {
			super(caption);
			setStyleName(Reindeer.LABEL_SMALL);
		}
	}

	public void bindVaadinMenuContributor(IVaadinMenuContributor menuContributor) {
		menuContributorsList.add(menuContributor);
		if (initialized) {
			menuContributor.contribute(this.menubar, this);
		}
	}

	public void unbindVaadinMenuContributor(IVaadinMenuContributor menuContributor) {
		// TODO
		System.out.println("unbindVaadinMenuContributor");
	}

	@Override
	public void addOrSelectTab(Component c, String caption) {
		tabSheet.setSelectedTab(tabSheet.addTab(c, caption));
	}

	@Override
	public TabSheet getMainTabSheet() {
		return tabSheet;
	}

	@Override
	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		logger.trace("Vaading application request start.");
	}

	@Override
	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		logger.trace("Vaading application request end.");
	}

	@Override
	public void close() {
		getContext().removeTransactionListener(this);
		applicationLifecycleManager.close(this);
		notificationManager.unregisterApplication(this);
		super.close();
	}

	@Override
	public synchronized void transactionStart(Application application, Object transactionData) {
		if (application == this) {
			applicationLifecycleManager.transactionStart(this, transactionData);
			// This method is sinchronized to prevent changes to the application from another thread
			// because in this method may be changed the application object
			notificationManager.updateNotifications(this, false);
		}
	}

	@Override
	public synchronized void transactionEnd(Application application, Object transactionData) {
		if (application == this) {
			applicationLifecycleManager.transactionEnd(this, transactionData);
		}

	}

	public void setNotificationManager(NotificationManager notificationManager) {
		this.notificationManager = notificationManager;
		notificationManager.registerApplication(this);
	}

	@Override
	public void refreshNotifications() {
		notificationManager.updateNotifications(this, true);
	}

	@Override
	public void addCloseTabListener(CloseTabListener closeTabListener) {
		closeTabListenerList.add(closeTabListener);
	}

	@Override
	public void removeCloseTabListener(CloseTabListener closeTabListener) {
		closeTabListenerList.remove(closeTabListener);
	}

	public void setUnoErrorHandler(UnoErrorHandler unoErrorHandler) {
		this.unoErrorHandler = unoErrorHandler;
	}

	public void setApplicationLifecycleManager(ApplicationLifecycleManager applicationLifecycleManager) {
		this.applicationLifecycleManager = applicationLifecycleManager;
	}

	@Override
	public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {

		if (unoErrorHandler != null) {
			unoErrorHandler.handleError(this, event);
		} else {
			super.terminalError(event);
		}

	}

	@Override
	public void setContextObject(String name, Object contextObject) {
		mapContextObject.put(name, contextObject);
	}

	@Override
	public Object getContextObject(String name) {
		return mapContextObject.get(name);
	}

	@Override
	public String[] getContextObjectKeys() {
		return mapContextObject.keySet().toArray(new String[mapContextObject.size()]);
	}

}
