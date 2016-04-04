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

import javax.enterprise.context.Dependent;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.menu.RestoreDefaultFiltersMenuBuilder;
import org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTasksListGridPresenter;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = DroolsTasksListGridPresenter.SCREEN_ID)
public class DroolsTasksListGridPresenter extends DataSetTasksListGridPresenter {

    public static final String SCREEN_ID = "Drools Tasks List";

    public DroolsTasksListGridPresenter() {
        super();
    }

    public DroolsTasksListGridPresenter(DataSetTaskListView view, Caller<TaskLifeCycleService> taskOperationsService, DataSetQueryHelper dataSetQueryHelper, DataSetQueryHelper dataSetQueryHelperDomainSpecific, User identity) {
        super(view, taskOperationsService, dataSetQueryHelper, dataSetQueryHelperDomainSpecific, identity);
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelCustomMenu(refreshSelectorMenuBuilder).endMenu()
                .newTopLevelCustomMenu(new RestoreDefaultFiltersMenuBuilder(this)).endMenu()
                .build();
    }

}