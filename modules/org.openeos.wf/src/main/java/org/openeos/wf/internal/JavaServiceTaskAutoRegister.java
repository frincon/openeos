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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import org.openeos.wf.JavaServiceTaskAware;
import org.openeos.wf.JavaServiceTaskService;

@SuppressWarnings("rawtypes")
public class JavaServiceTaskAutoRegister extends ServiceTracker<JavaServiceTaskAware, JavaServiceTaskAware> {

	private JavaServiceTaskService javaServiceTaskService;

	public JavaServiceTaskAutoRegister(BundleContext bundleContext, JavaServiceTaskService javaServiceTaskService) {
		super(bundleContext, JavaServiceTaskAware.class, null);
		this.javaServiceTaskService = javaServiceTaskService;
	}

	public void registerJavaServiceTaskAware(JavaServiceTaskAware javaServiceTaskAware) {
		javaServiceTaskService.registerService(javaServiceTaskAware.getName(), javaServiceTaskAware);
	}

	public void unregisterJavaServiceTaskAware(JavaServiceTaskAware<?> javaServiceTaskAware) {
		javaServiceTaskService.unregisterService(javaServiceTaskAware.getName());
	}

	@Override
	public JavaServiceTaskAware<?> addingService(ServiceReference<JavaServiceTaskAware> reference) {
		JavaServiceTaskAware<?> javaServiceTaskAware = super.addingService(reference);
		registerJavaServiceTaskAware(javaServiceTaskAware);
		return javaServiceTaskAware;
	}

	@Override
	public void removedService(ServiceReference<JavaServiceTaskAware> reference, JavaServiceTaskAware service) {
		unregisterJavaServiceTaskAware(service);
		super.removedService(reference, service);
	}

}
