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
package org.openeos.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.osgi.framework.Bundle;

public class ResourceExistsBundleFilter implements BundleFilter<Collection<URL>> {

	private String path;
	private String extension;

	public ResourceExistsBundleFilter(String path, String extension) {
		this.path = path;
		this.extension = extension;
	}

	@Override
	public Collection<URL> filterBundle(Bundle bundle) {
		Collection<URL> result = new ArrayList<URL>();
		for (String pathPattern : getPathPatterns(bundle)) {
			if (isAbsolutePathFileName(pathPattern)) {
				result.add(bundle.getEntry(pathPattern));
			} else if (isPathPatternFileName(pathPattern)) {
				Enumeration<URL> enumUrls = bundle.findEntries(pathFromPattern(pathPattern), filePatternFromPattern(pathPattern),
						false);
				while (enumUrls != null && enumUrls.hasMoreElements()) {
					result.add(enumUrls.nextElement());
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}
		if (!result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	private String pathFromPattern(String pathPattern) {
		return pathPattern.substring(0, pathPattern.lastIndexOf("/") + 1);
	}

	private String filePatternFromPattern(String pathPattern) {
		return pathPattern.substring(pathPattern.lastIndexOf("/") + 1);
	}

	private boolean isPathPatternFileName(String pathPattern) {
		return pathPattern.contains("*") && !pathPattern.endsWith("/") && pathPattern.indexOf("*") > pathPattern.lastIndexOf("/");
	}

	private boolean isAbsolutePathFileName(String pathPattern) {
		return !pathPattern.contains("*") && !pathPattern.endsWith("/");
	}

	protected String[] getPathPatterns(Bundle bundle) {
		if (!path.endsWith("/")) {
			return new String[] { path + "/*." + extension };
		} else {
			return new String[] { path + "*." + extension };
		}
	}
}
