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
package org.openeos.reporting.jasperreports.entity;

public interface Constants extends org.openeos.reporting.entity.Constants, org.openeos.reporting.jasperreports.Constants {

	public static final String PROPERTY_ENTITY_REPORT_TYPE = "org.openeos.reporting.jasperreports.entity.PROPERTY_ENTITY_REPORT_TYPE";

	public static final String PARAMETER_ENTITY_IDS_SQL = "ENTITY_IDS_SQL";

	public static final String ENTITY_REPORT_TYPE_BEAN = "BEAN";

	public static final String ENTITY_REPORT_TYPE_SQL = "SQL";

	public static final String DEFAULT_ENTITY_REPORT_TYPE = ENTITY_REPORT_TYPE_SQL;

}
