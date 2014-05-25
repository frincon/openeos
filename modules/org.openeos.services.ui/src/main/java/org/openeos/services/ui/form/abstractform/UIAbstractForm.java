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

package org.openeos.services.ui.form.abstractform;

import org.abstractform.binding.BForm;
import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.BPresenter;
import org.abstractform.binding.fluent.BFForm;
import org.openeos.services.ui.UIBean;

/**
 * @author Fernando Rincon Martin <frm.rincon@gmail.com>
 * 
 */
public abstract class UIAbstractForm<T> extends BFForm<UIBean, T> implements AbstractFormBindingForm<T, UIBean> {

	public static final Integer DEFAULT_RANKING = 0;

	/**
	 * @param id
	 * @param name
	 * @param beanClass
	 */
	protected UIAbstractForm(String id, String name, Class<T> uiBeanClass) {
		super(id, name, uiBeanClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openeos.services.ui.form.BindingForm#getRanking()
	 */
	@Override
	public Integer getRanking() {
		return DEFAULT_RANKING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openeos.services.ui.form.abstractform.AbstractFormBindingForm#
	 * getAbstractBForm()
	 */
	@Override
	public BForm<UIBean, T> getAbstractBForm() {
		return this;
	}

	@Override
	public BPresenter createPresenter(BFormInstance<UIBean> formInstance, UIBean model) {
		return new UIPresenter<T>(getBeanClass(), model);
	}

}
