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

public interface ReportingEngine<T> {

	public Class<T> getReportClass();

	/**
	 * Generate report with the report passed as argument
	 * 
	 * @param report
	 * @param parameters
	 *            May be null
	 * @param mimeType
	 *            May be null
	 * @return
	 * @throws ReportingException
	 */
	public InputStream generateReport(T report, Map<String, Object> parameters, String mimeType) throws ReportingException;

}
