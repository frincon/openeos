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

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class SafeWorkItemHandlerDecorator implements WorkItemHandler {

	private WorkItemHandler delegate;
	private WorkItemExceptionHandler exceptionHandler;

	public SafeWorkItemHandlerDecorator(WorkItemHandler delegate, WorkItemExceptionHandler exceptionHandler) {
		this.delegate = delegate;
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		try {
			delegate.executeWorkItem(workItem, manager);
		} catch (Exception e) {
			exceptionHandler.handleExecuteException(e, workItem, manager);
		}
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		try {
			delegate.abortWorkItem(workItem, manager);
		} catch (Exception e) {
			exceptionHandler.handleAbortException(e, workItem, manager);
		}
	}

}
