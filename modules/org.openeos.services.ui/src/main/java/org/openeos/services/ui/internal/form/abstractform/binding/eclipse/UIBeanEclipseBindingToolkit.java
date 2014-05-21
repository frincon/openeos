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
package org.openeos.services.ui.internal.form.abstractform.binding.eclipse;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.abstractform.binding.BField;
import org.abstractform.binding.BFormInstance;
import org.abstractform.binding.BPresenter;
import org.abstractform.binding.eclipse.EclipseBindingToolkit;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateSetStrategy;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.form.abstractform.BFUITable;
import org.openeos.services.ui.internal.UIBeanImpl;

public class UIBeanEclipseBindingToolkit extends EclipseBindingToolkit {

	private IDictionaryService dictionaryService;

	public IDictionaryService getDictionaryService() {
		return dictionaryService;
	}

	public void setDictionaryService(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	protected <S> Binding bindField(DataBindingContext dbCtx, final BField field, IObservableValue master,
			final BFormInstance<S> formInstance, Class<S> beanClass, boolean immediate, final BPresenter<S> presenter) {
		if (BFUITable.TYPE_UITABLE.equals(field.getType())) {
			final IObservableSet model = UIBeanProperties.set(beanClass, field.getPropertyName(), dictionaryService).observeDetail(
					master);
			final IObservableSet target = new WritableSet();
			target.addChangeListener(new IChangeListener() {

				// TODO Make better implementation
				@Override
				public void handleChange(ChangeEvent event) {
					formInstance.setFieldValue(field.getId(), target);
				}
			});
			formInstance.setFieldValue(field.getId(), target);
			UpdateSetStrategy modelToTarget = new UpdateSetStrategy(UpdateSetStrategy.POLICY_UPDATE);
			UpdateSetStrategy targetToModel = new UpdateSetStrategy(immediate ? UpdateSetStrategy.POLICY_UPDATE
					: UpdateSetStrategy.POLICY_ON_REQUEST);

			Binding binding = dbCtx.bindSet(target, model, targetToModel, modelToTarget);

			// This is implemented because the UIBean not send change events when modify collection
			model.addChangeListener(new IChangeListener() {

				@Override
				public void handleChange(ChangeEvent event) {
					UIBeanImpl bean = (UIBeanImpl) formInstance.getValue();
					presenter.modelHasChanged(field.getPropertyName(), (S) bean.getBeanWrapped());
				}
			});

			return binding;

		} else {
			return super.bindField(dbCtx, field, master, formInstance, beanClass, immediate, presenter);
		}
	}

	@Override
	protected IObservableValue getObservableValue(IObservableValue master, BField field, Class<?> beanClass) {
		return UIBeanProperties.value(beanClass, field.getPropertyName(), dictionaryService).observeDetail(master);
	}

	@Override
	protected <S> void bindPresenter(DataBindingContext dbCtx, final BPresenter<S> presenter, BFormInstance<S> formInstance) {
		final PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				presenter.modelHasChanged(evt.getPropertyName(), (S) ((UIBeanImpl) evt.getSource()).getBeanWrapped());
			}
		};
		if (formInstance.getValue() != null) {
			callAddPropertyChangeMethod(formInstance.getValue(), listener);
		}
		formInstance.addValuePropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				S model = (S) evt.getOldValue();
				callRemovePropertyChangeMethod(model, listener);
				presenter.setModel((S) ((UIBeanImpl) evt.getNewValue()).getBeanWrapped());
				model = (S) evt.getNewValue();
				callAddPropertyChangeMethod(model, listener);
			}
		});
	}

	@Override
	protected <S> void callPresenterFieldChanged(BPresenter<S> presenter, String fieldId, BFormInstance<S> formInstance) {
		UIBeanImpl bean = (UIBeanImpl) formInstance.getValue();
		presenter.fieldHasChanged(fieldId, (S) bean.getBeanWrapped());
	}

}
