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

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.services.ui.vaadin.ApplicationLifecycleListener;
import org.openeos.vaadin.main.ApplicationLifecycleManager;
import org.openeos.vaadin.main.IUnoVaadinApplication;

public abstract class AbstractVaadinLifecycleManager implements ApplicationLifecycleManager {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractVaadinLifecycleManager.class);

	@Override
	public void init(IUnoVaadinApplication application) {
		Utils.traceCall(LOG);
		for (ApplicationLifecycleListener listener : getApplicationLifecycleListenerList()) {
			listener.onInit(application);
		}
	}

	@Override
	public void close(IUnoVaadinApplication application) {
		Utils.traceCall(LOG);
		for (ApplicationLifecycleListener listener : getApplicationLifecycleListenerListReverse()) {
			listener.onClose(application);
		}
	}

	@Override
	public void transactionStart(IUnoVaadinApplication application, Object transactionData) {
		Utils.traceCall(LOG);
		for (ApplicationLifecycleListener listener : getApplicationLifecycleListenerList()) {
			listener.onTransactionStart(application);
		}
	}

	@Override
	public void transactionEnd(IUnoVaadinApplication application, Object transactionData) {
		Utils.traceCall(LOG);
		for (ApplicationLifecycleListener listener : getApplicationLifecycleListenerListReverse()) {
			listener.onTransactionEnd(application);
		}
	}

	protected abstract Collection<ApplicationLifecycleListener> getApplicationLifecycleListenerList();

	protected Collection<ApplicationLifecycleListener> getApplicationLifecycleListenerListReverse() {
		LinkedList<ApplicationLifecycleListener> linkedList = new LinkedList<ApplicationLifecycleListener>();
		for (ApplicationLifecycleListener listener : getApplicationLifecycleListenerList()) {
			linkedList.addFirst(listener);
		}
		return linkedList;
	}

}
