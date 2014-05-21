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
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeWorkItemExceptionHandlerDecorator implements WorkItemExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SafeWorkItemExceptionHandlerDecorator.class);

	private WorkItemExceptionHandler delegate;

	public SafeWorkItemExceptionHandlerDecorator(WorkItemExceptionHandler delegate) {
		this.delegate = delegate;
	}

	@Override
	public void handleExecuteException(Exception exception, WorkItem workItem, WorkItemManager workItemManager) {
		try {
			delegate.handleExecuteException(exception, workItem, workItemManager);
		} catch (Exception ex) {
			LOG.warn("An exception has occured when trying to handle exception", ex);
		}
	}

	@Override
	public void handleAbortException(Exception exception, WorkItem workItem, WorkItemManager workItemManager) {
		try {
			delegate.handleAbortException(exception, workItem, workItemManager);
		} catch (Exception ex) {
			LOG.warn("An exception has occured when trying to handle exception", ex);
		}
	}

}
