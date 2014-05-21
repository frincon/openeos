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
package org.openeos.lanterna.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SessionAware;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import org.openeos.services.dictionary.IDictionaryService;
import org.openeos.services.ui.IMenuContributor;
import org.openeos.services.ui.IWindowManagerService;
import org.openeos.services.ui.UIDAOService;
import org.openeos.services.ui.WindowActionService;
import org.openeos.services.ui.model.IMenuDefinition;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.text.LanternaSshTerminal;

public class LanternaSshFactory implements Factory<Command> {

	private static final Logger LOG = LoggerFactory.getLogger(LanternaSshFactory.class);

	private class LanternaSshCommand implements Command, SessionAware {

		private ServerSession session;
		private OutputStream errorStream;
		private OutputStream outputStream;
		private InputStream inputStream;
		private ExitCallback exitCallback;

		private boolean closed;

		@Override
		public void setSession(ServerSession session) {
			this.session = session;
		}

		@Override
		public void destroy() {
			if (!closed) {
				closed = true;
				// ShellFactoryImpl.flush(out, err);
				// ShellFactoryImpl.close(in, out, err);
				exitCallback.onExit(0);
			}
		}

		@Override
		public void setErrorStream(OutputStream errorStream) {
			this.errorStream = errorStream;
		}

		@Override
		public void setExitCallback(ExitCallback exitCallback) {
			this.exitCallback = exitCallback;
		}

		@Override
		public void setInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		@Override
		public void setOutputStream(OutputStream outputStream) {
			this.outputStream = outputStream;
		}

		@Override
		public void start(Environment environment) throws IOException {

			//TODO Make with prototype bean
			Terminal terminal = new LanternaSshTerminal(inputStream, outputStream, Charset.forName(System
					.getProperty("file.encoding")), environment);
			LanternaRunner runner = new LanternaRunner();
			runner.setTerminal(terminal);
			runner.setSession(session);
			runner.setRootMenuList(getRootMenus());
			runner.setWindowManagerService(windowManagerService);
			runner.setDictionaryService(dictionaryService);
			runner.setUidaoService(uidaoService);
			runner.setWindowActionService(windowActionService);
			taskExecutor.execute(runner);

		}

	}

	private IWindowManagerService windowManagerService;
	private TaskExecutor taskExecutor;
	private List<IMenuContributor> menuContributorList = new ArrayList<IMenuContributor>();
	private IDictionaryService dictionaryService;
	private UIDAOService uidaoService;
	private WindowActionService windowActionService;

	public void setWindowActionService(WindowActionService windowActionService) {
		this.windowActionService = windowActionService;
	}

	public void setUidaoService(UIDAOService uidaoService) {
		this.uidaoService = uidaoService;
	}

	public void setWindowManagerService(IWindowManagerService windowManagerService) {
		this.windowManagerService = windowManagerService;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setDictionaryService(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public Command create() {
		return new LanternaSshCommand();
	}

	private List<IMenuDefinition> getRootMenus() {
		List<IMenuDefinition> ret = new ArrayList<IMenuDefinition>();
		Collections.sort(menuContributorList, new Comparator<IMenuContributor>() {

			@Override
			public int compare(IMenuContributor o1, IMenuContributor o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});

		for (IMenuContributor contributor : menuContributorList) {
			ret.addAll(contributor.getRootMenuDefinitionList());
		}
		return ret;
	}

	public void bindMenuContributor(IMenuContributor menuContributor) {
		this.menuContributorList.add(menuContributor);
	}

	public void unbindMenuContributor(IMenuContributor menuContributor) {
		this.menuContributorList.remove(menuContributor);
		// TODO Menu of windows must chenged
		LOG.warn("TODO Menu contributar has been unbind... Actual sessions not shown correct menu.");
	}

}
