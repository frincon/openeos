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

import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.exception.ConstraintViolationException;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.dictionary.model.IPropertyDefinition;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.UIContext;
import org.openeos.services.ui.UIDAOCoinstraintValidationException;
import org.openeos.services.ui.UIDAOService;

public class UIDAOServiceImpl implements UIDAOService {

	private static final Logger LOG = LoggerFactory.getLogger(UIDAOServiceImpl.class);

	private SessionFactory sessionFactory;
	private IDictionaryService dictionaryService;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setDictionaryService(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UIBean> list(Class<?> persistentClass, Order... orders) {
		Session ses = getSession();
		Criteria crit = ses.createCriteria(persistentClass);
		IClassDefinition classDef = dictionaryService.getClassDefinition(persistentClass);

		for (Order order : orders) {
			crit.addOrder(order);
		}
		crit.addOrder(Order.asc(classDef.getIdPropertyDefinition().getName()));
		List<UIBean> result = new ArrayList<UIBean>();
		for (Object obj : crit.list()) {
			Hibernate.initialize(obj);
			result.add(new UIBeanImpl(obj, classDef));
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public UIBean create(Class<?> persistentClass, UIContext context) {
		try {
			Object newObject = persistentClass.newInstance();
			establishDefaultValues(newObject, context, dictionaryService.getClassDefinition(persistentClass));
			return new UIBeanImpl(newObject, dictionaryService.getClassDefinition(persistentClass));
		} catch (InstantiationException e) {
			//TODO Change to UIException
			throw new UndeclaredThrowableException(e);
		} catch (IllegalAccessException e) {
			//TODO Change to UIException
			throw new UndeclaredThrowableException(e);
		}
	}

	private void establishDefaultValues(Object newObject, UIContext context, IClassDefinition classDefinition) {
		for (IPropertyDefinition prop : classDefinition.getAllPropertyDefinitionList()) {
			if (prop.getDefaultValue() != null) {
				try {
					Object defaultValue = MVEL.eval(prop.getDefaultValue(), context);
					if (defaultValue != null) {
						prop.set(newObject, defaultValue);
						LOG.debug("Estabished default value to property '{}' of object of class '{}'", prop.getName(),
								newObject.getClass());
					}
				} catch (Exception ex) {
					LOG.warn(MessageFormat.format("The default value for property '{0}' of object of class '{1}' "
							+ "can't be processed because MVEL thorw a exception.", prop.getName(), newObject.getClass()), ex);
				}
			}
		}

	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { UIDAOCoinstraintValidationException.class })
	public void save(UIBean bean) throws UIDAOCoinstraintValidationException {
		if (bean instanceof UIBeanImpl) {
			UIBeanImpl uiBeanI = (UIBeanImpl) bean;
			Session ses = getSession();
			Object id = dictionaryService.getClassDefinition(uiBeanI.getBeanWrapped().getClass()).getIdPropertyDefinition()
					.get(uiBeanI.getBeanWrapped());
			try {
				ses.saveOrUpdate(uiBeanI.getBeanWrapped());
				ses.flush();
				uiBeanI.fireOnSave();
			} catch (HibernateException ex) {
				// See https://hibernate.atlassian.net/browse/HB-1014
				dictionaryService.getClassDefinition(uiBeanI.getBeanWrapped().getClass()).getIdPropertyDefinition()
						.set(uiBeanI.getBeanWrapped(), id);
				if (ex instanceof ConstraintViolationException) {
					ConstraintViolationException constraintException = (ConstraintViolationException) ex;
					throw new UIDAOCoinstraintValidationException(constraintException);
				}
				throw ex;
			}
		} else {
			throw new UnsupportedOperationException("Don't recognice bean passes as argument, must be UIBeanImpl class.");
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(UIBean bean) {
		if (bean instanceof UIBeanImpl) {
			UIBeanImpl uiBeanI = (UIBeanImpl) bean;
			Session ses = getSession();
			ses.delete(ses.get(uiBeanI.getModelClass(), (Serializable) dictionaryService
					.getClassDefinition(uiBeanI.getModelClass()).getId(uiBeanI.getBeanWrapped())));
			ses.flush();
		} else {
			throw new UnsupportedOperationException("Don't recognice bean passes as argument, must be UIBeanImpl class.");
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAll(Collection<UIBean> uiBeanCollection) {
		for (UIBean bean : uiBeanCollection) {
			delete(bean);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public List<UIBean> list(Class<?> persistentClass, int startIndex, int count, Order... orders) {
		Session ses = getSession();
		Criteria crit = ses.createCriteria(persistentClass);
		IClassDefinition classDef = dictionaryService.getClassDefinition(persistentClass);

		for (Order order : orders) {
			crit.addOrder(order);
		}
		crit.addOrder(Order.asc(classDef.getIdPropertyDefinition().getName()));
		crit.setFirstResult(startIndex);
		crit.setMaxResults(count);
		List<UIBean> result = new ArrayList<UIBean>();
		for (Object obj : crit.list()) {
			Hibernate.initialize(obj);
			result.add(new UIBeanImpl(obj, classDef));
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	@Transactional(readOnly = false)
	public int count(Class<?> persistentClass) {
		Session ses = getSession();
		Criteria crit = ses.createCriteria(persistentClass).setProjection(Projections.rowCount());
		return ((Long) crit.uniqueResult()).intValue();
	}

	@Override
	public <T> UIBean wrap(Class<T> persistentClass, T model) {
		IClassDefinition def = dictionaryService.getClassDefinition(persistentClass);
		if (def == null || !(persistentClass.isAssignableFrom(model.getClass()))) {
			throw new IllegalArgumentException();
		}
		return new UIBeanImpl(model, def);
	}

	@Override
	@Transactional(readOnly = false)
	public void refresh(UIBean bean) {
		Object entity = ((UIBeanImpl) bean).getBeanWrapped();
		if (getSession().contains(entity)) {
			getSession().refresh(entity);
		}
	}
}
