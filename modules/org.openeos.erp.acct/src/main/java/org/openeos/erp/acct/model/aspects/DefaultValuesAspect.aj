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
package org.openeos.erp.acct.model.aspects;

import org.openeos.services.dictionary.DefaultValue;

import org.openeos.erp.acct.model.FinancialMgmtAccountingCombination;
import org.openeos.erp.acct.model.FinancialMgmtAcctSchema;
import org.openeos.erp.acct.model.FinancialMgmtCalendar;
import org.openeos.erp.acct.model.FinancialMgmtElement;
import org.openeos.erp.acct.model.FinancialMgmtGLBatch;
import org.openeos.erp.acct.model.FinancialMgmtGLCategory;
import org.openeos.erp.acct.model.FinancialMgmtGLJournal;

public aspect DefaultValuesAspect {

	declare @method: public * FinancialMgmtAccountingCombination.getOrganization() : @DefaultValue("contextObject('Organization')");

	declare @method: public * FinancialMgmtAcctSchema.getClient() : @DefaultValue("contextObject('Client')");
	declare @method: public * FinancialMgmtAcctSchema.getOrganization() : @DefaultValue("contextObject('Organization')");

	declare @method: public * FinancialMgmtCalendar.getClient() : @DefaultValue("contextObject('Client')");
	declare @method: public * FinancialMgmtCalendar.getOrganization() : @DefaultValue("contextObject('Organization')");

	declare @method: public * FinancialMgmtElement.getClient() : @DefaultValue("contextObject('Client')");
	declare @method: public * FinancialMgmtElement.getOrganization() : @DefaultValue("contextObject('Organization')");

	declare @method: public * FinancialMgmtGLBatch.getOrganization() : @DefaultValue("contextObject('Organization')");

	declare @method: public * FinancialMgmtGLCategory.getClient() : @DefaultValue("contextObject('Client')");
	declare @method: public * FinancialMgmtGLCategory.getOrganization() : @DefaultValue("contextObject('Organization')");

	declare @method: public * FinancialMgmtGLJournal.getOrganization() : @DefaultValue("contextObject('Organization')");
	declare @method: public * FinancialMgmtGLJournal.getTotalCR() : @DefaultValue("java.math.BigDecimal.ZERO");
	declare @method: public * FinancialMgmtGLJournal.getTotalDR() : @DefaultValue("java.math.BigDecimal.ZERO");
	declare @method: public * FinancialMgmtGLJournal.isDraft() : @DefaultValue("true");

}
