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
package org.openeos.jbpm.integration.internal;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnoPersistenceContextManager implements PersistenceContextManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(UnoPersistenceContextManager.class);
	
	public UnoPersistenceContext unoPersistenceContext;
	
	public UnoPersistenceContextManager(UnoPersistenceContext unoPersistenceContext) {
		this.unoPersistenceContext = unoPersistenceContext;
	}
	
	@Override
	public PersistenceContext getApplicationScopedPersistenceContext() {
		return unoPersistenceContext;
	}

	@Override
	public PersistenceContext getCommandScopedPersistenceContext() {
		return unoPersistenceContext;
	}

	@Override
	public void beginCommandScopedEntityManager() {
		LOG.debug("Called beginCommandScopedEntityManager()");
	}

	@Override
	public void endCommandScopedEntityManager() {
		LOG.debug("Called endCommandScopedEntityManager()");
		//unoPersistenceContext.saveAll();
	}

	@Override
	public void clearPersistenceContext() {
		LOG.debug("Called clearPersistenceContext()");
	}

	@Override
	public void dispose() {
		LOG.debug("Called dispose()");
	}

}
