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
package org.openeos.wf.test;

import static org.openeos.test.UnoConfiguration.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import org.ops4j.pax.exam.Option;

public class WorkflowConfiguration {

	public static Option wfDependencyBundles() {
		return composite(mavenBundle("org.openeos", "org.openeos.numeration").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.numeration.freemarker").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.utils").versionAsInProject(),
				mavenBundle("org.apache.commons", "com.springsource.org.apache.commons.codec").versionAsInProject(),
				mavenBundle("org.apache.commons", "com.springsource.org.apache.commons.io").versionAsInProject(),
				mavenBundle("org.freemarker", "freemarker").versionAsInProject(),
				mavenBundle("org.springframework.security", "spring-security-core").versionAsInProject(),
				mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").version("1.3.2"));

	}

	public static Option wfBundles() {
		return composite(wfDependencyBundles(), mavenBundle("org.openeos", "org.openeos.wf").versionAsInProject());
	}

}
