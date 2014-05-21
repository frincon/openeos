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
package org.openeos.erp.sales.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.erp.sales.SalesInvoiceService;
import org.openeos.erp.sales.dao.SalesInvoiceDAO;
import org.openeos.erp.sales.model.SalesInvoice;
import org.openeos.numeration.NumerationService;
import org.openeos.wf.Constants;
import org.openeos.wf.JavaServiceTaskAware;
import org.openeos.wf.WorkflowService;

public class SalesInvoiceServiceImpl implements SalesInvoiceService, JavaServiceTaskAware<SalesInvoiceService> {

	private static final String WORKFLOW_INVOICE_DEFAULT_NAME = "org.openeos.erp.sales.InvoiceDefault";

	private static final Logger LOG = LoggerFactory.getLogger(SalesInvoiceServiceImpl.class);

	private NumerationService numerationService;
	private WorkflowService workflowService;
	private SalesInvoiceDAO salesInvoiceDAO;

	public SalesInvoiceServiceImpl(NumerationService numerationService, WorkflowService workflowService,
			SalesInvoiceDAO salesInvoiceDAO) {
		this.numerationService = numerationService;
		this.workflowService = workflowService;
		this.salesInvoiceDAO = salesInvoiceDAO;
	}

	private SalesInvoice read(String salesInvoiceId) {
		return salesInvoiceDAO.read(salesInvoiceId);
	}

	@Override
	@Transactional
	public void confirm(String salesInvoiceId) {
		confirm(read(salesInvoiceId));
	}

	@Override
	@Transactional
	public void confirm(SalesInvoice salesInvoice) {
		LOG.debug("Confirm invoice {}", salesInvoice.getDocumentNo());
		if (!salesInvoice.isDraft()) {
			throw new IllegalStateException("The invoice is already confirmed");
		}
		salesInvoice.setDraft(false);
		if (salesInvoice.getDocumentType().isDocNoControlled()) {
			String newNumber = numerationService.getAndIncrement(salesInvoice.getDocumentType().getFinalSequence().getId(),
					salesInvoice);
			LOG.debug("New document number: {}", newNumber);
			salesInvoice.setDocumentNo(newNumber);
		}
		salesInvoiceDAO.update(salesInvoice);
	}

	@Override
	@Transactional
	public void post(String salesInvoiceId) {
		post(read(salesInvoiceId));
	}

	@Override
	@Transactional
	public void post(SalesInvoice salesInvoice) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PROPERTY_WORKFLOW_ENTITY_NAME, SalesInvoice.class.getName());
		parameters.put(Constants.PROPERTY_WORKFLOW_ENTITY_ID, salesInvoice.getId());
		String processInstanceId = workflowService.startProcess(
				workflowService.getLastWorkflowDefinitionByKey(WORKFLOW_INVOICE_DEFAULT_NAME).getId(), parameters);
		salesInvoice.setPostedWorkflowId(processInstanceId);
		salesInvoice.setPosted(true);
		salesInvoiceDAO.update(salesInvoice);
	}

	@Override
	@Transactional
	public List<String> checkConstraints(String salesInvoiceId) {
		return checkConstraints(read(salesInvoiceId));
	}

	@Override
	@Transactional
	public List<String> checkConstraints(SalesInvoice salesInvoice) {
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return SALES_INVOICE_SERVICE_NAME;
	}

	@Override
	public Class<SalesInvoiceService> getServiceInterface() {
		return SalesInvoiceService.class;
	}

}
