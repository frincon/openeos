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
package org.openeos.reporting.entity.internal;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openeos.reporting.ReportingException;
import org.openeos.reporting.ReportingService;
import org.openeos.reporting.entity.Constants;
import org.openeos.reporting.entity.EntityReportingService;

public class EntityReportingServiceImpl implements EntityReportingService, Constants {

	private ReportingService reportService;

	public EntityReportingServiceImpl(ReportingService reportService) {
		this.reportService = reportService;
	}

	@Override
	public <T> InputStream generateReport(String reportId, Class<T> entityClass, Collection<T> entityCollection)
			throws ReportingException {
		return generateReport(reportId, entityClass, entityCollection, null);
	}

	@Override
	public <T> InputStream generateReport(String reportId, Class<T> entityClass, T entity) throws ReportingException {
		return generateReport(reportId, entityClass, Arrays.asList(entity));
	}

	@Override
	public <T> InputStream generateReport(String reportId, Class<T> entityClass, Collection<T> entityCollection, String mimeType)
			throws ReportingException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PARAMETER_IS_ENTITY_REPORT, true);
		parameters.put(PARAMETER_ENTITY_CLASS, entityClass);
		parameters.put(PARAMETER_ENTITY_COLLECTION, entityCollection);
		return reportService.generateReport(reportId, mimeType, parameters);
	}

	@Override
	public <T> InputStream generateReport(String reportId, Class<T> entityClass, T entity, String mimeType)
			throws ReportingException {
		return generateReport(reportId, entityClass, Arrays.asList(entity), mimeType);
	}

}
