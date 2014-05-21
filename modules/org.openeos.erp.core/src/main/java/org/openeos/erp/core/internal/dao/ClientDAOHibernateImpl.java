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
package org.openeos.erp.core.internal.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.hibernate.GenericDAOHibernateImpl;
import org.openeos.erp.core.dao.ClientDAO;
import org.openeos.erp.core.model.Client;

public class ClientDAOHibernateImpl extends GenericDAOHibernateImpl<Client, String> implements ClientDAO {

	public ClientDAOHibernateImpl() {
		super(Client.class);
	}

	@Override
	@Transactional
	public Client findClientBySearchKey(String searchKey) {
		Criteria crit = getSession().createCriteria(Client.class);
		crit.add(Restrictions.eq(Client.PROPERTY_VALUE, searchKey));
		return (Client) crit.uniqueResult();
	}

}
