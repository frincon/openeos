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
package org.openeos.numeration.internal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import org.openeos.dao.ListTypeService;
import org.openeos.numeration.NumerationResolver;
import org.openeos.numeration.NumerationService;
import org.openeos.numeration.NumerationServiceException;
import org.openeos.numeration.SequenceDAO;
import org.openeos.numeration.model.Sequence;
import org.openeos.numeration.model.SequenceInstance;
import org.openeos.numeration.model.list.NumerationResolverListItem;

public class NumerationServiceImpl implements NumerationService {

	private SequenceDAO sequenceDAO;

	private Map<String, NumerationResolver> mapResolvers = new HashMap<String, NumerationResolver>();

	public void setSequenceDAO(SequenceDAO sequenceDAO) {
		this.sequenceDAO = sequenceDAO;
	}

	@Override
	@Transactional
	public String getAndIncrement(String sequenceId) {
		return getAndIncrement(sequenceId, null);
	}

	@Override
	@Transactional
	public String createNumeration(String value, String name, String description, String pattern,
			NumerationResolverListItem numerationResolverId) throws NumerationServiceException {
		return createNumeration(value, name, description, pattern, numerationResolverId, null);
	}

	public void bindNumerationResolver(NumerationResolver resolver) {
		if (resolver != null) {
			mapResolvers.put(resolver.getNumerationResolverId(), resolver);
		}
	}

	public void unbindNumerationResolver(NumerationResolver resolver) {
		if (resolver != null) {
			mapResolvers.remove(resolver.getNumerationResolverId());
		}
	}

	private NumerationResolver getNumerationResolver(String numerationResolverId) {
		return mapResolvers.get(numerationResolverId);
	}

	@Override
	@Transactional
	public String getAndIncrement(String sequenceId, Object entity) {
		Sequence seq = sequenceDAO.read(sequenceId);
		sequenceDAO.refresh(seq);
		NumerationResolver resolver = getNumerationResolver(seq.getNumerationResolverId().getValue());
		if (resolver == null) {
			throw new NumerationServiceException(String.format("The resolver with id '%s' not fond", seq.getNumerationResolverId()));
		}
		String resolved = resolver.resolveNumeration(
				seq.getPattertForResolver() == null ? seq.getPattern() : seq.getPattertForResolver(), entity, new Long(0L));
		String result = null;
		// TODO Make with query
		for (SequenceInstance seqInstance : seq.getSequenceInstanceList()) {
			if (resolved.equals(seqInstance.getPatternResolved())) {
				seqInstance.setActualNumber(seqInstance.getActualNumber() + 1);
				sequenceDAO.update(seq);
				result = resolver.resolveNumeration(seq.getPattern(), entity, seqInstance.getActualNumber());
				return result;
			}
		}
		SequenceInstance seqInstance = new SequenceInstance();
		seqInstance.setActualNumber(1);
		seqInstance.setSequence(seq);
		seqInstance.setPatternResolved(resolved);
		result = resolver.resolveNumeration(seq.getPattern(), entity, seqInstance.getActualNumber());
		seq.getSequenceInstanceList().add(seqInstance);
		sequenceDAO.update(seq);
		return result;
	}

	@Override
	public String createNumeration(String value, String name, String description, String pattern,
			NumerationResolverListItem numerationResolverId, String pattertForResolver) throws NumerationServiceException {
		Sequence seq = new Sequence();
		seq.setValue(value);
		seq.setName(name);
		seq.setDescription(description);
		seq.setPattern(pattern);
		seq.setNumerationResolverId(numerationResolverId);
		seq.setPattertForResolver(pattertForResolver);
		sequenceDAO.create(seq);
		return seq.getId();
	}

}
