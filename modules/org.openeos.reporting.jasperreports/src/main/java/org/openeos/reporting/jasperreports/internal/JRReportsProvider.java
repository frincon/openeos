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

import net.sf.jasperreports.engine.JasperReport;

import org.openeos.reporting.BaseReportDefinition;
import org.openeos.reporting.ReportDefinition;
import org.openeos.reporting.ReportsProvider;
import org.openeos.reporting.jasperreports.JRReportsRegistry;
import org.openeos.utils.PriorityRankingRegistry;

public class JRReportsProvider implements ReportsProvider<JasperReport>, JRReportsRegistry {

	private PriorityRankingRegistry<String, JasperReport> registry = new PriorityRankingRegistry<String, JasperReport>();

	@Override
	public ReportDefinition<JasperReport> findReportDefinition(String id) {
		JasperReport report = registry.getActualElement(id);
		if (report != null) {
			return wrap(id, registry.getActualRanking(id), report);
		} else {
			return null;
		}
	}

	private ReportDefinition<JasperReport> wrap(final String id, final int ranking, final JasperReport report) {
		return new BaseReportDefinition<JasperReport>(id, ranking) {

			@Override
			public Class<JasperReport> getReportClass() {
				return JasperReport.class;
			}

			@Override
			public JasperReport getReport() {
				return report;
			}

		};
	}

	@Override
	public void registerJasperReport(String id, int ranking, JasperReport report) {
		registry.addElement(id, ranking, report);
	}

	@Override
	public void unregisterJasperReport(String id, JasperReport report) {
		registry.removeElement(id, report);
	}

}
