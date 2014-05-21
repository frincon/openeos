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

import org.osgi.framework.Bundle;

public class HeaderBundleFilter implements BundleFilter<String> {

	private String header;

	public HeaderBundleFilter(String header) {
		this.header = header;
	}

	@Override
	public String filterBundle(Bundle bundle) {
		String headerValue = bundle.getHeaders().get(header);
		if (headerValue != null) {
			return headerValue;
		} else {
			return null;
		}
	}

}
