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
package org.openeos.reporting.entity;

import java.io.InputStream;
import java.util.Collection;

import org.openeos.reporting.ReportingException;

public interface EntityReportingService {

	public <T> InputStream generateReport(String reportId, Class<T> entityClass, Collection<T> entityCollection)
			throws ReportingException;

	public <T> InputStream generateReport(String reportId, Class<T> entityClass, T entity) throws ReportingException;

	public <T> InputStream generateReport(String reportId, Class<T> entityClass, Collection<T> entityCollection, String mimeType)
			throws ReportingException;

	public <T> InputStream generateReport(String reportId, Class<T> entityClass, T entity, String mimeType)
			throws ReportingException;

}
