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
package org.openeos.jbpm.integration.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgeIdResolverBundleResourceBased implements KnowledgeIdResolver {

	private static final Logger LOG = LoggerFactory.getLogger(KnowledgeIdResolverBundleResourceBased.class);

	private static final String FILE_NAME = KnowledgeIdResolverBundleResourceBased.class.getName() + ".sessionId";

	private BundleContext bundleContext;

	public KnowledgeIdResolverBundleResourceBased(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	@Override
	public Integer getKnowledgeSessionId() {
		try {
			LOG.debug("Trying to read file {} to retrieve knowledge session id", FILE_NAME);
			File file = bundleContext.getDataFile(FILE_NAME);
			LOG.debug("The absolute path is '{}'", file.getAbsolutePath());
			if (file.exists()) {
				LOG.debug("The file exists... reading");
				ObjectInputStream st = null;
				int sessionId;
				try {
					st = new ObjectInputStream(new FileInputStream(file));
					sessionId = st.readInt();
				} finally {
					if (st != null) {
						st.close();
					}
				}
				LOG.debug("Id readed: {}", sessionId);
				return sessionId;
			}
			LOG.debug("The file not exists, return null");
		} catch (Exception e) {
			LOG.warn("An exception was thrown while trying to read session Id, return null", e);
		}
		return null;
	}

	@Override
	public void saveKnowledgeSessionId(Integer sessionId) {
		File file = bundleContext.getDataFile(FILE_NAME);
		ObjectOutputStream st = null;
		try {
			st = new ObjectOutputStream(new FileOutputStream(file));
			st.writeInt(sessionId);
		} catch (FileNotFoundException e) {
			LOG.warn("Exception was thrown while trying to save session id in filesystem", e);
		} catch (IOException e) {
			LOG.warn("Exception was thrown while trying to save session id in filesystem", e);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (IOException e) {
					LOG.warn("Exception was thrown while trying to close stream", e);
				}
			}
		}
	}
}
