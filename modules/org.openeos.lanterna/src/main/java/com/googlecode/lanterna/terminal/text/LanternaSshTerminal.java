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
package com.googlecode.lanterna.terminal.text;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.lanterna.input.LanternaSshProfile;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.text.ANSITerminal;

public class LanternaSshTerminal extends ANSITerminal {

	private static final Logger LOG = LoggerFactory.getLogger(LanternaSshTerminal.class);

	private Environment environment;

	public LanternaSshTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset, Environment env) {
		super(terminalInput, terminalOutput, terminalCharset);
		this.environment = env;
		addInputProfile(new LanternaSshProfile(env));
	}

	@Override
	@Deprecated
	public TerminalSize queryTerminalSize() {
		try {
			int cols = Integer.parseInt(environment.getEnv().get(Environment.ENV_COLUMNS));
			int rows = Integer.parseInt(environment.getEnv().get(Environment.ENV_LINES));
			TerminalSize size = new TerminalSize(cols, rows);
			return size;
		} catch (Exception ex) {
			LOG.error("Error getting terminal size", ex);
			return super.getTerminalSize();
		}
	}

	@Override
	public TerminalSize getTerminalSize() {
		try {
			int cols = Integer.parseInt(environment.getEnv().get(Environment.ENV_COLUMNS));
			int rows = Integer.parseInt(environment.getEnv().get(Environment.ENV_LINES));
			TerminalSize size = new TerminalSize(cols, rows);
			return size;
		} catch (Exception ex) {
			LOG.error("Error getting terminal size", ex);
			return super.getTerminalSize();
		}

	}

	@Override
	public void setCBreak(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEcho(boolean arg0) {
		// TODO Auto-generated method stub

	}

}
