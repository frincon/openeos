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
package org.openeos.services.ui;


public abstract class UnencapsulateExceptionResolver implements ThrowableToMessageResolver {

	private ThrowableToMessageService throwableToMessageService;

	public UnencapsulateExceptionResolver(ThrowableToMessageService throwableToMessageService) {
		this.throwableToMessageService = throwableToMessageService;
	}

	@Override
	public String resolveMessage(Throwable t) {
		Throwable newThrowable = unencapsuleThrowable(t);

		if (newThrowable != null) {
			return throwableToMessageService.resolveMessage(newThrowable);
		} else {
			return null;
		}
	}

	protected abstract Throwable unencapsuleThrowable(Throwable t);

}
