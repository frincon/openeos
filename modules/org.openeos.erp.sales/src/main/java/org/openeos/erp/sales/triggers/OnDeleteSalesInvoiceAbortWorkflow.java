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
package org.openeos.erp.sales.triggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.dao.DeleteEvent;
import org.openeos.dao.DeleteListener;
import org.openeos.dao.UserException;
import org.openeos.erp.sales.model.SalesInvoice;
import org.openeos.wf.WorkflowService;

public class OnDeleteSalesInvoiceAbortWorkflow implements DeleteListener<SalesInvoice> {

	private static final Logger LOG = LoggerFactory.getLogger(OnDeleteSalesInvoiceAbortWorkflow.class);

	private WorkflowService workflowService;

	public OnDeleteSalesInvoiceAbortWorkflow(WorkflowService workflowService) {
		if (workflowService == null) {
			throw new IllegalArgumentException();
		}
		this.workflowService = workflowService;
	}

	@Override
	public void beforeDelete(DeleteEvent<? extends SalesInvoice> event) throws UserException {
		if (event.getEntity().getPostedWorkflowId() != null) {
			try {
				workflowService.abortProcessInstance(event.getEntity().getPostedWorkflowId());
			} catch (Exception ex) {
				LOG.warn("Abort process from invoice has thrown an unexpected exception.", ex);
			}
		}
	}

	@Override
	public void afterDelete(DeleteEvent<? extends SalesInvoice> event) {
		// Do nothing
	}

}
