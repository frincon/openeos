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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.digest.DigestUtils;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.Terminal.ErrorEvent;

public class URLResource implements ApplicationResource {

	private static final long serialVersionUID = 1078580594244762329L;

	private long cacheTime = DEFAULT_CACHETIME;
	private URL url;
	private String mimeType;
	private String fileName;
	private Application application;

	public URLResource(URL url, Application application) {
		this.url = url;
		this.application = application;
		mimeType = URLConnection.guessContentTypeFromName(url.getFile());
		fileName = DigestUtils.md5Hex(url.toString()) + url.toString().substring(url.toString().lastIndexOf("."));
		application.addResource(this);
	}

	@Override
	public String getMIMEType() {
		return mimeType;
	}

	@Override
	public DownloadStream getStream() {
		try {
			DownloadStream downloadStream = new DownloadStream(url.openStream(), getMIMEType(), getFilename());
			downloadStream.setCacheTime(getCacheTime());
			return downloadStream;
		} catch (final IOException e) {
			// Log the exception using the application error handler
			getApplication().getErrorHandler().terminalError(new ErrorEvent() {

				public Throwable getThrowable() {
					return e;
				}

			});

			return null;
		}
	}

	@Override
	public Application getApplication() {
		return application;
	}

	@Override
	public String getFilename() {
		return fileName;
	}

	@Override
	public long getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

}
