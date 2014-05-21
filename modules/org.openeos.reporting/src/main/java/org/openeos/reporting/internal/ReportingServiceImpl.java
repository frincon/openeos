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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.reporting.ReportDefinition;
import org.openeos.reporting.ReportingEngine;
import org.openeos.reporting.ReportingException;
import org.openeos.reporting.ReportingService;
import org.openeos.reporting.ReportsProvider;

public class ReportingServiceImpl implements ReportingService {

	private static final Logger LOG = LoggerFactory.getLogger(ReportingServiceImpl.class);

	private class ReportingEngineItem implements Comparable<ReportingEngineItem> {

		ReportingEngine<?> reportingEngine;
		int ranking;

		@Override
		public int compareTo(ReportingEngineItem o) {
			return Integer.compare(ranking, o.ranking);
		}

	}

	private List<ReportsProvider<?>> reportsProviderList = new ArrayList<ReportsProvider<?>>();
	private List<ReportingEngineItem> reportingEngineList = new ArrayList<ReportingEngineItem>();

	@Override
	public InputStream generateReport(String reportId) throws ReportingException {
		return generateReport(reportId, null, null);
	}

	@Override
	public InputStream generateReport(String reportId, Map<String, Object> parameters) throws ReportingException {
		return generateReport(reportId, null, parameters);
	}

	@Override
	public InputStream generateReport(String reportId, String mimeType) throws ReportingException {
		return generateReport(reportId, mimeType, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public InputStream generateReport(String reportId, String mimeType, Map<String, Object> parameters) throws ReportingException {
		LOG.debug("Generating Report. reportId: '{}' mimeType: '{}'", reportId, mimeType);
		LOG.trace("   Parameters: {}", parameters);
		Collection<ReportDefinition<?>> reportDefinitions = findAllReportDefinitionAndSort(reportId);
		LOG.debug("Report definitions found: {}", reportDefinitions.size());
		ReportDefinition<?> reportDefinitionFound = null;
		ReportingEngine<Object> engine = null;
		for (ReportDefinition<?> reportDefinition : reportDefinitions) {
			reportDefinitionFound = reportDefinition;
			LOG.debug("Trying to find engine for class '{}'", reportDefinition.getReportClass().getName());
			engine = (ReportingEngine<Object>) findEngine(reportDefinition.getReportClass());
			if (engine != null) {
				LOG.debug("Engine and report found!! Generating report");
				break;
			}
		}
		if (engine == null || reportDefinitionFound == null) {
			// TODO Make with normal exception?
			throw new IllegalArgumentException(String.format("Not found suitable report and engine for report id '%s'", reportId));
		}
		return engine.generateReport(reportDefinitionFound.getReport(), parameters, mimeType);
	}

	private Collection<ReportDefinition<?>> findAllReportDefinitionAndSort(String reportId) {
		SortedSet<ReportDefinition<?>> resultSet = new TreeSet<ReportDefinition<?>>();
		for (ReportsProvider<?> reportsProvider : reportsProviderList) {
			ReportDefinition<?> reportDefinition = reportsProvider.findReportDefinition(reportId);
			if (reportDefinition != null) {
				resultSet.add(reportDefinition);
			}
		}
		return resultSet;
	}

	private ReportingEngine<?> findEngine(Class<?> reportClass) {
		for (ReportingEngineItem engItem : reportingEngineList) {
			if (engItem.reportingEngine.getReportClass().isAssignableFrom(reportClass)) {
				return engItem.reportingEngine;
			}
		}
		return null;
	}

	@Override
	public void registerReportingEngine(ReportingEngine<?> reportingEngine, int ranking) {
		ReportingEngineItem item = new ReportingEngineItem();
		item.reportingEngine = reportingEngine;
		item.ranking = ranking;
		reportingEngineList.add(item);
		Collections.sort(reportingEngineList);
	}

	@Override
	public void unregisterReportingEngine(ReportingEngine<?> reportingEngine) {
		int index = -1;
		for (int i = 0; i < reportingEngineList.size(); i++) {
			if (reportingEngineList.get(i).reportingEngine == reportingEngine) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException();
		}
		reportingEngineList.remove(index);
	}

	@Override
	public void registerReportsProvider(ReportsProvider<?> reportsProvider) {
		reportsProviderList.add(reportsProvider);
	}

	@Override
	public void unregisterReportsProvider(ReportsProvider<?> reportsProvider) {
		reportsProviderList.remove(reportsProvider);
	}

}
