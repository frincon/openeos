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
package org.openeos.erp.sales.ui;

import java.io.IOException;
import java.io.InputStream;

import org.hibernate.SessionFactory;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.document.EntityDocument;
import org.openeos.services.document.EntityDocumentService;
import org.openeos.services.ui.UIApplication;
import org.openeos.usertask.UserTask;
import org.openeos.usertask.ui.VaadinEntityUsertaskUI;
import org.openeos.vaadin.main.IUnoVaadinApplication;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class VaadinPrintDocumentUsertaskUI extends VaadinEntityUsertaskUI {

	public static final String METADATA_PRINT_DOCUMENT_NAME = "org.openeos.usertask.ui.PrintEntityDocument.name";

	private EntityDocumentService entityDocumentService;

	public VaadinPrintDocumentUsertaskUI(EntityDocumentService entityDocumentService, IDictionaryService dictionaryService,
			SessionFactory sessionFactory, String id) {
		super(dictionaryService, sessionFactory, id);
		this.entityDocumentService = entityDocumentService;
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	protected VerticalLayout createVaadinComponent(UserTask userTask, UIApplication<IUnoVaadinApplication> application) {

		EntityInfo info = extractEntity(userTask);

		String documentName = userTask.getMetaData(METADATA_PRINT_DOCUMENT_NAME);
		if (documentName == null || documentName.trim().length() == 0) {
			throw new IllegalArgumentException("The user task has not document meta data");
		}

		System.out.print(info.classDefinition);

		final EntityDocument document = entityDocumentService.findDocument((Class<Object>) info.classDefinition.getClassDefined(),
				info.entity, documentName);
		if (document == null) {
			throw new RuntimeException("Document not found");
		}

		final StreamSource streamSource = new StreamSource() {

			@Override
			public InputStream getStream() {
				try {
					return document.openInputStream();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		final StreamResource resource = new StreamResource(streamSource, document.getName(), application.getConcreteApplication()
				.getMainWindow().getApplication());

		VerticalLayout layout = super.createVaadinComponent(userTask, application);

		Panel panel = createPanel("Documents");
		panel.setWidth("100%");

		Button buttonDocument = new Button();
		buttonDocument.setStyleName(Reindeer.BUTTON_LINK);
		buttonDocument.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				event.getButton().getWindow().open(resource, document.getName());
			}
		});
		buttonDocument.setCaption(documentName);

		panel.addComponent(buttonDocument);
		layout.addComponent(panel);

		return layout;
	}
}
