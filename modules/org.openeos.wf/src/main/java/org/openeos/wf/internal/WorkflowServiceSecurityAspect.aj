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
package org.openeos.wf.internal;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.openeos.wf.Constants;
import org.openeos.wf.WorkflowService;

public aspect WorkflowServiceSecurityAspect {

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowServiceSecurityAspect.class);

	String around(WorkflowService service, String processId) : execution(public String WorkflowService.startProcess(String))
			&& args(processId) && this(service){
		String user = getContextUser();
		if (user != null) {
			LOG.debug("Adding parameter {} whith value {} to start process", Constants.LANUCHER_USER_PARAMETER, user);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(Constants.LANUCHER_USER_PARAMETER, user);
			return service.startProcess(processId, parameters);
		} else {
			LOG.debug("No user found, proceed without parameter {}", Constants.LANUCHER_USER_PARAMETER);
			return proceed(service, processId);
		}
	}

	String around(WorkflowService service, String processId, Map<String, Object> originalParameters) : 
		execution(public String WorkflowService.startProcess(String, Map<String, Object>)) && args(processId, originalParameters) && this(service){
		if (originalParameters.containsKey(Constants.LANUCHER_USER_PARAMETER)) {
			return proceed(service, processId, originalParameters);
		}
		String user = getContextUser();
		if (user != null) {
			LOG.debug("Adding parameter {} whith value {} to start process", Constants.LANUCHER_USER_PARAMETER, user);
			Map<String, Object> parameters = new HashMap<String, Object>(originalParameters);
			parameters.put(Constants.LANUCHER_USER_PARAMETER, user);
			return proceed(service, processId, parameters);
		} else {
			LOG.debug("No user found, proceed without parameter {}", Constants.LANUCHER_USER_PARAMETER);
			return proceed(service, processId, originalParameters);
		}
	}

	private String getContextUser() {
		LOG.debug("Searching authentication in security context");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			LOG.debug("Authentication object found");
			Object principal = auth.getPrincipal();
			if (principal instanceof String) {
				return (String) principal;
			} else if (principal instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) principal;
				return userDetails.getUsername();
			} else {
				LOG.warn("Unkown class '{}' of principal in security context. The parameter in process will not be filled.",
						principal.getClass().toString());
			}
		}
		return null;
	}
}
