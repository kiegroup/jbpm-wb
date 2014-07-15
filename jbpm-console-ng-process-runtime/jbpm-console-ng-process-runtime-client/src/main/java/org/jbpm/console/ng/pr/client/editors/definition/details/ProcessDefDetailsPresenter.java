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

package org.jbpm.console.ng.pr.client.editors.definition.details;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;

import java.util.ArrayList;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.model.TaskDefSummary;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.DummyProcessPath;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.ProcessDefSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessDefStyleEvent;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.DefaultPosition;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Process Definition Details", preferredWidth = 500)
public class ProcessDefDetailsPresenter {

    private PlaceRequest place;

    public interface ProcessDefDetailsView extends UberView<ProcessDefDetailsPresenter> {

        void displayNotification( String text );

        HTML getNroOfHumanTasksText();

        HTML getProcessNameText();
        
        HTML getProcessIdText();

        HTML getHumanTasksListBox();

        HTML getUsersGroupsListBox();

        HTML getProcessDataListBox();
        
        HTML getProcessServicesListBox();

        HTML getSubprocessListBox();

        HTML getDeploymentIdText();

        void setProcessAssetPath( Path processAssetPath );

        void setEncodedProcessSource( String encodedProcessSource );
        
        Path getProcessAssetPath();
        
        String getEncodedProcessSource();
    }
    
    private Menus menus;
    
    @Inject
    private PlaceManager placeManager;

    @Inject
    private ProcessDefDetailsView view;
    
    @Inject
    private Event<ProcessDefStyleEvent> processDefStyleEvent;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;
    
    @Inject
    private Caller<ProcessDefinitionService> processDefService;

    @Inject
    private Caller<VFSService> fileServices;

    private Constants constants = GWT.create( Constants.class );

    public ProcessDefDetailsPresenter() {
        makeMenuBar();
    }
    
