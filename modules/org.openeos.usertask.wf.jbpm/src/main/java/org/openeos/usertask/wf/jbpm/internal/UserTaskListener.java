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
package org.openeos.usertask.wf.jbpm.internal;

import java.util.HashSet;
import java.util.Set;

import org.drools.runtime.StatefulKnowledgeSession;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openeos.usertask.eventadmin.Constants;

public class UserTaskListener implements EventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(UserTaskListener.class);

	private Set<Long> abortingRightNow = new HashSet<Long>();

	private StatefulKnowledgeSession session;

	public UserTaskListener(StatefulKnowledgeSession session) {
		this.session = session;
	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(Constants.TOPIC_COMPLETED) || event.getTopic().equals(Constants.TOPIC_DELETED)) {
			LOG.debug("User task completed or deleted received, checking if is associated with work item");
			String workItemString = (String) event.getProperty(UserTaskHandler.WORKITEM_ID_KEY);
			if (workItemString != null) {
				LOG.debug("The property {} exists... completing or canceling work item", UserTaskHandler.WORKITEM_ID_KEY);
				long workItemId = Long.parseLong(workItemString);
				if (event.getTopic().equals(Constants.TOPIC_COMPLETED)) {
					LOG.debug("Completing work item");
					session.getWorkItemManager().completeWorkItem(workItemId, null);
				} else {
					if (abortingRightNow.contains(workItemId)) {
						LOG.debug("Not aborting work item because it has been aborted right now");
					} else {
						LOG.debug("Aborting work item");
						session.getWorkItemManager().abortWorkItem(workItemId);
					}
				}
			}

		}
	}

	public void addWorkItemAborting(long id) {
		abortingRightNow.add(id);
	}

	public void removeWorkItemAborting(long id) {
		abortingRightNow.remove(id);
	}

}
