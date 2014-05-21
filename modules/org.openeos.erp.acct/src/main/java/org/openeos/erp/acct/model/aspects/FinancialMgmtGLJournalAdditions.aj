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

import org.openeos.erp.acct.model.FinancialMgmtGLJournal;
import org.openeos.erp.document.TransactionDocument;
import org.openeos.services.dictionary.validation.annotations.SQLValidation;
import org.hibernate.annotations.Cascade;

public aspect FinancialMgmtGLJournalAdditions {

	declare parents: FinancialMgmtGLJournal implements TransactionDocument;

	declare @method: public * FinancialMgmtGLJournal.getDocumentType() : @SQLValidation("{alias}.targettype='org.openeos.erp.acct.model.FinancialMgmtGLJournal'");
	
	declare @method: public * FinancialMgmtGLJournal.getFinancialMgmtGLJournalLines() : @Cascade(org.hibernate.annotations.CascadeType.ALL);

}
