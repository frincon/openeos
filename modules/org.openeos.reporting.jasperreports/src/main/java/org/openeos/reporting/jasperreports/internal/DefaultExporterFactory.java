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
package org.openeos.reporting.jasperreports.internal;

import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;

public class DefaultExporterFactory implements ExporterFactory {

	private Class<? extends JRExporter> exporterClass;

	public DefaultExporterFactory(Class<? extends JRExporter> exporterClass) {
		if (!JRExporter.class.isAssignableFrom(exporterClass)) {
			throw new IllegalArgumentException();
		}
		this.exporterClass = exporterClass;
	}

	@Override
	public JRExporter createExporter(Map<String, ?> parameters) {
		try {
			return exporterClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
