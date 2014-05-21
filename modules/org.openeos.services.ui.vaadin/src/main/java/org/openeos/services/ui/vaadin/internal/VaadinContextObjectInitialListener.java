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

import java.util.List;

import org.openeos.services.ui.ContextObjectContainer;
import org.openeos.services.ui.ContextObjectContributor;
import org.openeos.services.ui.vaadin.ApplicationLifecycleListener;
import org.openeos.vaadin.main.IUnoVaadinApplication;

public class VaadinContextObjectInitialListener implements ApplicationLifecycleListener {

	private List<ContextObjectContributor> contextObjectCotributorList;

	public VaadinContextObjectInitialListener(List<ContextObjectContributor> contextObjectCotributorList) {
		this.contextObjectCotributorList = contextObjectCotributorList;
	}

	@Override
	public void onInit(final IUnoVaadinApplication application) {
		ContextObjectContainer container = new ContextObjectContainer() {

			@Override
			public void setContextObject(String key, Object contextObject) {
				application.setContextObject(key, contextObject);
			}

			@Override
			public String[] getContextObjectKeys() {
				return application.getContextObjectKeys();
			}

			@Override
			public Object getContextObject(String key) {
				return application.getContextObject(key);
			}
		};

		for (ContextObjectContributor contributor : contextObjectCotributorList) {
			contributor.contributeInitialContextObjects(container);
		}
	}

	@Override
	public void onClose(IUnoVaadinApplication application) {
	}

	@Override
	public void onTransactionStart(IUnoVaadinApplication application) {
	}

	@Override
	public void onTransactionEnd(IUnoVaadinApplication application) {
	}

}
