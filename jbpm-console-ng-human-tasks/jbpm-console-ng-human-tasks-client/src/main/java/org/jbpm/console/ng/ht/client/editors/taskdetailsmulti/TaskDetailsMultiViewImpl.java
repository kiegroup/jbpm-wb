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

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.MINI;

import javax.enterprise.context.Dependent;

import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.ht.client.editors.taskadmin.TaskAdminPresenter;
import org.jbpm.console.ng.ht.client.editors.taskassignments.TaskAssignmentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskcomments.TaskCommentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskdetails.TaskDetailsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskprocesscontext.TaskProcessContextPresenter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.client.editors.taskform.generic.GenericFormDisplayPresenter;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class TaskDetailsMultiViewImpl extends AbstractTabbedDetailsView<TaskDetailsMultiPresenter>
        implements TaskDetailsMultiPresenter.TaskDetailsMultiView {

    interface Binder
            extends
            UiBinder<Widget, TaskDetailsMultiViewImpl> {

    }

    private GenericFormDisplayPresenter genericFormDisplayPresenter;

    private TaskDetailsPresenter taskDetailsPresenter;

    private TaskAssignmentsPresenter taskAssignmentsPresenter;

    private TaskCommentsPresenter taskCommentsPresenter;
    
    private TaskAdminPresenter taskAdminPresenter;

    private TaskProcessContextPresenter taskProcessContextPresenter;
    @Override
    public void init( final TaskDetailsMultiPresenter presenter ) {
        super.init( presenter );
    }

    @Override
    public void initTabs() {
        tabPanel.addTab( "Generic Form Display", Constants.INSTANCE.Work() );
        tabPanel.addTab( "Task Details", Constants.INSTANCE.Details() );
        tabPanel.addTab( "Task Assignments", Constants.INSTANCE.Assignments() );
        tabPanel.addTab( "Task Comments", Constants.INSTANCE.Comments() );
        tabPanel.addTab( "Task Admin", Constants.INSTANCE.Task_Admin());
        tabPanel.addTab( "Process_Context", Constants.INSTANCE.Process_Context());
        
        tabPanel.setHeight( "700px" );

        ( (HTMLPanel) tabPanel.getWidget( 0 ) ).add( genericFormDisplayPresenter.getView() );
        ( (HTMLPanel) tabPanel.getWidget( 1 ) ).add( taskDetailsPresenter.getView() );
        ( (HTMLPanel) tabPanel.getWidget( 2 ) ).add( taskAssignmentsPresenter.getView() );
        ( (HTMLPanel) tabPanel.getWidget( 3 ) ).add( taskCommentsPresenter.getView() );
        ( (HTMLPanel) tabPanel.getWidget( 4 ) ).add( taskAdminPresenter.getView() );
        ( (HTMLPanel) tabPanel.getWidget( 5 ) ).add( taskProcessContextPresenter.getView() );

        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection( SelectionEvent<Integer> event ) {
                if ( event.getSelectedItem() == 1 ) {
                    taskDetailsPresenter.refreshTask();
                } else if ( event.getSelectedItem() == 2 ) {
                    taskAssignmentsPresenter.refreshTaskPotentialOwners();
                } else if ( event.getSelectedItem() == 3 ) {
                    taskCommentsPresenter.refreshComments();
                }else if ( event.getSelectedItem() == 3 ) {
                    taskAdminPresenter.refreshTaskPotentialOwners();
                }
                else if ( event.getSelectedItem() == 4 ) {
                    taskProcessContextPresenter.refreshProcessContextOfTask();
                }
            }
        } );
    }

    @Override
    public Button getCloseButton() {
        return new Button() {
            {
                setIcon( IconType.REMOVE );
                setSize( MINI );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.closeDetails();
                    }
                } );
            }
        };
    }

    @Override
    public void setupPresenters( final GenericFormDisplayPresenter genericFormDisplayPresenter,
                                 final TaskDetailsPresenter taskDetailsPresenter,
                                 final TaskAssignmentsPresenter taskAssignmentsPresenter,
                                 final TaskCommentsPresenter taskCommentsPresenter,
                                 final TaskAdminPresenter taskAdminPresenter,
                                 final TaskProcessContextPresenter taskProcessContextPresenter) {
        this.genericFormDisplayPresenter = genericFormDisplayPresenter;
        this.taskDetailsPresenter = taskDetailsPresenter;
        this.taskAssignmentsPresenter = taskAssignmentsPresenter;
        this.taskCommentsPresenter = taskCommentsPresenter;
        this.taskAdminPresenter = taskAdminPresenter;
        this.taskProcessContextPresenter =taskProcessContextPresenter;
    }

    @Override
    public IsWidget getRefreshButton() {
        return new Button() {
            {
                setIcon( IconType.REFRESH );
                setSize( MINI );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.refresh();
                    }
                } );
            }
        };
    }

}
