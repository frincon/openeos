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
package org.openeos.services.ui.internal;

import java.sql.BatchUpdateException;
import java.sql.SQLException;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.spi.ConversionContext;

import org.openeos.services.ui.ThrowableToMessageService;
import org.openeos.services.ui.UnencapsulateExceptionResolver;

public class PostgreSQLDialectBatchUpdateResolver extends UnencapsulateExceptionResolver {

	private ConversionContext conversionContext;

	public PostgreSQLDialectBatchUpdateResolver(ThrowableToMessageService throwableToMessageService,
			ConversionContext conversionContext) {
		super(throwableToMessageService);
		this.conversionContext = conversionContext;
	}

	@Override
	protected Throwable unencapsuleThrowable(Throwable t) {
		if (t instanceof ConstraintViolationException) {
			ConstraintViolationException constrainEx = (ConstraintViolationException) t;
			if (constrainEx.getConstraintName() == null) {
				SQLException sqlEx = constrainEx.getSQLException();
				if (sqlEx instanceof BatchUpdateException) {
					SQLException other = sqlEx.getNextException();
					if (other != null) {
						String constraintName = conversionContext.getViolatedConstraintNameExtracter().extractConstraintName(other);
						if (constraintName != null) {
							return new ConstraintViolationException(t.getMessage(), sqlEx, constraintName);
						}
					}
				}
			}
		}
		return null;
	}

}
