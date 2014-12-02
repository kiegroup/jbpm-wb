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

import javax.enterprise.context.Dependent;

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
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.ht.client.editors.taskassignments.TaskAssignmentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskcomments.TaskCommentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskdetails.TaskDetailsPresenter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.client.editors.taskform.generic.GenericFormDisplayPresenter;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.jbpm.console.ng.ht.client.editors.taskadmin.TaskAdminPresenter;

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
        
        
        
        int height = TaskDetailsMultiViewImpl.this.getOffsetHeight();
        if(height == 0){
            height = 700;
        }
        tabPanel.setHeight( height+"px" );
        ScrollPanel formScrollPanel = new ScrollPanel(genericFormDisplayPresenter.getView().asWidget());
        formScrollPanel.setHeight(height+"px");
        ScrollPanel taskDetailsScrollPanel = new ScrollPanel(taskDetailsPresenter.getView().asWidget());
        taskDetailsScrollPanel.setHeight(height+"px");
        ScrollPanel assignmentsScrollPanel = new ScrollPanel(taskAssignmentsPresenter.getView().asWidget());
        assignmentsScrollPanel.setHeight(height+"px");
        ScrollPanel commentsScrollPanel = new ScrollPanel(taskCommentsPresenter.getView().asWidget());
        commentsScrollPanel.setHeight(height+"px");
        ScrollPanel taskAdminScrollPanel = new ScrollPanel( taskAdminPresenter.getView().asWidget());
        taskAdminScrollPanel.setHeight(height+"px");
        
        
        ( (HTMLPanel) tabPanel.getWidget( 0 ) ).add( formScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 1 ) ).add( taskDetailsScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 2 ) ).add( assignmentsScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 3 ) ).add( commentsScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 4 ) ).add( taskAdminScrollPanel );

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
                                 final TaskAdminPresenter taskAdminPresenter) {
        this.genericFormDisplayPresenter = genericFormDisplayPresenter;
        this.taskDetailsPresenter = taskDetailsPresenter;
        this.taskAssignmentsPresenter = taskAssignmentsPresenter;
        this.taskCommentsPresenter = taskCommentsPresenter;
        this.taskAdminPresenter = taskAdminPresenter;
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
