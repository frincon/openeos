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
package org.openeos.services.ui.form.abstractform;

import java.util.EnumSet;
import java.util.Hashtable;

import org.abstractform.binding.BForm;
import org.openeos.services.ui.form.BindingFormCapability;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class AbstractFormUtils {

	public static final String SERVICE_PREFIX = AbstractFormUtils.class.getPackage().getName() + ".service.";
	public static final String SERVICE_BEAN_CLASS = SERVICE_PREFIX + "BEAN_CLASS";
	public static final String SERVICE_CAPABILITIES = SERVICE_PREFIX + "CAPABILITIES";

	public static final String SERVICE_UIBEAN_BINDING_PID = SERVICE_PREFIX + "SERVICE_UIBEAN_BINDING_PID";
	public static final String SERVICE_FIELD_FACTORY_PID = SERVICE_PREFIX + "SERVICE_FIELD_FACTORY_PID";

	private AbstractFormUtils() {
		// For not instantiating
	}

	@SuppressWarnings("rawtypes")
	public static <S> ServiceRegistration<BForm> registerBForm(BundleContext context, BForm<?, ? extends S> form,
			Class<? extends S> beanClass, EnumSet<BindingFormCapability> capabilities, Integer serviceRanking) {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_RANKING, serviceRanking);
		properties.put(SERVICE_BEAN_CLASS, beanClass);
		properties.put(SERVICE_CAPABILITIES, capabilities);
		return context.registerService(BForm.class, form, properties);
	}
}
