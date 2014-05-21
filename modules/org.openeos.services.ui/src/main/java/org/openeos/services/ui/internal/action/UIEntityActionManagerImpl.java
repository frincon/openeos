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
package org.openeos.services.ui.internal.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openeos.services.ui.UIApplication;
import org.openeos.services.ui.UIBean;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.action.EntityAction;
import org.openeos.services.ui.action.EntityActionManager;
import org.openeos.services.ui.action.UIEntityActionManager;
import org.openeos.services.ui.internal.UIBeanImpl;

public class UIEntityActionManagerImpl implements UIEntityActionManager {

	private class UIBeanAction implements EntityAction<UIBean> {

		private EntityAction<Object> realAction;

		UIBeanAction(EntityAction<Object> realAction) {
			this.realAction = realAction;
		}

		@Override
		public Class<UIBean> getEntityClass() {
			return UIBean.class;
		}

		@Override
		public String getName() {
			return realAction.getName();
		}

		@Override
		public String getDescription() {
			return realAction.getDescription();
		}

		@Override
		public String getGroup() {
			return realAction.getGroup();
		}

		@Override
		public boolean canExecute(UIBean entity) {
			return realAction.canExecute(UIBeanImpl.unwrap(entity));
		}

		@Override
		public boolean canExecute(List<UIBean> entityList) {
			return realAction.canExecute(UIBeanImpl.unwrap(entityList));
		}

		@Override
		public void execute(UIBean entity, UIApplication application) {
			realAction.execute(UIBeanImpl.unwrap(entity), application);
			uidaoService.refresh(entity);
		}

		@Override
		public void execute(List<UIBean> entityList, UIApplication application) {
			realAction.execute(UIBeanImpl.unwrap(entityList), application);
			for (UIBean entity : entityList) {
				uidaoService.refresh(entity);
			}
		}

	}

	private EntityActionManager entityActionManager;
	private UIDAOService uidaoService;

	public UIEntityActionManagerImpl(EntityActionManager entityActionManager, UIDAOService uidaoService) {
		this.entityActionManager = entityActionManager;
		this.uidaoService = uidaoService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<EntityAction<UIBean>> getEnabledActionsOrdered(Class<T> entityClass, List<UIBean> selectedObjects) {
		List<T> convertedList = (List<T>) UIBeanImpl.unwrap(selectedObjects);
		List<EntityAction<T>> intermediate = entityActionManager.getEnabledActionsOrdered(entityClass, convertedList);
		return Collections.unmodifiableList(convertResult(entityClass, intermediate));
	}

	private <T> List<EntityAction<UIBean>> convertResult(Class<T> entityClass, List<EntityAction<T>> intermediate) {
		ArrayList<EntityAction<UIBean>> result = new ArrayList<EntityAction<UIBean>>(intermediate.size());
		for (EntityAction<T> realAction : intermediate) {
			result.add(wrapAction(entityClass, realAction));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private <T> EntityAction<UIBean> wrapAction(Class<T> entityClass, EntityAction<T> realAction) {
		return new UIBeanAction((EntityAction<Object>) realAction);
	}

}
