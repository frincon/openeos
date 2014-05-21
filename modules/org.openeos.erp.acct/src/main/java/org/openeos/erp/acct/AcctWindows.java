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
package org.openeos.erp.acct;

import org.openeos.erp.acct.model.FinancialMgmtAccountingCombination;
import org.openeos.erp.acct.model.FinancialMgmtAcctSchema;
import org.openeos.erp.acct.model.FinancialMgmtElement;
import org.openeos.erp.acct.model.FinancialMgmtGLCategory;
import org.openeos.erp.acct.model.FinancialMgmtGLJournal;
import org.openeos.services.ui.window.AbstractDictionaryBasedWindowDefinition;

public class AcctWindows {

	public static class AccountTreeWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = AccountTreeWindow.class.getName();

		public AccountTreeWindow() {
			super(ID, FinancialMgmtElement.class);
			setName("Account Tree");
			addVisibleField(FinancialMgmtElement.PROPERTY_ORGANIZATION);
			addVisibleField(FinancialMgmtElement.PROPERTY_NAME);
			addVisibleField(FinancialMgmtElement.PROPERTY_ELEMENT_TYPE);
			addVisibleField(FinancialMgmtElement.PROPERTY_DESCRIPTION);
			addOrderAsc(FinancialMgmtElement.PROPERTY_NAME);
		}

	}

	public static class GeneralLedgerConfigurationWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = GeneralLedgerConfigurationWindow.class.getName();

		public GeneralLedgerConfigurationWindow() {
			super(ID, FinancialMgmtAcctSchema.class);
			setName("General Ledger Configuration");
			addVisibleField(FinancialMgmtAcctSchema.PROPERTY_ORGANIZATION);
			addVisibleField(FinancialMgmtAcctSchema.PROPERTY_NAME);
			addVisibleField(FinancialMgmtAcctSchema.PROPERTY_DESCRIPTION);
			addOrderAsc(FinancialMgmtAcctSchema.PROPERTY_NAME);
		}

	}

	public static class AccountCombinationWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = AccountCombinationWindow.class.getName();

		public AccountCombinationWindow() {
			super(ID, FinancialMgmtAccountingCombination.class);
			setName("Valid Account Combinations");
			addVisibleField(FinancialMgmtAccountingCombination.PROPERTY_ORGANIZATION);
			addVisibleField(FinancialMgmtAccountingCombination.PROPERTY_ALIAS);
			addVisibleField(FinancialMgmtAccountingCombination.PROPERTY_DESCRIPTION);
			addVisibleField(FinancialMgmtAccountingCombination.PROPERTY_FINANCIAL_MGMT_ACCT_SCHEMA);
			addVisibleField(FinancialMgmtAccountingCombination.PROPERTY_FINANCIAL_MGMT_ELEMENT_VALUE);
			addOrderAsc(FinancialMgmtAccountingCombination.PROPERTY_ORGANIZATION);
			addOrderAsc(FinancialMgmtAccountingCombination.PROPERTY_ALIAS);
		}

	}

	public static class GeneralLedgerCategoryWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = GeneralLedgerCategoryWindow.class.getName();

		public GeneralLedgerCategoryWindow() {
			super(ID, FinancialMgmtGLCategory.class);
			setName("G/L Category");
			addVisibleField(FinancialMgmtGLCategory.PROPERTY_ORGANIZATION);
			addVisibleField(FinancialMgmtGLCategory.PROPERTY_NAME);
			addVisibleField(FinancialMgmtGLCategory.PROPERTY_CATEGORY_TYPE);
			addVisibleField(FinancialMgmtGLCategory.PROPERTY_DESCRIPTION);
			addOrderAsc(FinancialMgmtGLCategory.PROPERTY_NAME);
		}

	}

	public static class GeneralLedgerJournalWindow extends AbstractDictionaryBasedWindowDefinition {

		public static final String ID = GeneralLedgerJournalWindow.class.getName();

		public GeneralLedgerJournalWindow() {
			super(ID, FinancialMgmtGLJournal.class);
			setName("G/L Journal Entries");
			addVisibleField(FinancialMgmtGLJournal.PROPERTY_ORGANIZATION);
			addVisibleField(FinancialMgmtGLJournal.PROPERTY_DOCUMENT_TYPE);
			addVisibleField(FinancialMgmtGLJournal.PROPERTY_DOCUMENT_NO);
			addVisibleField(FinancialMgmtGLJournal.PROPERTY_DESCRIPTION);
			addVisibleField(FinancialMgmtGLJournal.PROPERTY_FINANCIAL_MGMT_G_L_CATEGORY);
			addVisibleField(FinancialMgmtGLJournal.PROPERTY_TRANSACTION_DATE);
			addVisibleField(FinancialMgmtGLJournal.PROPERTY_ACCT_DATE);
			addVisibleField(FinancialMgmtGLJournal.PROPERTY_TOTAL_D_R);
			addVisibleField(FinancialMgmtGLJournal.PROPERTY_TOTAL_C_R);
			addOrderAsc(FinancialMgmtGLJournal.PROPERTY_DOCUMENT_NO);
			addOrderDesc(FinancialMgmtGLJournal.PROPERTY_TRANSACTION_DATE);
		}

	}

}
