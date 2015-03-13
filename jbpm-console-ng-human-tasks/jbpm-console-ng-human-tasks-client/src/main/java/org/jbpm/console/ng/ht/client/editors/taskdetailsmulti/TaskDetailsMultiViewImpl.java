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
import org.jbpm.console.ng.ht.client.editors.taskform.TaskFormPresenter;
import org.jbpm.console.ng.ht.client.editors.taskprocesscontext.TaskProcessContextPresenter;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.ht.client.editors.taskassignments.TaskAssignmentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskcomments.TaskCommentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskdetails.TaskDetailsPresenter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.jbpm.console.ng.ht.client.editors.taskadmin.TaskAdminPresenter;


@Dependent
public class TaskDetailsMultiViewImpl extends AbstractTabbedDetailsView<TaskDetailsMultiPresenter>
        implements TaskDetailsMultiPresenter.TaskDetailsMultiView, RequiresResize {

    interface Binder
            extends
            UiBinder<Widget, TaskDetailsMultiViewImpl> {

    }

    private TaskFormPresenter taskFormPresenter;

    private TaskDetailsPresenter taskDetailsPresenter;

    private TaskAssignmentsPresenter taskAssignmentsPresenter;

    private TaskCommentsPresenter taskCommentsPresenter;
    
    private TaskAdminPresenter taskAdminPresenter;

    private TaskProcessContextPresenter taskProcessContextPresenter;
    
    private ScrollPanel formScrollPanel = new ScrollPanel();
    
    private ScrollPanel taskDetailsScrollPanel = new ScrollPanel();
    
    private ScrollPanel taskProcessContextScrollPanel = new ScrollPanel();
    
    private ScrollPanel assignmentsScrollPanel = new ScrollPanel();
    
    private ScrollPanel commentsScrollPanel = new ScrollPanel();
    
    private ScrollPanel taskAdminScrollPanel = new ScrollPanel();
    
    @Override
    public void init( final TaskDetailsMultiPresenter presenter ) {
        super.init( presenter );
    }

    @Override
    public void initTabs() {
        tabPanel.addTab( "Generic Form Display", Constants.INSTANCE.Work() );
        tabPanel.addTab( "Task Details", Constants.INSTANCE.Details() );
        tabPanel.addTab( "Process Context", Constants.INSTANCE.Process_Context());
        tabPanel.addTab( "Task Assignments", Constants.INSTANCE.Assignments() );
        tabPanel.addTab( "Task Comments", Constants.INSTANCE.Comments() );
        tabPanel.addTab( "Task Admin", Constants.INSTANCE.Task_Admin());
        
       
        formScrollPanel.add(taskFormPresenter.getView());
        
        taskDetailsScrollPanel.add(taskDetailsPresenter.getView());
        
        taskProcessContextScrollPanel.add(taskProcessContextPresenter.getView());
        
        assignmentsScrollPanel.add(taskAssignmentsPresenter.getView());
        
        commentsScrollPanel.add(taskCommentsPresenter.getView());
        
        taskAdminScrollPanel.add( taskAdminPresenter.getView().asWidget());
        
        
        
        
        ( (HTMLPanel) tabPanel.getWidget( 0 ) ).add( formScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 1 ) ).add( taskDetailsScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 2 ) ).add( taskProcessContextScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 3 ) ).add( assignmentsScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 4 ) ).add( commentsScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 5 ) ).add( taskAdminScrollPanel );


        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection( SelectionEvent<Integer> event ) {
                if ( event.getSelectedItem() == 0 ) {
                    
                } else if ( event.getSelectedItem() == 1 ) {
                    taskDetailsPresenter.refreshTask();
                } else if ( event.getSelectedItem() == 2 ) {
                    taskProcessContextPresenter.refreshProcessContextOfTask();
                }else if ( event.getSelectedItem() == 3 ) {
                    taskAssignmentsPresenter.refreshTaskPotentialOwners();
                } else if ( event.getSelectedItem() == 4 ) {
                    taskCommentsPresenter.refreshComments();
                }else if ( event.getSelectedItem() == 5 ) {
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
                setTitle( Constants.INSTANCE.Close() );
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
    public void setupPresenters( final TaskFormPresenter taskFormPresenter,
                                 final TaskDetailsPresenter taskDetailsPresenter,
                                 final TaskAssignmentsPresenter taskAssignmentsPresenter,
                                 final TaskCommentsPresenter taskCommentsPresenter,
                                 final TaskAdminPresenter taskAdminPresenter,
                                 final TaskProcessContextPresenter taskProcessContextPresenter) {
        this.taskFormPresenter = taskFormPresenter;
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
                setTitle( Constants.INSTANCE.Refresh() );
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

    @Override
    public void onResize() {
        super.onResize(); 
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
               tabPanel.setHeight(TaskDetailsMultiViewImpl.this.getParent().getOffsetHeight()-30+"px");
              
               formScrollPanel.setHeight(TaskDetailsMultiViewImpl.this.getParent().getOffsetHeight()-30+"px");
               taskDetailsScrollPanel.setHeight(TaskDetailsMultiViewImpl.this.getParent().getOffsetHeight()-30+"px");
               taskProcessContextScrollPanel.setHeight(TaskDetailsMultiViewImpl.this.getParent().getOffsetHeight()-30+"px");
               assignmentsScrollPanel.setHeight(TaskDetailsMultiViewImpl.this.getParent().getOffsetHeight()-30+"px");
               commentsScrollPanel.setHeight(TaskDetailsMultiViewImpl.this.getParent().getOffsetHeight()-30+"px");
               taskAdminScrollPanel.setHeight(TaskDetailsMultiViewImpl.this.getParent().getOffsetHeight()-30+"px");
            }
        });
    }
    
    

}
