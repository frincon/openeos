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
package org.openeos.services.ui.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.LinkedList;

import org.junit.Test;

import org.openeos.services.ui.WindowContributor;
import org.openeos.services.ui.internal.WindowManagerServiceImpl;
import org.openeos.services.ui.model.IWindowDefinition;
import org.openeos.services.ui.model.IMenuDefinition.MenuType;

public class TestWindowManagerService {

	@Test
	public void test_windowManagerService() {
		WindowManagerServiceImpl windowManagerServiceImpl = new WindowManagerServiceImpl();

		IWindowDefinition window1 = mock(IWindowDefinition.class);
		IWindowDefinition window2 = mock(IWindowDefinition.class);
		when(window1.getId()).thenReturn("ID1");
		when(window2.getId()).thenReturn("ID2");

		System.out.println(MenuType.MENU.toString());

		LinkedList<IWindowDefinition> list = new LinkedList<IWindowDefinition>();
		list.add(window1);
		list.add(window2);

		WindowContributor contributor = mock(WindowContributor.class);
		when(contributor.getWindowDefinitionList()).thenReturn(list);

		windowManagerServiceImpl.bindWindowContributor(contributor);

		assertSame(window1, windowManagerServiceImpl.getWindowDefinition("ID1"));
		assertSame(window2, windowManagerServiceImpl.getWindowDefinition("ID2"));
	}

}
