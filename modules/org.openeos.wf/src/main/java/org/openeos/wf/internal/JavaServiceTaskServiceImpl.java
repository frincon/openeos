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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.wf.JavaServiceTaskException;
import org.openeos.wf.JavaServiceTaskService;

public class JavaServiceTaskServiceImpl implements JavaServiceTaskService {

	private static final Logger LOG = LoggerFactory.getLogger(JavaServiceTaskServiceImpl.class);

	private Map<String, Object> mapServices = new HashMap<String, Object>();

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable, U> T callService(String serviceName, String methodName, U parameter,
			Class<T> expectedResultClass) throws JavaServiceTaskException {
		return (T) callService(serviceName, methodName, parameter);
	}

	@Override
	public Serializable callService(String serviceName, String methodName, Object parameter) throws JavaServiceTaskException {
		Object service = mapServices.get(serviceName);
		if (service == null) {
			throw new JavaServiceTaskException(String.format("Service whith name '%s' not found in registry", serviceName));
		}
		Method method = findMethod(service, methodName, parameter);
		if (method == null) {
			throw new JavaServiceTaskException(String.format(
					"No suitable method found in service '%s' whith name '%s' and parameter compatible with '%s'", serviceName,
					methodName, parameter.getClass()));
		}
		Object obj;
		try {
			obj = invokeMethod(method, service, parameter);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new JavaServiceTaskException(String.format("An exception has thrown when invoking method '%s' of service '%s'",
					methodName, serviceName), e);
		}
		if (obj == null) {
			return null;
		} else if (obj instanceof Serializable) {
			return (Serializable) obj;
		} else {
			LOG.warn(String.format("The return value of service '%s' and method '%s' not implements Serializable.", serviceName,
					methodName));
			return null;
		}
	}

	private Object invokeMethod(Method method, Object service, Object parameter) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		if (method.getParameterTypes().length > 1) {
			return method.invoke(service, (Object[]) parameter);
		} else {
			return method.invoke(service, parameter);
		}
	}

	private Method findMethod(Object service, String methodName, Object parameter) {
		Method[] methods = service.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				Class<?>[] parameterTypes = methods[i].getParameterTypes();
				if (checkMethod(parameterTypes, parameter)) {
					return methods[i];
				}
			}
		}
		return null;
	}

	private boolean checkMethod(Class<?>[] parameterTypes, Object parameter) {
		if (parameter.getClass().isArray()) {
			LOG.debug("The parameter is an array, try to find method with the arguments of parameters as separated objects");
			Object[] parameters = (Object[]) parameter;
			if (parameterTypes.length == parameters.length) {
				boolean found = true;
				for (int i = 0; i < parameterTypes.length; i++) {
					if (!parameterTypes[i].isInstance(parameters[i])) {
						found = false;
						break;
					}
				}
				if (found) {
					return true;
				}
			}
		}
		return parameterTypes.length == 1 && parameterTypes[0].isInstance(parameter);

	}

	@Override
	public void registerService(String serviceName, Object service) {
		mapServices.put(serviceName, service);
	}

	@Override
	public void unregisterService(String serviceName) {
		mapServices.remove(serviceName);
	}

}
