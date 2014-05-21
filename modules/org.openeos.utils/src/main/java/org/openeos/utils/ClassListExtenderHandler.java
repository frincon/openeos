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
package org.openeos.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClassListExtenderHandler<T> implements ExtenderHeaderHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ClassListExtenderHandler.class);

	@Override
	public void onBundleArrival(Bundle bundle, String header) {
		String[] classes = header.split(",");
		for (int i = 0; i < classes.length; i++) {
			String className = classes[i];
			try {
				Class<T> objectClass = (Class<T>) bundle.loadClass(className);
				if (getExpectedClass().isAssignableFrom(objectClass)) {
					T objectCreated = buildObject(objectClass);
					processObject(bundle, objectCreated);
				} else {
					LOG.warn(
							"The class '{0}' put in the header '{1}' of bundle '{2}' is not of assignable to the expected class '{3}'",
							new Object[] { objectClass.getName(), getHeaderName(), bundle.getSymbolicName() }, getExpectedClass()
									.getName());
				}
			} catch (Exception ex) {
				LOG.warn("The construction of object in a extender has thown an exception " + className, ex);
			}
		}
	}

	protected abstract void processObject(Bundle bundle, T objectCreated);

	protected abstract Class<T> getExpectedClass();

	protected T buildObject(Class<T> objectClass) throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<T> defaultConstructor = objectClass.getConstructor();
		T ret = defaultConstructor.newInstance();
		return ret;
	}

}
