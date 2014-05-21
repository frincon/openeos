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
package org.openeos.numeration.test;

import static org.openeos.test.UnoConfiguration.*;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

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
import org.openeos.numeration.NumerationServiceException;
import org.openeos.numeration.model.list.NumerationResolverListItem;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class NumerationIntegrationTestCase {

	private static final Logger LOG = LoggerFactory.getLogger(NumerationIntegrationTestCase.class);

	@Inject
	private BundleContext bc;

	@Inject
	@Filter(timeout = 100000)
	private NumerationService numerationService;

	@Configuration
	public static Option[] config() {
		return options(unoDefaultConfiguration(), unoDerbyInMemoryConfiguration(), junitBundles(), weaverVerbose(),
				bundle("reference:file:" + PathUtils.getBaseDir() + "/target/classes").startLevel(4));
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
				NumerationResolverListItem.NUMERATION_RESOLVER_DEFAULT);
		LOG.debug("Id of new numeration: " + id);
	}

	@Test
	public void testCreateUse() throws Exception {
		LOG.info("Starting testCreateNumeration()");
		String id = numerationService.createNumeration("TEST2", "TEST2 Name", "This is example", "${number}",
				NumerationResolverListItem.NUMERATION_RESOLVER_DEFAULT);
		LOG.debug("Id of new numeration: " + id);
		String number = numerationService.getAndIncrement(id);
		assertEquals("1", number);
		number = numerationService.getAndIncrement(id);
		assertEquals("2", number);
	}

	@Test
	public void testPatternForResolver() throws Exception {
		String id = numerationService.createNumeration("TEST3", "TEST3 Name", "This is example", "${number}",
				NumerationResolverListItem.NUMERATION_RESOLVER_DEFAULT, "With default resolver is not good");
		LOG.debug("Id of new numeration: " + id);
		String number = numerationService.getAndIncrement(id);
		assertEquals("1", number);
		number = numerationService.getAndIncrement(id);
		assertEquals("2", number);

	}

}
