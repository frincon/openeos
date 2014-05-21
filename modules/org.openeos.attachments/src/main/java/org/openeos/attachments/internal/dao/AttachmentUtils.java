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
package org.openeos.attachments.internal.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;

import org.apache.commons.io.IOUtils;
import org.hibernate.LobHelper;

public class AttachmentUtils {

	private static final String TEMP_PREFIX = "attch";
	private static final String TEMP_SUFFIX = ".tmp";

	public static Blob createBlob(LobHelper lobHelper, InputStream origInputStream) {
		try {
			final File file = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);
			OutputStream out = new FileOutputStream(file);
			IOUtils.copy(origInputStream, out);
			out.close();
			origInputStream.close();
			long len = file.length();
			file.deleteOnExit();
			InputStream in = new FileInputStream(file) {

				@Override
				public void close() throws IOException {
					super.close();
					file.delete();
				}

			};
			return lobHelper.createBlob(in, len);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
