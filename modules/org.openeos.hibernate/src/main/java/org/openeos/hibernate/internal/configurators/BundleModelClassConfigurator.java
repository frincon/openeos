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
package org.openeos.hibernate.internal.configurators;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.usertype.UserType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.dao.ListType;
import org.openeos.hibernate.internal.ConfigurationProvider;

public class BundleModelClassConfigurator implements Configurator {

	private static final Logger LOG = LoggerFactory.getLogger(BundleModelClassConfigurator.class);

	private static final String MODEL_CLASS_HEADER = "Uno-Model-Classes";

	private BundleContext context;
	private Set<Class<?>> listTypeProxyCreatedSet = new HashSet<Class<?>>();

	public BundleModelClassConfigurator(BundleContext context) {
		this.context = context;
	}

	@Override
	public void init(ConfigurationProvider configurationProvider) {
		LOG.debug("Initializing configuration of model class bundles");
		Configuration conf = configurationProvider.getConfiguration();
		registerModelBundles(conf);
		configurationProvider.invalidate();
	}

	private void registerModelBundles(Configuration conf) {
		for (Bundle bundle : context.getBundles()) {
			LOG.trace(String.format("Checking if bundle '%s' has model classes", bundle.getSymbolicName()));
			if (hasModel(bundle)
					&& (bundle.getState() & (Bundle.INSTALLED | Bundle.ACTIVE | Bundle.RESOLVED | Bundle.STARTING | Bundle.STOPPING)) != 0) {
				registerBundle(conf, bundle);
			}
		}
	}

	private void registerBundle(Configuration conf, Bundle bundle) {
		String header = bundle.getHeaders().get(MODEL_CLASS_HEADER);
		for (String clazz : header.split(",")) {
			try {
				LOG.debug(String.format("Trying to register model class '%s'", clazz.trim()));
				Class<?> classToRegister = bundle.loadClass(clazz.trim());
				conf.addAnnotatedClass(classToRegister);
				checkForListType(conf, classToRegister);
			} catch (ClassNotFoundException ex) {
				LOG.error(String.format("Class not found exception when trying to register model class '%s' for bundle '%s'",
						clazz, bundle.getSymbolicName()), ex);
			}
		}
	}

	private void checkForListType(Configuration conf, Class<?> persistentClass) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(persistentClass);
			PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < props.length; i++) {
				PropertyDescriptor prop = props[i];
				if (ListType.class.isAssignableFrom(prop.getPropertyType())) {
					checkForCreateListTypeType(conf, prop.getPropertyType());
				}
			}
		} catch (Exception e) {
			LOG.error(String.format("An exception occured when trying to get bean info of class %s", persistentClass.toString()), e);
		}
	}

	private void checkForCreateListTypeType(Configuration conf, final Class<?> propertyType) throws NoSuchMethodException,
			IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if (!listTypeProxyCreatedSet.contains(propertyType)) {
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass(ListTypeUserType.class);
			factory.setFilter(new MethodFilter() {

				@Override
				public boolean isHandled(Method method) {
					return Modifier.isAbstract(method.getModifiers());
				}
			});
			MethodHandler handler = new MethodHandler() {

				@Override
				public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
					if (thisMethod.getName().equals("returnedClass") && args.length == 0) {
						LOG.debug("Handling method returnedClass() of type ListTypeUserType", thisMethod);
						return propertyType;
					} else {
						throw new UnsupportedOperationException();
					}
				}

			};
			Object type = factory.create(new Class<?>[0], new Object[0], handler);
			conf.registerTypeOverride((UserType) type, new String[] { propertyType.getSimpleName(), propertyType.getName() });
			listTypeProxyCreatedSet.add(propertyType);
		}
	}

	private boolean hasModel(Bundle bundle) {
		return bundle.getHeaders().get(MODEL_CLASS_HEADER) != null;
	}

}
