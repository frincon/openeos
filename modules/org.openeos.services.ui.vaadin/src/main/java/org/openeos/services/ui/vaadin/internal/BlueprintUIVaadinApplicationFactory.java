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
package org.openeos.services.ui.vaadin.internal;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.osgi.service.blueprint.container.BlueprintContainer;

import org.openeos.services.ui.UIApplication;
import org.openeos.vaadin.main.IUnoVaadinApplication;

public class BlueprintUIVaadinApplicationFactory implements UIVaadinApplicationFactory {

	private BlueprintContainer blueprintContainer;
	private String beanName;
	private String vaadinApplicationPropertyName;

	public BlueprintUIVaadinApplicationFactory(BlueprintContainer blueprintContainer, String beanName,
			String vaadinApplicationPropertyName) {
		this.blueprintContainer = blueprintContainer;
		this.beanName = beanName;
		this.vaadinApplicationPropertyName = vaadinApplicationPropertyName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UIApplication<IUnoVaadinApplication> createApplication(IUnoVaadinApplication application) {
		UIApplication<IUnoVaadinApplication> result = (UIApplication<IUnoVaadinApplication>) blueprintContainer
				.getComponentInstance(beanName);
		if (vaadinApplicationPropertyName != null) {
			try {
				PropertyUtils.setProperty(result, vaadinApplicationPropertyName, application);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
}
