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
package org.openeos.lanterna.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.gui.component.AbstractInteractableComponent;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.TerminalSize;

public class MultiColumnListBox extends AbstractInteractableComponent {

	private static final int HEADER_HEIGHT = 2;
	private static final int SCROLLBAR_WIDTH = 0;
	private static final int SCROLLBAR_HEIGHT = 0;
	private static final int SEPARATOR_WIDTH = 1;

	private List<MultiColumnListBoxColumn> columnList = new ArrayList<MultiColumnListBoxColumn>();

	private List<String[]> rowList = new ArrayList<String[]>();

	private int selectedIndex = -1;
	private int horizontalOffset = 0;

	public void addColumn(String header) {
		MultiColumnListBoxColumn column = new MultiColumnListBoxColumn(header, 40);
		columnList.add(column);
		invalidate();
	}

	public void addRow(String[] columns) {
		rowList.add(columns);
		if (selectedIndex == -1) {
			selectedIndex = 0;
		}
		invalidate();
	}

	@Override
	protected TerminalSize calculatePreferredSize() {
		// Get size of colums
		int width = SEPARATOR_WIDTH;
		for (MultiColumnListBoxColumn column : columnList) {
			width += column.getSize() + SEPARATOR_WIDTH;
		}
		int height = HEADER_HEIGHT + rowList.size();
		return new TerminalSize(width, height);
	}

	@Override
	public Result keyboardInteraction(Key key) {
		try {
			switch (key.getKind()) {
			case Tab:
				return Result.NEXT_INTERACTABLE_RIGHT;
			case ArrowRight:
				horizontalOffset++;
				break;

			case ReverseTab:
				return Result.PREVIOUS_INTERACTABLE_LEFT;

			case ArrowLeft:
				if (horizontalOffset > 0) {
					horizontalOffset--;
				} else {
					return Result.PREVIOUS_INTERACTABLE_LEFT;
				}
				break;
			case ArrowDown:
				if (selectedIndex < rowList.size() - 1) {
					selectedIndex++;
				} else {
					return Result.NEXT_INTERACTABLE_DOWN;
				}
				break;

			case ArrowUp:
				if (selectedIndex > 0) {
					selectedIndex--;
				} else {
					return Result.PREVIOUS_INTERACTABLE_UP;
				}
				break;

			default:
				return Result.EVENT_NOT_HANDLED;
			}
			return Result.EVENT_HANDLED;
		} finally {
			invalidate();
		}
	}

	private void drawStringOffset(TextGraphics graphics, int horizontalOffset, int column, int row, String string,
			ScreenCharacterStyle... styles) {
		if (column < horizontalOffset) {
			if (string.length() > (horizontalOffset - column)) {
				graphics.drawString(0, row, string.substring(horizontalOffset - column), styles);
			}
		} else {
			graphics.drawString(column - horizontalOffset, row, string, styles);
		}
	}

	@Override
	public void repaint(TextGraphics graphics) {
		graphics.applyTheme(getListItemThemeDefinition(graphics.getTheme()));
		graphics.fillArea(' ');

		TerminalSize preferredSize = getPreferredSize();
		boolean fitsHorizontal = fitsHorizontal(graphics);
		boolean fitsVertical = fitsVertical(graphics);

		if (fitsHorizontal) {
			horizontalOffset = 0;
		} else if (preferredSize.getColumns() - horizontalOffset < graphics.getWidth()) {
			horizontalOffset = preferredSize.getColumns() - graphics.getWidth() - SCROLLBAR_WIDTH;
		}

		int actualPosition = 0;
		for (MultiColumnListBoxColumn column : columnList) {
			String header = column.getHeader();
			if (header.length() > column.getSize()) {
				header = header.substring(0, column.getSize());
			}
			drawStringOffset(graphics, horizontalOffset, actualPosition, 0, header);
			drawStringOffset(graphics, horizontalOffset, actualPosition, 1,
					StringUtils.repeat(ACS.SINGLE_LINE_HORIZONTAL + "", column.getSize()));

			actualPosition += column.getSize();
			if (actualPosition - horizontalOffset <= graphics.getWidth()) {
				drawStringOffset(graphics, horizontalOffset, actualPosition, 0, ACS.SINGLE_LINE_VERTICAL + "");
				if (columnList.indexOf(column) == columnList.size() - 1) {
					drawStringOffset(graphics, horizontalOffset, actualPosition, 1, ACS.SINGLE_LINE_T_LEFT + "");
				} else {
					drawStringOffset(graphics, horizontalOffset, actualPosition, 1, ACS.SINGLE_LINE_CROSS + "");
				}
				actualPosition++;
			}
			if (actualPosition - horizontalOffset > graphics.getWidth()) {
				break;
			}
		}
		int headerHeight = 2;
		for (int i = 0; i < graphics.getHeight() && i < rowList.size(); i++) {
			actualPosition = 0;
			if (i >= rowList.size()) {
				break;
			}
			if (hasFocus() && i == selectedIndex) {
				graphics.applyTheme(graphics.getTheme().getDefinition(Theme.Category.LIST_ITEM_SELECTED));
			} else {
				graphics.applyTheme(graphics.getTheme().getDefinition(Theme.Category.LIST_ITEM));

			}
			for (MultiColumnListBoxColumn column : columnList) {
				String[] row = rowList.get(i);
				String actual = row[columnList.indexOf(column)];
				if (actual.length() > column.getSize()) {
					actual = actual.substring(0, column.getSize());
				} else {
					actual = StringUtils.rightPad(actual, column.getSize());
				}
				drawStringOffset(graphics, horizontalOffset, actualPosition, i + headerHeight, actual);
				actualPosition += column.getSize();
				drawStringOffset(graphics, horizontalOffset, actualPosition, i + headerHeight, ACS.SINGLE_LINE_VERTICAL + "");
				actualPosition++;
				if (actualPosition - horizontalOffset > graphics.getWidth()) {
					break;
				}
			}
		}
	}

	private boolean fitsVertical(TextGraphics graphics) {
		return graphics.getHeight() >= getPreferredSize().getRows();
	}

	private boolean fitsHorizontal(TextGraphics graphics) {
		return graphics.getWidth() > getPreferredSize().getColumns();
	}

	protected Theme.Definition getListItemThemeDefinition(Theme theme) {
		return theme.getDefinition(Category.LIST_ITEM);
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void removeAllRows() {
		this.rowList.clear();
		this.selectedIndex = -1;
		invalidate();
	}

	public void setSelectedIndex(int index) {
		this.selectedIndex = index;
	}

}
