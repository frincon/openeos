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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openeos.reporting.jasperreports.JRPostProcessor;
import org.openeos.reporting.jasperreports.JRPreProcessor;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class JasperReportsEngineLifecycleImpl implements JasperReportsEngineLifecycle {

	private List<JRPreProcessor> preProcessorList = new CopyOnWriteArrayList<JRPreProcessor>();
	private List<JRPostProcessor> postProcessorList = new CopyOnWriteArrayList<JRPostProcessor>();

	@Override
	public void callPreProcessors(JasperReport jasperReport, Map<String, Object> parameters) {
		for (JRPreProcessor preProcessor : preProcessorList) {
			preProcessor.preProcess(jasperReport, parameters);
		}
	}

	@Override
	public void callPostProcessors(JasperReport jasperReport, Map<String, Object> parameters, JasperPrint jasperPrint) {
		for(JRPostProcessor postProcessor : postProcessorList) {
			postProcessor.postProcess(	jasperReport, parameters, jasperPrint);
		}
	}

	public void bindPreProcessor(JRPreProcessor preProcessor) {
		preProcessorList.add(preProcessor);
	}

	public void unbindPreProcessor(JRPreProcessor preProcessor) {
		if (preProcessor != null) {
			preProcessorList.remove(preProcessor);
		}
	}

	public void bindPostProcessor(JRPostProcessor postProcessor) {
		postProcessorList.add(postProcessor);
	}

	public void unbindPostProcessor(JRPostProcessor postProcessor) {
		if (postProcessor != null) {
			postProcessorList.remove(postProcessor);
		}
	}

}
