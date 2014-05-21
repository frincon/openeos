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
package org.openeos.wf.internal.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.hibernate.GenericDAOHibernateImpl;
import org.openeos.wf.dao.DeploymentDAO;
import org.openeos.wf.model.Deployment;

public class DeploymentDAOHibernateImpl extends GenericDAOHibernateImpl<Deployment, String> implements DeploymentDAO {

	public DeploymentDAOHibernateImpl() {
		super(Deployment.class);
	}

	@Override
	@Transactional
	public Deployment findDeploymentByValueVersion(String value, String version) {
		Criteria crit = getSession().createCriteria(Deployment.class);
		crit.add(Restrictions.eq(Deployment.PROPERTY_VALUE, value));
		crit.add(Restrictions.eq(Deployment.PROPERTY_VERSION, version));
		return (Deployment) crit.uniqueResult();
	}

}
