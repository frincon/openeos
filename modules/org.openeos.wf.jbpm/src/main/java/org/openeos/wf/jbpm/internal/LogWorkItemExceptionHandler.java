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

import java.util.Map;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogWorkItemExceptionHandler implements WorkItemExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(LogWorkItemExceptionHandler.class);

	@Override
	public void handleExecuteException(Exception exception, WorkItem workItem, WorkItemManager workItemManager) {
		StringBuilder builder = new StringBuilder("An exception has occured when trying to EXECUTE a work item.\n");
		appendData(builder, workItem);
		LOG.warn(builder.toString(), exception);
	}

	@Override
	public void handleAbortException(Exception exception, WorkItem workItem, WorkItemManager workItemManager) {
		StringBuilder builder = new StringBuilder("An exception has occured when trying to ABORT a work item.\n");
		appendData(builder, workItem);
		LOG.warn(builder.toString(), exception);
	}

	private void appendData(StringBuilder builder, WorkItem workItem) {
		builder.append("\tWorkItem.id:    ").append(workItem.getId()).append("\n");
		builder.append("\tWorkItem.name:  ").append(workItem.getName()).append("\n");
		builder.append("\tWorkItem.state: ").append(workItem.getState()).append("\n");
		builder.append("\tWorkItem.params:\n");
		Map<String, Object> params = workItem.getParameters();
		for (String key : params.keySet()) {
			builder.append("\t\t").append(key).append(" -> ").append(params.get(key)).append("\n");
		}
	}

}
