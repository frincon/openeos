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
package org.openeos.reporting.internal;

import java.util.Map;

import org.osgi.framework.Constants;

import org.openeos.reporting.ReportingEngine;
import org.openeos.reporting.ReportingService;
import org.openeos.reporting.ReportsProvider;

public class AutoRegisterReportingServices {

	private ReportingService reportingService;

	public AutoRegisterReportingServices(ReportingService reportingService) {
		this.reportingService = reportingService;
	}

	public void bindReportingEngine(ReportingEngine<?> reportingEngine, Map<String, Object> properties) {
		reportingService.registerReportingEngine(reportingEngine, getRanking(properties));
	}

	public void unbindReportingEngine(ReportingEngine<?> reportingEngine) {
		if (reportingEngine != null) {
			reportingService.unregisterReportingEngine(reportingEngine);
		}
	}

	public void bindReportsProvider(ReportsProvider<?> reportsProvider, Map<String, Object> properties) {
		reportingService.registerReportsProvider(reportsProvider);
	}

	public void unbindReportsProvider(ReportsProvider<?> reportsProvider) {
		if (reportsProvider != null) {
			reportingService.unregisterReportsProvider(reportsProvider);
		}
	}

	private Integer getRanking(Map<String, Object> properties) {
		Integer ranking = (Integer) properties.get(Constants.SERVICE_RANKING);
		if (ranking == null) {
			ranking = 0;
		}
		return ranking;
	}

}
