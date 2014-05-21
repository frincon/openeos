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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.mvel2.MVEL;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.reporting.ReportingException;
import org.openeos.reporting.entity.EntityReportingService;
import org.openeos.reporting.jasperreports.JRLoaderAndRegisterBean;
import org.openeos.services.document.spi.EntityDocumentProvider;
import org.openeos.services.document.spi.EntityDocumentProviderResult;

public class JREntityLoaderAndRegisterBean extends JRLoaderAndRegisterBean implements EntityDocumentProvider {

	private static final Logger LOG = LoggerFactory.getLogger(JREntityLoaderAndRegisterBean.class);

	private String documentMimeType;
	private String documentNameTemplate;
	private Class<?> entityClass;
	private String documentType;

	private ServiceRegistration<EntityDocumentProvider> register;

	public String getDocumentMimeType() {
		return documentMimeType;
	}

	public void setDocumentMimeType(String documentMimeType) {
		this.documentMimeType = documentMimeType;
	}

	public String getDocumentNameTemplate() {
		return documentNameTemplate;
	}

	public void setDocumentNameTemplate(String documentNameTemplate) {
		this.documentNameTemplate = documentNameTemplate;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	@Override
	public void register() {
		super.register();

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_RANKING, getRanking());
		register = getBundleContext().registerService(EntityDocumentProvider.class, this, properties);
	}

	@Override
	public void unregister() {
		register.unregister();
		super.unregister();
	}

	private String resolveName(Object entity, String template) {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("entity", entity);
		return MVEL.eval(template, context).toString();
	}

	@Override
	public <T> EntityDocumentProviderResult generateDocument(Class<T> entityClass, T entity, String documentId) {
		if (this.entityClass.isAssignableFrom(entityClass) && documentId.equals(getDocumentType())) {
			ServiceReference<EntityReportingService> serviceRef = getBundleContext().getServiceReference(
					EntityReportingService.class);
			if (serviceRef != null) {
				try {
					EntityReportingService ers = getBundleContext().getService(serviceRef);
					InputStream content = ers.generateReport(getId(), entityClass, entity, getDocumentMimeType());
					String name = resolveName(entity, getDocumentNameTemplate());
					return new EntityDocumentProviderResult(name, getDocumentMimeType(), content);
				} catch (ReportingException e) {
					LOG.warn("Cannot generate document becuase has thrown exception", e);
				}
			} else {
				LOG.warn("Cant generate document because not find EntityReportingService");
			}

		}
		return null;
	}

}
