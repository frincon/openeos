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
package org.openeos.wf.jbpm.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.openeos.jbpm.integration.KnowledgeBaseProvider;
import org.openeos.wf.Constants;
import org.openeos.wf.Deployment;
import org.openeos.wf.WorkflowDefinition;
import org.openeos.wf.WorkflowService;

public class JbpmSynchronizer implements EventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(JbpmSynchronizer.class);

	private static final String BPMN2_URI = "http://www.omg.org/spec/BPMN/20100524/MODEL";

	private static final String DROOLS_URI = "http://www.jboss.org/drools";

	// External Services
	private WorkflowService workflowService;
	private BundleContext bundleContext;

	//Services to unregister
	private ServiceRegistration<EventHandler> eventHandlerRegistration;
	private ServiceRegistration<KnowledgeBaseProvider> kbaseProviderRegistration;

	private Date lastDeploymentDate = new Date(0);

	private ExecutorService syncExecutor;

	private KnowledgeBase kbase;

	public JbpmSynchronizer(WorkflowService workflowService, BundleContext bundleContext) {
		if (workflowService == null || bundleContext == null) {
			throw new IllegalArgumentException();
		}
		this.workflowService = workflowService;
		this.bundleContext = bundleContext;
		System.setProperty("jbpm.enable.multi.con", Boolean.toString(true));
	}

	public void start() {
		LOG.debug("Starting WF->JBPM Synchronizer");
		createInitialKBase();
		syncNow();
		registerKBaseProvider();
		createExecutorService();
		registerForEvents();
		sendSyncNow();
	}

	private void registerKBaseProvider() {
		LOG.debug("Registering KnowledgeBaseProvider as OSGI Service");
		kbaseProviderRegistration = bundleContext.registerService(KnowledgeBaseProvider.class, new KnowledgeBaseProvider() {

			@Override
			public KnowledgeBase getKnowledgeBase() {
				return kbase;
			}
		}, null);
	}

	private void sendSyncNow() {
		LOG.debug("Sending sync task to executor service");
		syncExecutor.execute(new Runnable() {
			@Override
			public void run() {
				syncNow();
			}
		});
	}

	private void syncNow() {
		LOG.debug("Initiating new synchornization");
		Date previousLastDate = lastDeploymentDate;
		LOG.debug("Previous deployment last date '{}'", previousLastDate);

		LOG.debug("Creating package builder and configuring it");
		PackageBuilder builder = new PackageBuilder();
		BPMN2ProcessFactory.configurePackageBuilder(builder);
		List<WorkflowDefinition> allDefs = workflowService.getAllWorkflowDefinition();
		int installed = 0;
		LOG.debug("Checking {} workflow definitions", allDefs.size());
		for (WorkflowDefinition def : allDefs) {
			Deployment deployment = workflowService.getDeployment(def.getDeploymemtId());
			LOG.debug("Check if deployment must be sincronized, deployment date: {} ", deployment.getDeploymentDate());
			if (deployment.getDeploymentDate().compareTo(previousLastDate) > 0) {
				try {
					LOG.debug("Need to deploy to kbase, building process for Workflow,  Id: {}, Key: {}, Version: {}",
							new Object[] { def.getId(), def.getKey(), def.getVersion() });
					builder.addProcessFromXml(getResourceFromDefinition(def));
					installed++;
					Date deploymentDate = deployment.getDeploymentDate();
					lastDeploymentDate = deploymentDate.compareTo(lastDeploymentDate) > 0 ? deploymentDate : lastDeploymentDate;
				} catch (Exception e) {
					LOG.error(String.format("Cant register process %s becouse has thorwn an exception", def.getKey()), e);
				}
			}
		}
		if (installed > 0 && builder.getPackage() != null) {
			LOG.debug("Total process builts: {}", installed);
			kbase.addKnowledgePackages(Arrays.asList((KnowledgePackage) new KnowledgePackageImp(builder.getPackage())));
		}
	}

	private void createExecutorService() {
		syncExecutor = Executors.newSingleThreadExecutor();
	}

	private void createInitialKBase() {
		kbase = KnowledgeBaseFactory.newKnowledgeBase();
	}

	private Resource getResourceFromDefinition(WorkflowDefinition def) throws ParserConfigurationException, SAXException,
			IOException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(def.getContent()));
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {

			@SuppressWarnings("rawtypes")
			@Override
			public Iterator getPrefixes(String namespaceURI) {
				return null;
			}

			@Override
			public String getPrefix(String namespaceURI) {
				return null;
			}

			@Override
			public String getNamespaceURI(String prefix) {
				if (prefix.equals("bpmn2")) {
					return BPMN2_URI;
				} else if (prefix.equals("drools")) {
					return DROOLS_URI;
				}
				return XMLConstants.NULL_NS_URI;
			}
		});
		XPathExpression expr = xpath.compile("//bpmn2:process");
		NodeList result = (NodeList) expr.evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
		if (result == null || result.getLength() != 1) {
			throw new RuntimeException("Process not found in xml");
		}
		Node processNode = result.item(0);
		Attr idAttr = doc.createAttribute("id");
		idAttr.setNodeValue(def.getId());
		Attr nameAttr = doc.createAttribute("name");
		nameAttr.setNodeValue(def.getKey());
		Attr packageAttr = doc.createAttributeNS(DROOLS_URI, "packageName");
		packageAttr.setNodeValue("default");
		Attr versionAttr = doc.createAttributeNS(DROOLS_URI, "version");
		versionAttr.setNodeValue(def.getVersion());
		processNode.getAttributes().setNamedItem(idAttr);
		processNode.getAttributes().setNamedItem(nameAttr);
		processNode.getAttributes().setNamedItemNS(packageAttr);
		processNode.getAttributes().setNamedItemNS(versionAttr);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Result output = new StreamResult(bos);
		Source input = new DOMSource(doc);
		transformer.transform(input, output);
		byte[] converted = bos.toByteArray();
		LOG.trace("The result xml:\n{}", new String(converted));
		return ResourceFactory.newByteArrayResource(converted);
	}

	public void stop() {
		eventHandlerRegistration.unregister();
		stopExecutorService();
		kbaseProviderRegistration.unregister();
	}

	private void stopExecutorService() {
		syncExecutor.shutdown();
		try {
			// Wait a while for existing tasks to terminate
			if (!syncExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
				syncExecutor.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!syncExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
					LOG.error("The sync executor did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			syncExecutor.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	private void registerForEvents() {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, Constants.PREFIX_TOPIC + "*");
		eventHandlerRegistration = bundleContext.registerService(EventHandler.class, this, properties);
	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(Constants.DEPLOYMENT_TOPIC)) {
			sendSyncNow();
		} else if (event.getTopic().equals(Constants.UNDEPLOYMENT_TOPIC)) {
			// TODO No-op
		}
	}

}
