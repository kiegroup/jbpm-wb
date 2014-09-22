/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.ht.client.editors.taskdetailsmulti;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsPresenter;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView.TabbedDetailsView;
import org.jbpm.console.ng.ht.client.editors.taskadmin.TaskAdminPresenter;
import org.jbpm.console.ng.ht.client.editors.taskassignments.TaskAssignmentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskcomments.TaskCommentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskdetails.TaskDetailsPresenter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.client.editors.taskform.generic.GenericFormDisplayPresenter;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
@WorkbenchScreen(identifier = "Task Details Multi", preferredWidth = 500)
public class TaskDetailsMultiPresenter extends AbstractTabbedDetailsPresenter {

    public interface TaskDetailsMultiView
            extends TabbedDetailsView<TaskDetailsMultiPresenter> {

        void setupPresenters( final GenericFormDisplayPresenter genericFormDisplayPresenter,
                              final TaskDetailsPresenter taskDetailsPresenter,
                              final TaskAssignmentsPresenter taskAssignmentsPresenter,
                              final TaskCommentsPresenter taskCommentsPresenter,
                              final TaskAdminPresenter taskAdminPresenter);

        IsWidget getRefreshButton();

        IsWidget getCloseButton();
    }

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

    @Inject
    private GenericFormDisplayPresenter genericFormDisplayPresenter;

    @Inject
    private TaskDetailsPresenter taskDetailsPresenter;

    @Inject
    private TaskAssignmentsPresenter taskAssignmentsPresenter;

    @Inject
    private TaskCommentsPresenter taskCommentsPresenter;

    @Inject
    private TaskDetailsMultiView view;
    
    @Inject
    private TaskAdminPresenter taskAdminPresenter;

    @PostConstruct
    public void init() {
        view.setupPresenters( genericFormDisplayPresenter, taskDetailsPresenter, taskAssignmentsPresenter, taskCommentsPresenter, taskAdminPresenter );
    }

    @WorkbenchPartView
    public UberView<TaskDetailsMultiPresenter> getView() {
        return view;
    }

    @DefaultPosition
    public Position getPosition() {
        return Position.EAST;
    }


    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.Details();
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        super.onStartup( place );
    }

    public void onTaskSelectionEvent( @Observes final TaskSelectionEvent event ) {
        selectedItemId = String.valueOf( event.getTaskId() );
        selectedItemName = event.getTaskName();
        genericFormDisplayPresenter.setup( event.getTaskId(), "none", "none", new Command() {
            @Override
            public void execute() {
                closeDetails();
            }
        } );

        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( this.place, String.valueOf( selectedItemId ) + " - " + selectedItemName ) );
        if (event.isForAdmin()) {
            view.getTabPanel().getTabWidget(4).getParent().setVisible(true);
        } else {
            view.getTabPanel().getTabWidget(4).getParent().setVisible(false);
        }
        view.getTabPanel().selectTab(0);
    }

    public void refresh() {
        taskSelected.fire( new TaskSelectionEvent( Long.valueOf( selectedItemId ), selectedItemName ) );
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return view.getRefreshButton();
                            }
                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return view.getCloseButton();
                            }
                        };
                    }
                } ).endMenu().build();
    }
}
