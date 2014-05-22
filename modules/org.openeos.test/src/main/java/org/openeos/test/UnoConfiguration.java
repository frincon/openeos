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
package org.openeos.test;

import static org.ops4j.pax.exam.Constants.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import org.ops4j.pax.exam.Constants;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.util.PathUtils;

public class UnoConfiguration {

	public static final String COVERAGE_COMMAND = "coverage.command";
	
	public static final String TEST_LOGLEVEL = "test.loglevel";

	public static Option unoDefaultConfiguration() {
		// @formatter:off
		return composite(
				unoMinimalConfiguration(),
				blueprintBundles(),

				//Necesary bundles

				//Bundles for blueprint

				mavenBundle("org.liquibase", "liquibase-osgi").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.jboss.logging", "jboss-logging").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("javax.transaction", "com.springsource.javax.transaction").version("1.1.0").startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.aopalliance", "com.springsource.org.aopalliance").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),

				mavenBundle("org.openeos", "org.openeos.hibernate").versionAsInProject().startLevel(3),
				mavenBundle("org.openeos", "org.openeos.dao").versionAsInProject().startLevel(3),
				mavenBundle("org.openeos", "org.openeos.dao.hibernate").versionAsInProject().startLevel(3),
				mavenBundle("org.openeos", "org.openeos.services.dictionary").versionAsInProject().startLevel(3),
				mavenBundle("org.openeos", "org.openeos.liquibase.extender").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),

				codeCoverageOption()
		);
		// @formatter:on
	}

	public static Option unoMinimalConfiguration() {
		String logLevel = System.getProperty(TEST_LOGLEVEL);
		if(logLevel != null) {
			logLevel = "-" + logLevel;
		} else {
			logLevel = "";
		}
		// @formatter:off
		return composite(
				systemProperty(EXAM_SYSTEM_KEY).value(EXAM_SYSTEM_DEFAULT),
				defaultTestSystemOptions(),
				systemProperty("org.ops4j.pax.url.mvn.repositories")
						.value("+http://repository.springsource.com/maven/bundles/release,http://repository.springsource.com/maven/bundles/external"),
				systemProperty("org.eclipse.equinox.http.jetty.http.port").value("8080"),
				systemProperty("osgi.resolveOptional").value("true"),
				systemProperty("osgi.framework.extensions").value("org.eclipse.equinox.weaving.hook"),
				systemProperty("eclipse.consoleLog").value("true"),
				systemProperty("osgi.debug").value("src/test/resources/equinox.options"),
				
				mavenBundle("org.aspectj", "com.springsource.org.aspectj.runtime").version("1.6.12.RELEASE").startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.aspectj", "com.springsource.org.aspectj.weaver").version("1.6.12.RELEASE").startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.openeos.external", "org.eclipse.equinox.weaving.hook").version("1.0.200.I20130319-1000")
						.noStart(),
				mavenBundle("org.openeos.external", "org.eclipse.equinox.weaving.aspectj").version("1.0.300.I20130319-1000")
						.startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.slf4j", "slf4j-api").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("ch.qos.logback", "logback-core").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("ch.qos.logback", "logback-classic").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.slf4j", "jcl-over-slf4j").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.slf4j", "log4j-over-slf4j").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.slf4j", "jul-to-slf4j").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				systemProperty("logback.configurationFile").value(
						"file:" + PathUtils.getBaseDir() + "/src/test/resources/logback" + logLevel + ".xml"),
				systemProperty("logback.statusListenerClass").value("ch.qos.logback.core.status.OnConsoleStatusListener"),
				
				mavenBundle("javax.persistence", "com.springsource.javax.persistence").version("2.0.0").startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.antlr", "com.springsource.antlr").version("2.7.7").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.antlr", "com.springsource.org.antlr.runtime").version("3.1.3").startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.dom4j", "com.springsource.org.dom4j").version("1.6.1").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("javax.xml.stream", "com.springsource.javax.xml.stream").version("1.0.1").startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.jboss.javassist", "com.springsource.javassist").version("3.15.0.GA").startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				
				mavenBundle("org.openeos", "org.openeos.logging").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),

				mavenBundle("org.openeos", "org.openeos.aspectj.config").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.openeos", "org.openeos.utils").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
						
				bundleStartLevel(5),
				frameworkStartLevel(5),

				bootDelegationPackages("org.eclipse.equinox.weaving.adaptors", "org.eclipse.equinox.weaving.hooks",
						"org.eclipse.equinox.service.weaving,org.xml.sax.helpers", "org.xml.sax,com.sun.source.tree",
						"com.sun.source.util"),

				systemPackages("org.eclipse.equinox.service.weaving", "com.sun.source.tree", "com.sun.source.util"),
				bootClasspathLibrary(maven("org.openeos.external", "org.eclipse.equinox.weaving.hook").version(
						"1.0.200.I20130319-1000"))

		);
		// @formatter:on
	}

	public static Option blueprintBundles() {
		// @formatter:off
		return composite(
				mavenBundle("org.eclipse.gemini.blueprint", "gemini-blueprint-core").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.eclipse.gemini.blueprint", "gemini-blueprint-extender").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.eclipse.gemini.blueprint", "gemini-blueprint-io").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "spring-aop").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "spring-asm").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "spring-beans").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "spring-context").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "spring-core").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "spring-expression").versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "org.springframework.orm").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "org.springframework.transaction").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "org.springframework.jdbc").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.springframework", "org.springframework.aspects").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES),
				mavenBundle("org.aopalliance", "com.springsource.org.aopalliance").versionAsInProject().startLevel(
						START_LEVEL_SYSTEM_BUNDLES)
		);
		// @formatter:on
	}

	public static Option weaverVerbose() {
		return composite(systemProperty("org.aspectj.weaver.showWeaveInfo").value("true"),
				systemProperty("org.aspectj.osgi.verbose").value("true"), systemProperty("aj.weaving.verbose").value("true")

		);
	}

	private static Option defaultTestSystemOptions() {
		//Coipied from PaxExamRuntime.defaultTestSystemOptions
		return composite(
				bootDelegationPackage("sun.*"),
				frameworkStartLevel(Constants.START_LEVEL_TEST_BUNDLE),
				url("link:classpath:META-INF/links/org.ops4j.pax.exam.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				//url("link:classpath:META-INF/links/org.ops4j.pax.exam.rbc.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.ops4j.pax.exam.inject.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.ops4j.pax.extender.service.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.osgi.compendium.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				// url( "link:classpath:META-INF/links/org.ops4j.pax.logging.api.link" ).startLevel( START_LEVEL_SYSTEM_BUNDLES ),
				url("link:classpath:META-INF/links/org.ops4j.base.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.ops4j.pax.swissbox.core.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.ops4j.pax.swissbox.extender.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.ops4j.pax.swissbox.framework.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.ops4j.pax.swissbox.framework.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.ops4j.pax.swissbox.lifecycle.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.ops4j.pax.swissbox.tracker.link").startLevel(START_LEVEL_SYSTEM_BUNDLES),
				url("link:classpath:META-INF/links/org.apache.geronimo.specs.atinject.link").startLevel(START_LEVEL_SYSTEM_BUNDLES));
	}

	public static Option unoDerbyInMemoryConfiguration() {
		return composite(mavenBundle("org.openeos", "org.openeos.ds.derby").version(getTestVersion()).startLevel(
				START_LEVEL_SYSTEM_BUNDLES));
	}

	public static Option unoPostgresqlConfiguration() {
		return composite(mavenBundle("org.openeos", "org.openeos.ds.postgresql").version(getTestVersion()).startLevel(
				START_LEVEL_SYSTEM_BUNDLES));
	}

	public static String getTestVersion() {
		return MavenUtils.getArtifactVersion("org.openeos", "org.openeos.test");
	}

	public static Option unoMasterData() {
		// @formatter:off
		return composite(mavenBundle("org.openeos", "org.openeos.data.masterdata").versionAsInProject(),
				//mavenBundle("org.openeos", "org.openeos.services.ui").versionAsInProject(),
				//mavenBundle("org.abstractform", "org.abstractform.binding").versionAsInProject(),
				//mavenBundle("org.abstractform", "org.abstractform.binding.eclipse").versionAsInProject(),
				//mavenBundle("org.abstractform", "org.abstractform.core").versionAsInProject(),
				//mavenBundle("org.openeos.external", "org.eclipse.equinox.common").versionAsInProject(),
				//mavenBundle("javax.validation", "validation-api").version("1.1.0.Final"), 
				mavenBundle("org.mvel", "mvel2").versionAsInProject()
				//mavenBundle("org.openeos.external", "com.ibm.icu").versionAsInProject(),
				//mavenBundle("org.openeos.external", "org.eclipse.core.databinding").versionAsInProject(),
				//mavenBundle("org.openeos.external", "org.eclipse.core.databinding.beans").versionAsInProject(),
				//mavenBundle("org.openeos.external", "org.eclipse.core.databinding.observable").versionAsInProject(),
				//mavenBundle("org.openeos.external", "org.eclipse.core.databinding.property").versionAsInProject());
		);
		// @formatter:on
	}

	public static Option unoSampleData() {
		// @formatter:off
		return composite(
				mavenBundle("org.openeos", "org.openeos.data.sampledata").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.erp.core").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.services.security").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.usertask").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.erp.sales").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.erp.document").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.numeration").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.numeration.freemarker").version(getTestVersion()),
				mavenBundle("org.freemarker", "freemarker").versionAsInProject(),
				mavenBundle("org.openeos", "org.openeos.wf").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.reporting").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.reporting.entity").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.reporting.jasperreports").version(getTestVersion()),
				mavenBundle("org.openeos", "org.openeos.services.document").version(getTestVersion()),
				mavenBundle("net.sf.jasperreports", "jasperreports").versionAsInProject(),
				wrappedBundle(mavenBundle("jfree", "jcommon").versionAsInProject()),
				wrappedBundle(mavenBundle("com.lowagie", "itext").versionAsInProject()),
				mavenBundle("commons-collections", "commons-collections").versionAsInProject(),
				mavenBundle("org.openeos.external", "javax.servlet").versionAsInProject(),
				mavenBundle("commons-digester","commons-digester").versionAsInProject(),
				mavenBundle("commons-beanutils","commons-beanutils").versionAsInProject(),
				mavenBundle("org.openeos", "org.openeos.erp.document").version(getTestVersion())
		);
		// @formatter:on
	}

	public static Option codeCoverageOption() {
		String coverageCommand = System.getProperty(COVERAGE_COMMAND);
		if (coverageCommand != null) {
			return vmOption(coverageCommand);
		}
		return null;
	}
}
