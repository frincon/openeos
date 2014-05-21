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
package org.openeos.reporting;

import java.io.InputStream;
import java.util.Map;

public interface ReportingService {

	public InputStream generateReport(String reportId) throws ReportingException;

	public InputStream generateReport(String reportId, Map<String, Object> parameters) throws ReportingException;

	public InputStream generateReport(String reportId, String mimeType) throws ReportingException;

	public InputStream generateReport(String reportId, String mimeType, Map<String, Object> parameters) throws ReportingException;

	public void registerReportingEngine(ReportingEngine<?> reportingEngine, int ranking);

	public void unregisterReportingEngine(ReportingEngine<?> reportingEngine);

	public void registerReportsProvider(ReportsProvider<?> reportsProvider);

	public void unregisterReportsProvider(ReportsProvider<?> reportsProvider);

}
