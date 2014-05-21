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
package org.openeos.reporting.jasperreports.test;

import static org.ops4j.pax.exam.CoreOptions.*;

import org.ops4j.pax.exam.Option;

public class JRConfiguration {

	public static Option jrDependencyBundles() {
		// @formatter:off
		return composite(
				mavenBundle("org.openeos", "org.openeos.reporting").versionAsInProject(),
				mavenBundle("net.sf.jasperreports", "jasperreports").versionAsInProject(),
				wrappedBundle(mavenBundle("jfree", "jcommon").versionAsInProject()),
				wrappedBundle(mavenBundle("com.lowagie", "itext").versionAsInProject()),
				mavenBundle("commons-collections", "commons-collections").versionAsInProject(),
				mavenBundle("org.openeos.external", "javax.servlet").versionAsInProject(),
				mavenBundle("commons-digester","commons-digester").versionAsInProject(),
				mavenBundle("commons-beanutils","commons-beanutils").versionAsInProject()
		);
		// @formatter:on
	}

	public static Option jrBundles() {
		// @formatter:off
		return composite(
				jrDependencyBundles(), 
				mavenBundle("org.openeos", "org.openeos.reporting.jasperreports").versionAsInProject()
		);
		// @formatter:on
	}

}
