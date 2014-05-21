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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.abstractform.binding.BFormInstance;
import org.abstractform.core.FormInstance;
import org.abstractform.core.selector.AbstractSelectorProvider;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BooleanType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.services.dictionary.IIdentificationCapable;
import org.openeos.services.ui.internal.UIBeanImpl;

public class UIBeanSelectorProvider<T> extends AbstractSelectorProvider<T> {

	private static final Logger LOG = LoggerFactory.getLogger(UIBeanSelectorProvider.class);

	private SessionFactory sessionFactory;
	private Class<T> beanClass;
	private FormInstance formInstance;
	private String sqlRestriction;

	public UIBeanSelectorProvider(SessionFactory sessionFactory, Class<T> beanClass, FormInstance formInstance,
			String sqlRestriction) {
		this.sessionFactory = sessionFactory;
		this.beanClass = beanClass;
		this.formInstance = formInstance;
		this.sqlRestriction = sqlRestriction;
		if (sqlRestriction != null && formInstance instanceof BFormInstance<?>) {

			this.formInstance.addFieldChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					// TODO This is not good idea, maybe cached query, check if query changed and fire if it changed. 
					fireElementsChanged();
				}
			});
		}
	}

	@Override
	@Transactional(readOnly = true)
	public SortedSet<T> getElements() {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(beanClass);
		if (sqlRestriction != null && formInstance instanceof BFormInstance<?>) {
			Object value = ((BFormInstance<?>) formInstance).getValue();
			if (value instanceof UIBeanImpl) {
				value = ((UIBeanImpl) value).getBeanWrapped();
			}
			crit.add(buildSqlRestriction(sqlRestriction, value));
		}
		final List<T> listElements = crit.list();
		TreeSet<T> result = new TreeSet<T>(new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return Integer.compare(listElements.indexOf(o1), listElements.indexOf(o2));
			}
		});
		result.addAll(listElements);
		return result;
	}

	private Criterion buildSqlRestriction(String sqlRestriction, Object value) {
		try {
			String result = sqlRestriction;
			int first = result.indexOf("@");
			List<Object> values = new ArrayList<Object>();
			List<Type> types = new ArrayList<Type>();
			while (first != -1) {
				String parameter = result.substring(first);
				int second = parameter.indexOf("@", 1);
				if (second == -1) {
					LOG.warn("There are a sql restrictoion not well formed, the sql restriction is: '{}' for bean class {} ",
							sqlRestriction, beanClass.getName());
					break;
				}
				parameter = parameter.substring(0, second + 1);
				String parameterWithoutRim = parameter.substring(1, parameter.length() - 1);
				Map<String, Object> mvelContext = new HashMap<String, Object>();
				mvelContext.put("source", value);
				Object parameterValue = MVEL.eval(parameterWithoutRim, mvelContext);
				// TODO Make more types
				if (String.class.isAssignableFrom(parameterValue.getClass())) {
					values.add(parameterValue);
					types.add(StringType.INSTANCE);
				} else if (Boolean.class.isAssignableFrom(parameterValue.getClass())) {
					values.add(parameterValue);
					types.add(BooleanType.INSTANCE);
				} else {
					throw new UnsupportedOperationException();
				}
				result = result.substring(0, first) + "? " + result.substring(second + first + 1);
				first = result.indexOf("@");
			}
			Type[] t = new Type[types.size()];
			return Restrictions.sqlRestriction(result, values.toArray(), types.toArray(t));
		} catch (CompileException ex) {
			LOG.warn(String.format("Compiling validation expression '%s' of class '%s' has thrown an error: %s", sqlRestriction,
					beanClass.toString(), ex.getMessage()), ex);
			return Restrictions.sqlRestriction("0=1");
		}
	}

	@Override
	public String getText(T element) {
		if (element instanceof IIdentificationCapable) {
			IIdentificationCapable identif = (IIdentificationCapable) element;
			return identif.getIdentifier();
		}
		return element.toString();
	}

}
