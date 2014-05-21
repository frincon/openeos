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
package org.openeos.reporting.jasperreports.entity.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.openeos.reporting.jasperreports.JRPostProcessor;
import org.openeos.reporting.jasperreports.JRPreProcessor;
import org.openeos.reporting.jasperreports.entity.Constants;
import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.dictionary.model.IClassDefinition;

public class JREntityProcessor implements JRPreProcessor, JRPostProcessor, Constants {

	private static final Logger LOG = LoggerFactory.getLogger(JREntityProcessor.class);

	private DataSource unoDataSource;
	private IDictionaryService dictionaryService;

	public JREntityProcessor(DataSource unoDataSource, IDictionaryService dictionaryService) {
		this.unoDataSource = unoDataSource;
		this.dictionaryService = dictionaryService;
	}

	@Override
	public void postProcess(JasperReport report, Map<String, Object> parameters, JasperPrint jasperPrint) {
		if (isEntityReport(report, parameters)) {
			String entityReportType = report.getProperty(PROPERTY_ENTITY_REPORT_TYPE);
			if (entityReportType == null) {
				entityReportType = DEFAULT_ENTITY_REPORT_TYPE;
			}
			if (entityReportType.equals(ENTITY_REPORT_TYPE_BEAN)) {
				postProcessTypeBean(report, parameters, jasperPrint);
			} else if (entityReportType.equals(ENTITY_REPORT_TYPE_SQL)) {
				postProcessTypeSql(report, parameters, jasperPrint);
			} else {
				LOG.warn("Unrecognized entity report type: '{}' ignoring", entityReportType);
			}
		}
	}

	private void postProcessTypeSql(JasperReport report, Map<String, Object> parameters, JasperPrint jasperPrint) {
		try {
			Connection conn = (Connection) parameters.get(JRParameter.REPORT_CONNECTION);
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	private void postProcessTypeBean(JasperReport report, Map<String, Object> parameters, JasperPrint jasperPrint) {
		// Nothing to do
	}

	@Override
	public void preProcess(JasperReport report, Map<String, Object> parameters) {
		if (isEntityReport(report, parameters)) {
			String entityReportType = report.getProperty(PROPERTY_ENTITY_REPORT_TYPE);
			if (entityReportType == null) {
				entityReportType = DEFAULT_ENTITY_REPORT_TYPE;
			}
			if (entityReportType.equals(ENTITY_REPORT_TYPE_BEAN)) {
				preProcessTypeBean(report, parameters);
			} else if (entityReportType.equals(ENTITY_REPORT_TYPE_SQL)) {
				preProcessTypeSql(report, parameters);
			} else {
				LOG.warn("Unrecognized entity report type: '{}' ignoring", entityReportType);
			}
		}
	}

	private boolean isEntityReport(JasperReport report, Map<String, Object> parameters) {
		Boolean isEntityReport = (Boolean) parameters.get(PARAMETER_IS_ENTITY_REPORT);
		return isEntityReport != null && isEntityReport.booleanValue();
	}

	private void preProcessTypeSql(JasperReport report, Map<String, Object> parameters) {
		try {
			Connection conn = unoDataSource.getConnection();
			parameters.put(JRParameter.REPORT_CONNECTION, conn);
			LOG.debug("Established parameter '{}' for database connection", JRParameter.REPORT_CONNECTION);
			parameters.put(PARAMETER_ENTITY_IDS_SQL, generateEntityIdsSql(report, parameters));
			LOG.debug("Established parameter '{}' with value '{}' for database entities ids", new Object[] { PARAMETER_ENTITY_IDS_SQL, parameters.get(PARAMETER_ENTITY_IDS_SQL) });
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private String generateEntityIdsSql(JasperReport report, Map<String, Object> parameters) {
		Collection<?> entityCollection = (Collection<?>) parameters.get(PARAMETER_ENTITY_COLLECTION);
		Class<?> entityClass = (Class<?>) parameters.get(PARAMETER_ENTITY_CLASS);
		if (entityCollection == null || entityClass == null) {
			LOG.warn("Processing entity report but missing parameters");
			return "()";
		} else {
			IClassDefinition def = dictionaryService.getClassDefinition(entityClass);
			StringBuilder builder = new StringBuilder("(");
			boolean first = true;
			for (Object entity : entityCollection) {
				if (!first) {
					builder.append(",");
				} else {
					first = false;
				}
				builder.append("'").append(((String) def.getId(entity)).replace("'", "''")).append("'");
			}
			if (first) {
				builder.append("''");
			}
			builder.append(")");
			return builder.toString();
		}
	}

	private void preProcessTypeBean(JasperReport report, Map<String, Object> parameters) {
		Collection<?> entityCollection = (Collection<?>) parameters.get(PARAMETER_ENTITY_COLLECTION);
		JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(entityCollection);
		parameters.put(JRParameter.REPORT_DATA_SOURCE, jrDataSource);
	}

}
