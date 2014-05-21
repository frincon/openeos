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
package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.terminal.TerminalSize;

public class ScrollPanel extends AbstractContainer {

	///////////////////////////////////////////
	// Inner Fields
	///////////////////////////////////////////

	private ScrollbarDisplayPolicy scrollbarDisplayPolicy = ScrollbarDisplayPolicy.AS_NEEDED;

	///////////////////////////////////////////
	// Nested Classes/Enums
	///////////////////////////////////////////

	public enum ScrollbarDisplayPolicy {
		ALWAYS, AS_NEEDED, NEVER
	}

	///////////////////////////////////////////
	// Constructors
	///////////////////////////////////////////

	public ScrollPanel() {
		super();
	}

	public ScrollPanel(ScrollbarDisplayPolicy scrollbarDisplayPolicy) {
		super();
		this.scrollbarDisplayPolicy = scrollbarDisplayPolicy;
	}

	///////////////////////////////////////////
	// Properties Get/Set
	///////////////////////////////////////////

	public ScrollbarDisplayPolicy getScrollbarDisplayPolicy() {
		return scrollbarDisplayPolicy;
	}

	public void setScrollbarDisplayPolicy(ScrollbarDisplayPolicy scrollbarDisplayPolicy) {
		this.scrollbarDisplayPolicy = scrollbarDisplayPolicy;
		invalidate(); //TODO: Call invalidate only when need
	}

	///////////////////////////////////////////
	// Methods
	///////////////////////////////////////////

	@Override
	public void addComponent(Component component, LayoutParameter... layoutParameters) {
		if (layoutParameters.length > 0) {
			throw new IllegalArgumentException("layoutParamters must be null adding component in ScrollPanel");
		}
		removeAllComponents();
		super.addComponent(component, layoutParameters);
	}

	@Override
	public boolean isScrollable() {
		return true;
	}

	@Override
	public void repaint(TextGraphics graphics) {

	}

	@Override
	protected TerminalSize calculatePreferredSize() {
		if (getComponentCount() == 1) {
			TerminalSize size = getComponentAt(0).getPreferredSize();
			if (scrollbarDisplayPolicy == ScrollbarDisplayPolicy.ALWAYS) {
				size = new TerminalSize(size.getColumns() + 1, size.getRows() + 1);
			}
			return size;
		} else {
			return new TerminalSize(0, 0);
		}
	}

}
