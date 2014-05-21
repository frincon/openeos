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
package org.openeos.numeration.freemarker.test;

import static org.openeos.test.UnoConfiguration.*;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.numeration.NumerationService;
import org.openeos.numeration.freemarker.FreemakerNumerationListItem;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class NumerationFreemarkerTestCase {

	private static final Logger LOG = LoggerFactory.getLogger(NumerationFreemarkerTestCase.class);

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 100000)
	private NumerationService numerationService;

	@Configuration
	public static Option[] config() {
		return options(unoDefaultConfiguration(), unoDerbyInMemoryConfiguration(), junitBundles(),
				mavenBundle("org.openeos", "org.openeos.numeration").version(getTestVersion()),
				mavenBundle("org.freemarker", "freemarker").versionAsInProject(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes/").startLevel(4));
	}

	@Test
	public void testServiceIsUp() {
		LOG.info("Starting testServiceIsUp()");
		assertNotNull(bc);
		assertNotNull(numerationService);
	}

	@Test
	public void testCreateNumeration() throws Exception {
		LOG.info("Starting testCreateNumeration()");
		String id = numerationService.createNumeration("TEST1", "TEST1 Name", "This is example", "${number}",
				FreemakerNumerationListItem.NUMERATION_RESOLVER_FREEMARKER);
		LOG.debug("Id of new numeration: " + id);
	}

	@Test
	public void testCreateUse() throws Exception {
		LOG.info("Starting testCreateUse()");
		String id = numerationService.createNumeration("TEST2", "TEST2 Name", "This is example", "${number}",
				FreemakerNumerationListItem.NUMERATION_RESOLVER_FREEMARKER);
		LOG.debug("Id of new numeration: " + id);
		String number = numerationService.getAndIncrement(id);
		assertEquals("1", number);
		number = numerationService.getAndIncrement(id);
		assertEquals("2", number);
	}

	@Test
	public void testCreateUseWithDate() throws Exception {
		LOG.info("Starting testCreateUseWithDate()");
		String id = numerationService.createNumeration("TEST3", "TEST3 Name", "This is example",
				"${entity.date?string('yyyy')}${number?string('000000')}",
				FreemakerNumerationListItem.NUMERATION_RESOLVER_FREEMARKER);
		LOG.debug("Id of new numeration: " + id);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		SampleEntity ent1 = new SampleEntity();
		ent1.date = formatter.parse("01/01/2013");
		String number = numerationService.getAndIncrement(id, ent1);
		assertEquals("2013000001", number);
		number = numerationService.getAndIncrement(id, ent1);
		assertEquals("2013000002", number);
		ent1.date = formatter.parse("01/01/2012");
		number = numerationService.getAndIncrement(id, ent1);
		assertEquals("2012000001", number);
	}

	@Test
	public void testResolvePattern() throws Exception {
		LOG.info("Starting testResolvePattern()");
		String id = numerationService.createNumeration("TEST4", "TEST4 Name", "This is example",
				"${entity.date?string('yyyyMMdd')}${number?string('000000')}",
				FreemakerNumerationListItem.NUMERATION_RESOLVER_FREEMARKER, "${entity.date?string('yyyy')}");
		LOG.debug("Id of new numeration: " + id);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		SampleEntity ent1 = new SampleEntity();
		ent1.date = formatter.parse("01/01/2013");
		String number = numerationService.getAndIncrement(id, ent1);
		assertEquals("20130101000001", number);
		number = numerationService.getAndIncrement(id, ent1);
		assertEquals("20130101000002", number);
		ent1.date = formatter.parse("01/01/2012");
		number = numerationService.getAndIncrement(id, ent1);
		assertEquals("20120101000001", number);

		// ok chenge the month
		ent1.date = formatter.parse("05/04/2013");
		number = numerationService.getAndIncrement(id, ent1);
		assertEquals("20130405000003", number);

		ent1.date = formatter.parse("12/12/2012");
		number = numerationService.getAndIncrement(id, ent1);
		assertEquals("20121212000002", number);
	}

	public class SampleEntity {
		private Date date;

		public Date getDate() {
			return date;
		}
	}

}
