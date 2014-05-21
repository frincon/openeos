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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.utils.ExtenderHandler;
import org.openeos.wf.Constants;
import org.openeos.wf.Deployment;
import org.openeos.wf.DeploymentBuilder;
import org.openeos.wf.WorkflowService;

public class WorkflowDefinitionExtenderHandler implements ExtenderHandler<Collection<URL>>, Constants {

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowDefinitionExtenderHandler.class);

	private WorkflowService workflowService;

	public WorkflowDefinitionExtenderHandler(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	@Override
	public void starting() {
		if (workflowService == null) {
			throw new IllegalStateException("Few services");
		}
	}

	@Override
	public void stopping() {
		// No-OP
	}

	@Override
	public void onBundleArrival(Bundle bundle, Collection<URL> object) {
		LOG.debug("Trying to deploy bundle {} with workflow definitions", bundle);
		// Check if this deployment exists and not is debug
		boolean debug = isDebugBundle(bundle);
		if (!debug && alreadyDeployed(bundle)) {
			LOG.debug("Ignoring bundle {} because the bundle already deployed", bundle);
			return;
		}

		DeploymentBuilder builder = workflowService.createDeployment().key(getKey(bundle)).version(getVersion(bundle, debug));
		for (URL url : object) {
			builder.addURL(url);
		}
		builder.deploy();

	}

	private boolean alreadyDeployed(Bundle bundle) {
		Deployment deployment = workflowService.getDeployment(getKey(bundle), getVersion(bundle, false));
		return deployment != null;
	}

	private boolean isDebugBundle(Bundle bundle) {
		String header = bundle.getHeaders().get(HEADER_WORKFLOW_DEBUG);
		if (header != null) {
			return Boolean.parseBoolean(header);
		}
		return false;
	}

	private String getKey(Bundle bundle) {
		return DEPLOYMENT_PREFIX + bundle.getSymbolicName();
	}

	private String getVersion(Bundle bundle, boolean debug) {
		StringBuilder buff = new StringBuilder(bundle.getVersion().toString());
		if (debug) {
			buff.append("-");
			buff.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		}
		return buff.toString();
	}

	@Override
	public void onBundleDeparture(Bundle bundle) {
		// TODO Think about what to do 
	}

}
