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

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.wf.JavaServiceTaskException;
import org.openeos.wf.JavaServiceTaskService;

public class JbpmJavaServiceTaskHandler implements WorkItemHandler {

	private static final Logger LOG = LoggerFactory.getLogger(JbpmJavaServiceTaskHandler.class);

	public static final String PARAMETER_SERVICE_NAME = "Interface";
	public static final String PARAMETER_METHOD_NAME = "Operation";
	public static final String PARAMETER_INPUT_PARAMS = "Parameter";
	public static final String PARAMETER_RESULT = "Result";

	private JavaServiceTaskService javaServiceTaskService;

	public JbpmJavaServiceTaskHandler(JavaServiceTaskService javaServiceTaskService) {
		this.javaServiceTaskService = javaServiceTaskService;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String serviceName = (String) workItem.getParameter(PARAMETER_SERVICE_NAME);
		String methodName = (String) workItem.getParameter(PARAMETER_METHOD_NAME);
		Object inputParams = workItem.getParameter(PARAMETER_INPUT_PARAMS);
		LOG.debug("Executing java service task whith this parameters: serviceName: '{}', methodName: '{}', inputParams: '{}'",
				new Object[] { serviceName, methodName, inputParams });
		try {
			Object result = javaServiceTaskService.callService(serviceName, methodName, inputParams);
			LOG.debug(" - The result: {}", result);
			Map<String, Object> results = new HashMap<String, Object>();
			results.put(PARAMETER_RESULT, result);
			manager.completeWorkItem(workItem.getId(), results);
		} catch (JavaServiceTaskException e) {
			LOG.error(String.format("An exception occured when trying to execute service task whith service name "
					+ "'%s' and method name '%s' whith input params '%s'. The work item has suspended", serviceName, methodName,
					inputParams), e);
		}
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// Do Nothing, cannot be aborted
	}

}
