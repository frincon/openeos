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
package org.openeos.services.ui;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface UIBean {

	public Object get(String propertyName);

	public void set(String propertyName, Object value);

	public void addPropertyChangeListener(PropertyChangeListener listener);

	public void removePropertyChangeListener(PropertyChangeListener listener);

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	public void addUIBeanListener(UIBeanListener uiBeanListener);

	public void removeUIBeanListener(UIBeanListener uiBeanListener);

	public boolean isSameIdentity(UIBean otherUIBean);

	public Class<?> getType(String propertyName);

	public List<String> getPropertyNames();

	public boolean isNew();

	public Class<?> getModelClass();

}
