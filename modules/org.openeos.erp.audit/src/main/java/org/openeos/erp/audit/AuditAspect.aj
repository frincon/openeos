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
package org.openeos.erp.audit;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

privileged  aspect AuditAspect {

	public interface Auditable {
	}

	private Audit Auditable.audit;

	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	public Audit Auditable.getAudit() {
		return audit;
	}

	public void Auditable.setAudit(Audit audit) {
		this.audit = audit;
	}

	declare parents : (@Entity * && !Audit) implements Auditable;

}
