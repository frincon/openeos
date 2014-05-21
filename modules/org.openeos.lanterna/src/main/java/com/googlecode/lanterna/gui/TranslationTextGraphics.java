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
package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.gui.Theme.Definition;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

public class TranslationTextGraphics implements TextGraphics {

	private TextGraphics originalGraphics;
	private TerminalPosition translation;
	private TerminalSize size;
	
	

	public TranslationTextGraphics(TextGraphics originalGraphics, TerminalPosition translation, TerminalSize size) {
		this.originalGraphics = originalGraphics;
		this.translation = translation;
		this.size = size;
	}

	@Override
	public void applyTheme(Category category) {
		originalGraphics.applyTheme(category);
	}

	@Override
	public void applyTheme(Definition themeItem) {
		originalGraphics.applyTheme(themeItem);
	}

	@Override
	public void drawString(int column, int row, String string, ScreenCharacterStyle... styles) {
		originalGraphics.drawString(column + translation.getColumn(), row + translation.getColumn(), string, styles);
	}

	@Override
	public void fillArea(char character) {
		originalGraphics.fillArea(character);
	}

	@Override
	public void fillRectangle(char character, TerminalPosition topLeft, TerminalSize rectangleSize) {
		TerminalPosition newTopLeft = new TerminalPosition(topLeft.getColumn() + translation.getColumn(), topLeft.getRow()
				+ translation.getRow());
		originalGraphics.fillRectangle(character, newTopLeft, rectangleSize);
	}

	@Override
	public Color getBackgroundColor() {
		return originalGraphics.getBackgroundColor();
	}

	@Override
	public Color getForegroundColor() {
		return originalGraphics.getForegroundColor();
	}

	@Override
	public int getHeight() {
		return size.getRows();
	}

	@Override
	public TerminalSize getSize() {
		return size;
	}

	@Override
	public Theme getTheme() {
		return originalGraphics.getTheme();
	}

	@Override
	public int getWidth() {
		return size.getColumns();
	}

	@Override
	public void setBackgroundColor(Color backgroundColor) {
		originalGraphics.setBackgroundColor(backgroundColor);
	}

	@Override
	public void setBoldMask(boolean enabledBoldMask) {
		originalGraphics.setBoldMask(enabledBoldMask);
	}

	@Override
	public void setForegroundColor(Color foregroundColor) {
		originalGraphics.setForegroundColor(foregroundColor);
	}

	@Override
	public TextGraphics subAreaGraphics(TerminalPosition terminalPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextGraphics subAreaGraphics(TerminalPosition topLeft, TerminalSize subAreaSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TerminalPosition translateToGlobalCoordinates(TerminalPosition pointInArea) {
		// TODO Auto-generated method stub
		return null;
	}

}
