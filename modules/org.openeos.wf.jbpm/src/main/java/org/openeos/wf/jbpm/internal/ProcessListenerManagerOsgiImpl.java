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
package org.openeos.wf.jbpm.internal;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ProcessListenerManagerOsgiImpl implements ProcessListenerManager {

	private BundleContext bundleContext;
	private ServiceTracker<ProcessEventListener, ProcessEventListener> processEventListenerTracker;

	public ProcessListenerManagerOsgiImpl(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	@Override
	public void beforeProcessStarted(ProcessStartedEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.beforeProcessStarted(event);
		}
	}

	@Override
	public void afterProcessStarted(ProcessStartedEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.afterProcessStarted(event);
		}

	}

	@Override
	public void beforeProcessCompleted(ProcessCompletedEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.beforeProcessCompleted(event);
		}

	}

	@Override
	public void afterProcessCompleted(ProcessCompletedEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.afterProcessCompleted(event);
		}

	}

	@Override
	public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.beforeNodeTriggered(event);
		}

	}

	@Override
	public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.afterNodeTriggered(event);
		}

	}

	@Override
	public void beforeNodeLeft(ProcessNodeLeftEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.beforeNodeLeft(event);
		}
	}

	@Override
	public void afterNodeLeft(ProcessNodeLeftEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.afterNodeLeft(event);
		}
	}

	@Override
	public void beforeVariableChanged(ProcessVariableChangedEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.beforeVariableChanged(event);
		}
	}

	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		for (ProcessEventListener listener : processEventListenerTracker.getTracked().values()) {
			listener.afterVariableChanged(event);
		}
	}

	@Override
	public synchronized void init() {
		processEventListenerTracker = new ServiceTracker<ProcessEventListener, ProcessEventListener>(bundleContext,
				ProcessEventListener.class, null);
		processEventListenerTracker.open();
	}

	@Override
	public synchronized void destroy() {
		processEventListenerTracker.close();
		processEventListenerTracker = null;
	}

}
