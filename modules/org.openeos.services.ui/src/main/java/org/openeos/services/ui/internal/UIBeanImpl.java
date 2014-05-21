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
package org.openeos.services.ui.internal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.dictionary.model.IPropertyDefinition;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.UIBeanEvent;
import org.openeos.services.ui.UIBeanListener;

public class UIBeanImpl implements UIBean {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private Set<UIBeanListener> uiBeanListenerSet = new CopyOnWriteArraySet<UIBeanListener>();

	private Object beanWrapped;
	private IClassDefinition classDefinition;

	public UIBeanImpl(Object beanWrapped, IClassDefinition classDefinition) {
		this.beanWrapped = beanWrapped;
		this.classDefinition = classDefinition;
	}

	public Object getBeanWrapped() {
		return beanWrapped;
	}

	@Override
	public Object get(String propertyName) {
		return classDefinition.getPropertyDefinition(propertyName).get(beanWrapped);
	}

	@Override
	public void set(String propertyName, Object value) {
		Object old = get(propertyName);
		classDefinition.getPropertyDefinition(propertyName).set(beanWrapped, value);
		propertyChangeSupport.firePropertyChange(propertyName, old, value);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public boolean isSameIdentity(UIBean otherUIBean) {
		if (classDefinition.getId(beanWrapped) != null && otherUIBean != null && otherUIBean instanceof UIBeanImpl) {
			return classDefinition.getId(beanWrapped).equals(classDefinition.getId(((UIBeanImpl) otherUIBean).getBeanWrapped()));
		}
		return false;
	}

	@Override
	public Class<?> getType(String propertyName) {
		return classDefinition.getPropertyDefinition(propertyName).getPropertyClass();
	}

	@Override
	public List<String> getPropertyNames() {
		List<String> propertyNames = new ArrayList<String>(classDefinition.getAllPropertyDefinitionList().size());
		for (IPropertyDefinition propDef : classDefinition.getAllPropertyDefinitionList()) {
			propertyNames.add(propDef.getName());
		}
		return propertyNames;
	}

	@Override
	public boolean isNew() {
		return classDefinition.getId(beanWrapped) == null;

	}

	@Override
	public void addUIBeanListener(UIBeanListener uiBeanListener) {
		uiBeanListenerSet.add(uiBeanListener);
	}

	@Override
	public void removeUIBeanListener(UIBeanListener uiBeanListener) {
		uiBeanListenerSet.remove(uiBeanListener);
	}

	public void fireOnSave() {
		UIBeanEvent event = new UIBeanEvent(this);
		for (UIBeanListener listener : uiBeanListenerSet) {
			listener.onSave(event);
		}
	}

	@Override
	public Class<?> getModelClass() {
		return classDefinition.getClassDefined();
	}

	public static Object unwrap(UIBean bean) {
		if (bean instanceof UIBeanImpl) {
			UIBeanImpl uiBean = (UIBeanImpl) bean;
			return uiBean.getBeanWrapped();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public static List<Object> unwrap(List<UIBean> beanList) {
		return new UIBeanUnwrapedList(beanList);
	}

	private static class UIBeanUnwrapedList extends AbstractList<Object> {
		private List<UIBean> wrappedList;

		UIBeanUnwrapedList(List<UIBean> wrappedList) {
			this.wrappedList = wrappedList;
		}

		@Override
		public Object get(int index) {
			return unwrap(wrappedList.get(index));
		}

		@Override
		public int size() {
			return wrappedList.size();
		}
	}

}