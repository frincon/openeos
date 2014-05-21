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
package org.openeos.erp.core.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Filter;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.erp.core.model.Client;
import org.openeos.erp.core.model.Organization;
import org.openeos.hibernate.AbstractFilterProvider;
import org.openeos.hibernate.BasicFilterDefinition;
import org.openeos.hibernate.SessionObserver;
import org.openeos.services.ui.SessionObjectsService;

public class ClientFilterProvider extends AbstractFilterProvider implements SessionObserver {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ClientFilterProvider.class);

	public static final String CLIENT_FILTER_NAME = "org_openeos_erp_core_filter_CLIENT";
	public static final String CLIENT_ORGANIZATION_FILTER_NAME = "org_openeos_erp_core_filter_CLIENT_ORGANIZATION";

	private SessionObjectsService sessionObjectsService;

	public ClientFilterProvider(BasicFilterDefinition[] basicFilterDefinitionList, SessionObjectsService sessionObjectsService) {
		super(basicFilterDefinitionList);
		this.sessionObjectsService = sessionObjectsService;
	}

	@Override
	public void addFilterToClassIfNecesary(PersistentClass persistentClass, BasicFilterDefinition filterDef) {
		if (filterDef.getName().equals(CLIENT_FILTER_NAME)) {
			if (hasProperty(persistentClass, "client", Client.class)) {
				// Assume than all client properties has the column ad_client_id
				LOG.debug("Adding filter 'org.openeos.erp.core.filter.CLIENT' to class '{}'", persistentClass.getClassName());
				persistentClass.addFilter(CLIENT_FILTER_NAME, filterDef.getDefaultCondition(), true,
						Collections.<String, String> emptyMap(), Collections.<String, String> emptyMap());
			} else if (persistentClass.getClassName().equals(Client.class.getName())) {
				LOG.debug("Adding filter 'org.openeos.erp.core.filter.CLIENT' to class '{}'", persistentClass.getClassName());
				persistentClass.addFilter(CLIENT_FILTER_NAME, filterDef.getDefaultCondition(), true,
						Collections.<String, String> emptyMap(), Collections.<String, String> emptyMap());
			}
		} else if (filterDef.getName().equals(CLIENT_ORGANIZATION_FILTER_NAME)) {
			if (hasProperty(persistentClass, "organization", Organization.class)) {
				LOG.debug("Adding filter 'org.openeos.erp.core.filter.CLIENT_ORGANIZATION' to class '{}'",
						persistentClass.getClassName());
				persistentClass.addFilter(CLIENT_ORGANIZATION_FILTER_NAME, filterDef.getDefaultCondition(), true,
						Collections.<String, String> emptyMap(), Collections.<String, String> emptyMap());

			} else if (persistentClass.getClassName().equals(Organization.class.getName())) {
				LOG.debug("Adding filter 'org.openeos.erp.core.filter.CLIENT_ORGANIZATION' to class '{}'",
						persistentClass.getClassName());
				persistentClass.addFilter(CLIENT_ORGANIZATION_FILTER_NAME, filterDef.getDefaultCondition(), true,
						Collections.<String, String> emptyMap(), Collections.<String, String> emptyMap());
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private boolean hasProperty(PersistentClass persistentClass, String propertyName, Class<?> propertyClass) {
		try {
			Property property = persistentClass.getProperty(propertyName);
			return property.getType().getReturnedClass().isAssignableFrom(propertyClass);
		} catch (MappingException ex) {
			return false;
		}
	}

	@Override
	public void sessionCreated(Session session) {
		LOG.debug("New session was created, enabling client filter by default");
		enableFilter(session);
	}

	public void disableFilter(Session session) {
		session.disableFilter(CLIENT_FILTER_NAME);
		session.disableFilter(CLIENT_ORGANIZATION_FILTER_NAME);
	}

	public void enableFilter(Session session) {
		Client client = (Client) sessionObjectsService.getSessionObject(ClientContextObjectContributor.CONTEXT_OBJECT_CLIENT_NAME);
		if (client != null) {
			LOG.debug("New session created, enabling filter for client selection");
			Filter filter = session.enableFilter(CLIENT_FILTER_NAME);
			filter.setParameter("client", client.getId());
			List<String> organizationList = new ArrayList<String>();
			for (Organization org : ((Client) session.get(Client.class, client.getId())).getOrganizations()) {
				organizationList.add(org.getId());
			}
			filter = session.enableFilter(CLIENT_ORGANIZATION_FILTER_NAME);
			if (organizationList.size() == 0) {
				organizationList.add("NULL_EXPECT_NEVER_HAS_THIS_KEY_BECAUSE_THE_STRING_HAS_MORE_LEN THAN THE PERMITTED_KEY");
			}
			filter.setParameterList("organizationList", organizationList);

		} else {
			LOG.debug("Client filter not enabling because client is not established");
		}

	}

	@Override
	public void sessionClosed(Session session) {
		// No-op
	}

}
