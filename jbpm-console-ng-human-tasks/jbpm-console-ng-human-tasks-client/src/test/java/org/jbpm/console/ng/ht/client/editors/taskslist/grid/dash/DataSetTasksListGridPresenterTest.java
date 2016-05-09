/*
 * Copyright 2015 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.console.ng.ht.client.editors.taskslist.grid.AbstractTasksListGridPresenterTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetTasksListGridPresenterTest extends AbstractTasksListGridPresenterTest {

    @InjectMocks
    protected DataSetTasksListGridPresenter presenter;

    @Override
    public DataSetTasksListGridPresenter getPresenter() {
        return presenter;
    }

    @Test
    public void testMenus() {
        final Menus menus = presenter.getMenus();

        assertEquals(4, menus.getItems().size());
    }

}