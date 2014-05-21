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
package org.openeos.jbpm.integration.test;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;

import org.ops4j.pax.exam.Option;

public class JbpmIntegrationConfiguration {

	public static Option jbpmIntegrationBundleDependencies() {
		return composite(

				mavenBundle("org.openeos", "org.openeos.numeration").versionAsInProject(),

				wrappedBundle(maven("com.google.protobuf", "protobuf-java").versionAsInProject()).instructions(
						"Bundle-DocURL=http://code.google.com/p/protobuf", "Bundle-SymbolicName=com.google.protobuf",
						"Export-Package=com.google.protobuf"),

				mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.jaxb-xjc").version("2.2.6_1"),

				mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.jaxb-impl").version("2.2.6_1"),

				mavenBundle("org.apache.servicemix.specs", "org.apache.servicemix.specs.jaxb-api-2.2").version("1.6.0"),

				mavenBundle("org.apache.geronimo.specs", "geronimo-activation_1.1_spec").version("1.0.2"),

				mavenBundle("com.thoughtworks.xstream", "com.springsource.com.thoughtworks.xstream").version("1.4.1"),

				mavenBundle("org.xmlpull", "com.springsource.org.xmlpull").version("1.1.4.c"),

				mavenBundle("org.mvel", "mvel2").version("2.1.3.Final"),

				mavenBundle("org.eclipse.jdt.core.compiler", "ecj").versionAsInProject(),

				mavenBundle("org.drools", "knowledge-api").versionAsInProject(),

				mavenBundle("org.drools", "knowledge-internal-api").versionAsInProject(),

				mavenBundle("org.drools", "drools-core").versionAsInProject(),

				mavenBundle("org.drools", "drools-compiler").versionAsInProject(),

				mavenBundle("org.drools", "drools-persistence-jpa").versionAsInProject(),

				mavenBundle("org.jbpm", "jbpm-flow").versionAsInProject(),

				mavenBundle("org.jbpm", "jbpm-flow-builder").versionAsInProject(),

				mavenBundle("org.jbpm", "jbpm-bpmn2").versionAsInProject());
	}

	public static Option jbpmIntegrationBundles() {
		return composite(jbpmIntegrationBundleDependencies(), mavenBundle("org.openeos", "org.openeos.jbpm.integration")
				.versionAsInProject());
	}

}
