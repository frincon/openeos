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
package org.openeos.erp.sales.model.aspects;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.openeos.erp.sales.model.BPTaxCategory;

import org.openeos.erp.core.model.BusinessPartner;

public aspect BusinessPartnerSalesAspect {

	public static final String BusinessPartner.PROPERTY_BP_TAX_CATEGORY = "BPTaxCategory";
	public static final String BusinessPartner.PROPERTY_CUSTOMER = "customer";

	private Boolean BusinessPartner.customer;
	private BPTaxCategory BusinessPartner.bpTaxCategory;

	@Column(name = "customer", nullable = false)
	public Boolean BusinessPartner.isCustomer() {
		return this.customer;
	}

	public void BusinessPartner.setCustomer(Boolean customer) {
		this.customer = customer;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_bp_taxcategory_id", nullable = true)
	public BPTaxCategory BusinessPartner.getBPTaxCategory() {
		return this.bpTaxCategory;
	}

	public void BusinessPartner.setBPTaxCategory(BPTaxCategory bpTaxCategory) {
		this.bpTaxCategory = bpTaxCategory;
	}

}
