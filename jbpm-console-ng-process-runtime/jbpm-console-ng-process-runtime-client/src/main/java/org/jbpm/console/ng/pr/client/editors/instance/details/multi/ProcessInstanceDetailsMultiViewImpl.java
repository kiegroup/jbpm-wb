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
package org.jbpm.console.ng.pr.client.editors.instance.details.multi;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.MINI;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.pr.client.editors.documents.list.ProcessDocumentListPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.details.ProcessInstanceDetailsPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.log.RuntimeLogPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.userTask.history.UserTaskListHistoryPresenter;
import org.jbpm.console.ng.pr.client.editors.variables.list.ProcessVariableListPresenter;
import org.jbpm.console.ng.pr.client.i18n.Constants;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class ProcessInstanceDetailsMultiViewImpl extends AbstractTabbedDetailsView<ProcessInstanceDetailsMultiPresenter>
        implements ProcessInstanceDetailsMultiPresenter.ProcessInstanceDetailsMultiView {

    interface Binder
            extends
            UiBinder<Widget, ProcessInstanceDetailsMultiViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    private ProcessInstanceDetailsPresenter detailsPresenter;

    @Inject
    private ProcessVariableListPresenter variableListPresenter;

    @Inject
    private ProcessDocumentListPresenter documentListPresenter;

    @Inject
    private RuntimeLogPresenter runtimeLogPresenter;

    @Inject
    private UserTaskListHistoryPresenter userTaskListHistoryPresenter;
    
    @Override
    public void init( final ProcessInstanceDetailsMultiPresenter presenter ) {
        super.init( presenter );
        uiBinder.createAndBindUi( this );
    }
    
    @Override
    public void onResize() {
        super.onResize();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                int height = ProcessInstanceDetailsMultiViewImpl.this.getOffsetHeight();
                if(height == 0){
                    height = 700;
                }
                GWT.log("ProcessInstanceDetailsMultiViewImpl height"+height);
                tabPanel.setHeight(height+"px");
            }
        });
    }

    @Override
    public void initTabs() {

        tabPanel.addTab( "Instance Details", Constants.INSTANCE.Process_Instance_Details() );

        tabPanel.addTab( "Process Variables", Constants.INSTANCE.Process_Variables() );

        tabPanel.addTab( "Documents", "Documents" );

        tabPanel.addTab( "Process Logs", Constants.INSTANCE.Logs() );
        
        tabPanel.addTab( "User Task History", Constants.INSTANCE.User_Task_Log() );
        
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                int height = ProcessInstanceDetailsMultiViewImpl.this.getOffsetHeight();
                if(height == 0){
                    height = 700;
                }
                GWT.log("initTabs ProcessInstanceDetailsMultiViewImpl height"+height);
                tabPanel.setHeight(height+"px");
            }
        });

        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection( SelectionEvent<Integer> event ) {
                if ( event.getSelectedItem() == 1 ) {
                    variableListPresenter.refreshGrid();
                } else if ( event.getSelectedItem() == 2 ) {
                    documentListPresenter.refreshGrid();
                }else if ( event.getSelectedItem() == 4 ) {
                	userTaskListHistoryPresenter.refreshGrid();
                }
            }
        } );
        int height = ProcessInstanceDetailsMultiViewImpl.this.getOffsetHeight();
                if(height == 0){
                    height = 700;
                }
        ScrollPanel detailsScrollPanel = new ScrollPanel(detailsPresenter.getWidget().asWidget());
        detailsScrollPanel.setHeight(height+"px");
        ScrollPanel variablesScrollPanel = new ScrollPanel(variableListPresenter.getWidget().asWidget());
        variablesScrollPanel.setHeight(height+"px");
        ScrollPanel documentScrollPanel = new ScrollPanel(documentListPresenter.getWidget().asWidget());
        documentScrollPanel.setHeight(height+"px");
        ScrollPanel runtimeScrollPanel = new ScrollPanel(runtimeLogPresenter.getWidget().asWidget());
        runtimeScrollPanel.setHeight(height+"px");
        ScrollPanel userTaskHistoryScrollPanel = new ScrollPanel(userTaskListHistoryPresenter.getWidget().asWidget());
        userTaskHistoryScrollPanel.setHeight(height+"px");
        
        ( (HTMLPanel) tabPanel.getWidget( 0 ) ).add( detailsScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 1 ) ).add( variablesScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 2 ) ).add( documentScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 3 ) ).add( runtimeScrollPanel );
        ( (HTMLPanel) tabPanel.getWidget( 4 ) ).add( userTaskHistoryScrollPanel );
    }

    @Override
    public IsWidget getOptionsButton() {
        return new DropdownButton( Constants.INSTANCE.Options() ) {{
            setSize( MINI );
            setRightDropdown( true );
            add( new NavLink( Constants.INSTANCE.Signal() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.signalProcessInstance();
                    }
                } );
            }} );

            add( new NavLink( Constants.INSTANCE.Abort() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.abortProcessInstance();
                    }
                } );
            }} );

            add( new NavLink( Constants.INSTANCE.View_Process_Model() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.goToProcessInstanceModelPopup();
                    }
                } );
            }} );
        }};
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

    @Override
    public IsWidget getCloseButton() {
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

}
