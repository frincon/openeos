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
package org.openeos.services.ui.vaadin.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	public static void traceCall(Class<?> objectClass) {
		Logger log = LoggerFactory.getLogger(objectClass);
		traceCall(log, 3);
	}

	public static void traceCall(Logger log) {
		traceCall(log, 3);
	}

	public static void traceCall(Logger log, int ignoreCalls) {
		if (log.isTraceEnabled()) {
			StackTraceElement element = Thread.currentThread().getStackTrace()[ignoreCalls];
			log.trace("Called " + element.getClassName() + "." + element.getMethodName());
		}
	}

}
