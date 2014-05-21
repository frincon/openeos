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
package org.openeos.wf.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.openeos.wf.WorkflowServiceException;
import org.openeos.wf.model.WorkflowDefinition;

public class WorkflowDefinitionValuesExtractorImpl implements WorkflowDefinitionValuesExtractor {

	private static final String BPMN2_URI = "http://www.omg.org/spec/BPMN/20100524/MODEL";

	//private static final String TNS_URI = "http://www.jboss.org/drools";

	private class Bpmn2Handler extends DefaultHandler {

		boolean found = false;

		private String id;
		private String name;

		//private String packageName;
		//private String version;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (uri.equals(BPMN2_URI) && "process".equals(localName)) {
				if (found) {
					throw new UnsupportedOperationException("More than one process found");
				}
				id = attributes.getValue("id");
				name = attributes.getValue("name");
				//packageName = attributes.getValue(TNS_URI, "package");
				//version = attributes.getValue(TNS_URI, "version");
				found = true;
			}
		}

	};

	@Override
	public void extractValues(WorkflowDefinition workflowDefinition, byte[] content) {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			Bpmn2Handler handler = new Bpmn2Handler();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new ByteArrayInputStream(content)));
			if (handler.id == null || handler.name == null) {
				throw new WorkflowServiceException("The name and id must not be empty");
			}
			workflowDefinition.setValue(handler.id);
			workflowDefinition.setName(handler.name);
		} catch (SAXException | IOException e) {
			throw new WorkflowServiceException("An exception occured while trying to read workflow definition", e);
		}

	}
}
