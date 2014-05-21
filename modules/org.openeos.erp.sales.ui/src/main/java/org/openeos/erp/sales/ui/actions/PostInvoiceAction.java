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
package org.openeos.erp.sales.ui.actions;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.openeos.erp.core.Constants;
import org.openeos.erp.sales.SalesInvoiceService;
import org.openeos.erp.sales.model.SalesInvoice;
import org.openeos.services.ui.MessageType;
import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.action.EntityAction;

public class PostInvoiceAction implements EntityAction<SalesInvoice> {

	private SalesInvoiceService salesInvoiceService;

	public void setSalesInvoiceService(SalesInvoiceService salesInvoiceService) {
		this.salesInvoiceService = salesInvoiceService;
	}

	@Override
	public Class<SalesInvoice> getEntityClass() {
		return SalesInvoice.class;
	}

	@Override
	public String getName() {
		return "Post Invoice";
	}

	@Override
	public String getDescription() {
		return "Post the selected invoices";
	}

	@Override
	public boolean canExecute(SalesInvoice entity) {
		return !entity.isPosted();
	}

	@Override
	@Transactional
	public void execute(SalesInvoice entity, UIApplication application) {
		internalExecute(entity);
		application.refreshNotifications();
		application.showMessage(MessageType.INFO, "Invoice posted");
	}

	@Override
	public boolean canExecute(List<SalesInvoice> entityList) {
		for (SalesInvoice entity : entityList) {
			if (!canExecute(entity)) {
				return false;
			}
		}
		return !entityList.isEmpty();
	}

	@Override
	@Transactional
	public void execute(List<SalesInvoice> entityList, UIApplication application) {
		int count = 0;
		for (SalesInvoice entity : entityList) {
			internalExecute(entity);
			count++;
		}
		application.refreshNotifications();
		if (count == 1) {
			application.showMessage(MessageType.INFO, "Invoice posted");
		} else {
			application.showMessage(MessageType.INFO, String.format("%d invoices posted", count));
		}
	}

	private void internalExecute(SalesInvoice entity) {
		salesInvoiceService.post(entity.getId());
	}

	@Override
	public String getGroup() {
		return Constants.GROUP_ACTION_ACTIONS;
	}

}