    @DefaultPosition
    public Position getPosition(){
        return Position.EAST;
    }
    

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Definition_Details();
    }

    @WorkbenchPartView
    public UberView<ProcessDefDetailsPresenter> getView() {
        return view;
    }
    
    private void changeStyleRow( String processDefName, String processDefVersion ){
        processDefStyleEvent.fire( new ProcessDefStyleEvent( processDefName, processDefVersion ) );
    }

    public void refreshProcessDef( final String deploymentId, final String processId ) {
        dataServices.call( new RemoteCallback<List<TaskDefSummary>>() {
            @Override
            public void callback( List<TaskDefSummary> tasks ) {
                view.getNroOfHumanTasksText().setText( String.valueOf( tasks.size() ) );
                view.getHumanTasksListBox().setText("");
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                if(tasks.isEmpty()){
                    safeHtmlBuilder.appendEscapedLines("No User Tasks defined in this process");
                    view.getHumanTasksListBox().setStyleName("muted");
                    view.getHumanTasksListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }else{
                    for ( TaskDefSummary t : tasks ) {
                       safeHtmlBuilder.appendEscapedLines(t.getName() +"\n" );
                    }
                    view.getHumanTasksListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }
                
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       } ).getAllTasksDef( processId );

        dataServices.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( Map<String, String> entities ) {
                view.getUsersGroupsListBox().setText("");
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                if(entities.keySet().isEmpty()){
                    safeHtmlBuilder.appendEscapedLines("No user or group used in this process");
                    view.getUsersGroupsListBox().setStyleName("muted");
                    view.getUsersGroupsListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }else{
                    for ( String key : entities.keySet() ) {
                        safeHtmlBuilder.appendEscapedLines(entities.get( key ) + " - " + key +"\n");
                    }
                    view.getUsersGroupsListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }
                
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       } ).getAssociatedEntities( processId );

        dataServices.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( Map<String, String> inputs ) {
                view.getProcessDataListBox().setText("");
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                if(inputs.keySet().isEmpty()){
                    safeHtmlBuilder.appendEscapedLines("No process variables defined for this process");
                    view.getProcessDataListBox().setStyleName("muted");
                    view.getProcessDataListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }else{
                    for ( String key : inputs.keySet() ) {
                        safeHtmlBuilder.appendEscapedLines(key + " - " + inputs.get( key ) +"\n");
                    }
                    view.getProcessDataListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }
                
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       } ).getRequiredInputData( processId );

        dataServices.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> subprocesses ) {
                view.getSubprocessListBox().setText("");
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                if(subprocesses.isEmpty()){
                    safeHtmlBuilder.appendEscapedLines("No subproceses required by this process");
                    view.getSubprocessListBox().setStyleName("muted");
                    view.getSubprocessListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }else{
                    for ( String key : subprocesses ) {
                        safeHtmlBuilder.appendEscapedLines(key + "\n");
                    }
                    view.getSubprocessListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }
                
            }
        }, new ErrorCallback<Message>() {
           @Override
           public boolean error( Message message, Throwable throwable ) {
               ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
               return true;
           }
       } ).getReusableSubProcesses( processId );

        processDefService.call( new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback( ProcessSummary process ) {
                if (process != null) {
                    view.setEncodedProcessSource( process.getEncodedProcessSource() );
                    view.getProcessNameText().setText(process.getName());
                    if ( process.getOriginalPath() != null ) {
                        fileServices.call( new RemoteCallback<Path>() {
                            @Override
                            public void callback( Path processPath ) {
                                view.setProcessAssetPath( processPath );
                            }
                        } ).get( process.getOriginalPath() );
                    } else {
                        view.setProcessAssetPath( new DummyProcessPath( process.getProcessDefId() ) );
                    }
                    changeStyleRow(process.getName(), process.getVersion());
                } else {
                    // set to null to ensure it's clear state
                    view.setEncodedProcessSource(null);
                    view.setProcessAssetPath(null);
                }
            }
        }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           } ).getItem( new ProcessDefinitionKey(deploymentId, processId) );
        
        dataServices.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( Map<String, String> services ) {
                view.getProcessServicesListBox().setText("");
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                if(services.keySet().isEmpty()){
                    safeHtmlBuilder.appendEscaped("No services required for this process");
                    view.getProcessServicesListBox().setStyleName("muted");
                    view.getProcessServicesListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }else{
                    for ( String key : services.keySet() ) {
                        safeHtmlBuilder.appendEscapedLines(key + " - " + services.get( key ) +"\n");
                    }
                    view.getProcessServicesListBox().setHTML(safeHtmlBuilder.toSafeHtml());
                }
                
            }
        }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           } ).getServiceTasks( processId );
        
        
    }

    @OnOpen
    public void onOpen() {
        
    }
    
    public void onProcessDefSelectionEvent(@Observes ProcessDefSelectionEvent event){
        
        view.getProcessIdText().setText( event.getProcessId() );
        
        view.getDeploymentIdText().setText( event.getDeploymentId() );

        refreshProcessDef( event.getDeploymentId(), event.getProcessId() );
    }
    
    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }
    
    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( constants.New_Instance()).respondsWith(new Command() {
                        @Override
                        public void execute() {
                            PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Form Display Popup" );
                            placeRequestImpl.addParameter( "processId", view.getProcessIdText().getText() );
                            placeRequestImpl.addParameter( "domainId", view.getDeploymentIdText().getText() );
                            placeRequestImpl.addParameter( "processName", view.getProcessNameText().getText() );
                            placeManager.goTo( placeRequestImpl );
                        }
                     }).endMenu()
                .newTopLevelMenu( constants.Options())
                .withItems(getOptions())
                .endMenu()
                .newTopLevelMenu( constants.Refresh() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        refreshProcessDef( view.getDeploymentIdText().getText(), view.getProcessNameText().getText() );
                        view.displayNotification( constants.Process_Definition_Details_Refreshed() );
                    }
                } )
                .endMenu().build();

    }
    private List<MenuItem> getOptions(){
        List<MenuItem> menuItems = new ArrayList<MenuItem>(2);
        
        menuItems.add( MenuFactory.newSimpleItem( constants.View_Process_Model()).respondsWith( new Command() {
            @Override
            public void execute() {   
                    PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Designer" );

                   // if ( view.getEncodedProcessSource() != null ) {
                        placeRequestImpl.addParameter( "readOnly", "true" );
                        //placeRequestImpl.addParameter( "encodedProcessSource", view.getEncodedProcessSource() );
                        placeRequestImpl.addParameter("processId", view.getProcessIdText().getText());
                        placeRequestImpl.addParameter("deploymentId", view.getDeploymentIdText().getText());
                        
                    //}
                    placeManager.goTo( view.getProcessAssetPath(), placeRequestImpl );
                }        
        } ).endMenu().build().getItems().get( 0 ) );
        
        menuItems.add( MenuFactory.newSimpleItem( constants.View_Process_Instances()).respondsWith( new Command() {
            @Override
            public void execute() {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Instances" );
                placeRequestImpl.addParameter( "processName", view.getProcessNameText().getText() );
                placeManager.goTo( placeRequestImpl );
            }
        } ).endMenu().build().getItems().get( 0 ) );
        
        
        return menuItems;
    
    }

}
