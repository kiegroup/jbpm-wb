/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.dashboard.renderer.client.panel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.ga.events.ServerTemplateSelected;
import org.jbpm.console.ng.gc.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardConstants;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = DashboardScreen.SCREEN_ID)
public class DashboardScreen {

    protected static final String SCREEN_ID = "DashboardScreen";

    public interface View extends UberView<DashboardScreen> {
    }

    @Inject
    private ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    @Inject
    ProcessDashboard processDashboard;

    @Inject
    TaskDashboard taskDashboard;

    @Inject
    DashboardView view;

    @Inject
    PlaceManager placeManager;

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return DashboardConstants.INSTANCE.processDashboardName();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public IsWidget getProcessDashboard() {
        return processDashboard;
    }

    public IsWidget getTaskDashboard() {
        return taskDashboard;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelCustomMenu(serverTemplateSelectorMenuBuilder)
                .endMenu()
                .build();
    }

    public void onServerTemplateSelected(@Observes final ServerTemplateSelected serverTemplateSelected ) {
        //Refresh view
        placeManager.closePlace(SCREEN_ID);
        placeManager.goTo(SCREEN_ID);
    }

}