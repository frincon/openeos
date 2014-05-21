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
import org.jbpm.workflow.instance.WorkflowRuntimeException;

public class CheckExceptionHandler implements WorkItemExceptionHandler {

	@Override
	public void handleExecuteException(Exception exception, WorkItem workItem, WorkItemManager workItemManager) {
		checkException(exception);
	}

	@Override
	public void handleAbortException(Exception exception, WorkItem workItem, WorkItemManager workItemManager) {
		checkException(exception);
	}

	private void checkException(Exception exception) {
		if (exception instanceof WorkflowRuntimeException) {
			WorkflowRuntimeException wre = (WorkflowRuntimeException) exception;
			throw wre;
		}
	}

}
