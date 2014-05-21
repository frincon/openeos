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
package org.openeos.numeration.freemarker.internal;

import java.io.StringReader;
import java.io.StringWriter;

import org.openeos.numeration.NumerationResolver;
import org.openeos.numeration.NumerationServiceException;
import org.openeos.numeration.freemarker.FreemakerNumerationListItem;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerNumerationResolver implements NumerationResolver {

	private static final String TEMPLATE_NAME = "templateName";

	public class DataModel {
		private Object entity;
		private Long number;

		public Object getEntity() {
			return entity;
		}

		public void setEntity(Object entity) {
			this.entity = entity;
		}

		public Long getNumber() {
			return number;
		}

		public void setNumber(Long number) {
			this.number = number;
		}

	}

	@Override
	public String getNumerationResolverId() {
		return FreemakerNumerationListItem.NUMERATION_RESOLVER_FREEMARKER.getValue();
	}

	@Override
	public String resolveNumeration(String template, Object entity, Long number) {
		try {
			Template t = new Template(TEMPLATE_NAME, new StringReader(template), new Configuration());
			DataModel dm = new DataModel();
			dm.entity = entity;
			dm.number = number;
			StringWriter writer = new StringWriter();
			t.process(dm, writer);
			return writer.toString();
		} catch (Exception ex) {
			throw new NumerationServiceException("An error occurred when trying to resolve template", ex);
		}
	}

}
