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
package org.openeos.erp.core.ui.forms;

import java.util.SortedSet;

import org.abstractform.binding.fluent.BFField;
import org.abstractform.binding.fluent.BFForm;
import org.abstractform.binding.fluent.BFSubForm;
import org.abstractform.core.FormInstance;
import org.abstractform.core.selector.SelectorConstants;
import org.abstractform.core.selector.SelectorProvider;
import org.abstractform.core.selector.SelectorProviderFactory;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.erp.core.model.Client;
import org.openeos.erp.core.model.Organization;
import org.openeos.erp.core.ui.beans.ChangeProfileBean;
import org.openeos.erp.core.ui.internal.ClientFilterProvider;
import org.openeos.services.ui.form.abstractform.UIBeanSelectorProvider;

public class ChangeProfileForm extends BFForm<ChangeProfileBean> {

	public static final String ID = ChangeProfileForm.class.getName();
	public static final String NAME = "Change Profile Form";

	public BFSubForm SUBFORM_MAIN = addSubForm(null, 1);
	public BFField FIELD_CLIENT = SUBFORM_MAIN.addField(0, 0, null, "Client", "actualClient").required(true)
			.extra(SelectorConstants.EXTRA_SELECTOR_PROVIDER_FACTORY, new SelectorProviderFactory() {

				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public SelectorProvider<?> createSelectorProvider(FormInstance formInstance) {
					return new ChangeProfileSelectorProvider(Client.class, formInstance, null);
				}
			});
	public BFField FIELD_ORGANIZATION = SUBFORM_MAIN.addField(1, 0, null, "Default Organization", "actualOrganization")
			.required(false).extra(SelectorConstants.EXTRA_SELECTOR_PROVIDER_FACTORY, new SelectorProviderFactory() {

				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public SelectorProvider<?> createSelectorProvider(FormInstance formInstance) {
					return new ChangeProfileSelectorProvider(Organization.class, formInstance,
							"{alias}.ad_client_id=@source.actualClient.id@");
				}
			});;

	private SessionFactory sessionFactory;
	private ClientFilterProvider clientFilterProvider;

	public ChangeProfileForm(SessionFactory sessionFactory, ClientFilterProvider clientFilterProvider) {
		super(ID, NAME, ChangeProfileBean.class);
		this.sessionFactory = sessionFactory;
		this.clientFilterProvider = clientFilterProvider;
	}

	public class ChangeProfileSelectorProvider<T> extends UIBeanSelectorProvider<T> {

		public ChangeProfileSelectorProvider(Class<T> beanClass, FormInstance formInstance, String sqlRestriction) {
			super(sessionFactory, beanClass, formInstance, sqlRestriction);
			// TODO Auto-generated constructor stub
		}

		// TODO this can do with annotations and AOP
		// @DisableFilter(ClientFilterProvider.class) for example

		@Override
		@Transactional(readOnly = true)
		public SortedSet<T> getElements() {
			clientFilterProvider.disableFilter(sessionFactory.getCurrentSession());
			SortedSet<T> ret = super.getElements();
			clientFilterProvider.enableFilter(sessionFactory.getCurrentSession());
			return ret;
		}

	}
}
