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
package org.openeos.usertask.ui;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.IClassDefinition;
import org.openeos.services.ui.UIApplication;
import org.openeos.usertask.UserTask;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class VaadinEntityUsertaskUI extends VaadinUsertaskUI {

	private static final Logger LOG = LoggerFactory.getLogger(VaadinEntityUsertaskUI.class);

	public static final String METADATA_ENTITY_ID = "entityId";
	public static final String METADATA_ENTITY_NAME = "entityName";

	private IDictionaryService dictionaryService;
	private SessionFactory sessionFactory;
	private String id;

	protected class EntityInfo {
		public IClassDefinition classDefinition;
		public Object entity;

	}

	public VaadinEntityUsertaskUI(IDictionaryService dictionaryService, SessionFactory sessionFactory, String id) {
		this.dictionaryService = dictionaryService;
		this.sessionFactory = sessionFactory;
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	protected VerticalLayout createVaadinComponent(UserTask userTask, UIApplication<IUnoVaadinApplication> application) {

		EntityInfo info = extractEntity(userTask);

		Panel panel = createPanel("Entities");
		StringBuilder captionBuilder = new StringBuilder();
		captionBuilder.append(info.classDefinition.getSingularEntityName());
		captionBuilder.append(": ");
		captionBuilder.append(info.classDefinition.getStringRepresentation(info.entity));
		Button button = new Button(captionBuilder.toString());
		button.setStyleName(Reindeer.BUTTON_LINK);
		panel.addComponent(button);
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(false);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		mainLayout.addComponent(panel);
		return mainLayout;
	}

	protected Panel createPanel(String caption) {
		Panel panel = new Panel(caption);
		panel.setStyleName("background-transparent");
		return panel;
	}

	@Transactional
	protected EntityInfo extractEntity(UserTask userTask) {
		// TODO Make cache because the inherit classes may call us
		String entityId = userTask.getMetaData(METADATA_ENTITY_ID);
		String entityName = userTask.getMetaData(METADATA_ENTITY_NAME);
		if (entityId == null || entityId.trim().length() == 0 || entityName == null || entityName.trim().length() == 0) {
			throw new IllegalArgumentException("The user task has no entity.");
		}

		IClassDefinition classDef = dictionaryService.getClassDefinition(entityName);
		Object entity = sessionFactory.getCurrentSession().get(classDef.getClassDefined(), entityId);

		EntityInfo info = new EntityInfo();
		info.classDefinition = classDef;
		info.entity = entity;
		return info;

	}

}
