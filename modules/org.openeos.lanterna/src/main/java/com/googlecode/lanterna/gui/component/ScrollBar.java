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

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalSize;

public class ScrollBar extends AbstractInteractableComponent {

	///////////////////////////////////////////
	// Constants
	///////////////////////////////////////////
	private static final int CHARACTERS_ARROWS = 2;

	///////////////////////////////////////////
	// Inner Fields
	///////////////////////////////////////////

	private ScrollbarOrientation orientation = ScrollbarOrientation.HORIZONTAL;
	private int value = 1, visible = 1, minimum = 1, maximum = 1;

	///////////////////////////////////////////
	// Nested Classes/Enums
	///////////////////////////////////////////

	public enum ScrollbarOrientation {
		HORIZONTAL, VERTICAL
	}

	///////////////////////////////////////////
	// Constructors
	///////////////////////////////////////////

	public ScrollBar() {
		super();
	}

	public ScrollBar(ScrollbarOrientation orientation) {
		super();
		this.orientation = orientation;
	}

	public ScrollBar(ScrollbarOrientation orientation, int value, int visible, int minimum, int maximum) {
		super();
		this.orientation = orientation;
	}

	///////////////////////////////////////////
	// Properties Get/Set
	///////////////////////////////////////////

	public ScrollbarOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(ScrollbarOrientation orientation) {
		this.orientation = orientation;
		invalidate();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		if (value > maximum || value < minimum) {
			throw new IllegalArgumentException("The value is not consistent with maximum and/or minimum");
		}
		this.value = value;
		invalidate();
	}

	public int getVisible() {
		return visible;
	}

	public int getMinimum() {
		return minimum;
	}

	public int getMaximum() {
		return maximum;
	}

	///////////////////////////////////////////
	// Methods
	///////////////////////////////////////////

	public void setValues(int value, int visible, int minimum, int maximum) {
		if (value < minimum || value > maximum || visible > (maximum - minimum) || minimum > maximum) {
			throw new IllegalArgumentException("The values provided are not consistent.");
		}
		this.value = value;
		this.visible = visible;
		this.minimum = minimum;
		this.maximum = maximum;
		invalidate();
	}

	@Override
	public Result keyboardInteraction(Key key) {
		return Result.EVENT_NOT_HANDLED;
	}

	@Override
	public void repaint(TextGraphics graphics) {
		int totalSpace;
		totalSpace = orientation == ScrollbarOrientation.HORIZONTAL ? graphics.getWidth() : graphics.getHeight();
		if (totalSpace < CHARACTERS_ARROWS) {
			//Paint nothing, there arent enough space
			return;
		}

		int middle = (int) Math
				.floor((orientation == ScrollbarOrientation.HORIZONTAL ? graphics.getHeight() : graphics.getWidth()) * 1.0 / 2 * 1.0);

		//Draw arrows
		
	}

	@Override
	protected TerminalSize calculatePreferredSize() {
		int columns = 0;
		int rows = 0;

		int total = CHARACTERS_ARROWS + maximum - minimum + 1;

		switch (orientation) {
		case HORIZONTAL:
			rows = 1;
			columns = total;
			break;
		case VERTICAL:
			columns = 1;
			rows = total;
			break;
		}
		return new TerminalSize(columns, rows);
	}
}
