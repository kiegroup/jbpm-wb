/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.ht.client.editors.taskslist.grid;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTasksListGridPresenter;
import org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTasksListGridPresenterTest;
import org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTasksListGridViewImpl;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.junit.Test;
import org.uberfire.mocks.CallerMock;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.assertEquals;

public class DroolsTasksListGridPresenterTest extends DataSetTasksListGridPresenterTest {

    @Override
    protected DataSetTasksListGridPresenter createPresenter(final DataSetTasksListGridViewImpl viewMock,
                                                            final CallerMock<TaskLifeCycleService> callerMockTaskOperationsService,
                                                            final DataSetQueryHelper dataSetQueryHelperMock,
                                                            final DataSetQueryHelper dataSetDomainDataQueryHelperMock,
                                                            final User identity) {
        return new DroolsTasksListGridPresenter(viewMock, callerMockTaskOperationsService,
                dataSetQueryHelperMock, dataSetDomainDataQueryHelperMock, identity);
    }

    @Test
    public void testMenus(){
        final Menus menus = presenter.getMenus();

        assertEquals(3, menus.getItems().size());
    }

}
