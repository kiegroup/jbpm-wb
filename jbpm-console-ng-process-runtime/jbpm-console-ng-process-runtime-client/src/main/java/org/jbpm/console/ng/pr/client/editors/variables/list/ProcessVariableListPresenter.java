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
package org.jbpm.console.ng.pr.client.editors.variables.list;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.uberfire.backend.vfs.VFSService;
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
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Process Variables List")
public class ProcessVariableListPresenter {
    
    private Constants constants = GWT.create(Constants.class);
    
    private PlaceRequest place;
    
    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;
    
    public interface ProcessVariableListView extends UberView<ProcessVariableListPresenter> {
        
        void displayNotification(String text);
        
        HTML getProcessInstanceIdText();
        
        HTML getProcessDefinitionIdText();
        
        HTML getProcessNameText();
        
        void setProcessInstance(ProcessInstanceSummary pi);
        
    }
    
    private Menus menus;
    
    @Inject
    private PlaceManager placeManager;
    
    @Inject
    private ProcessVariableListView view;
    
    @Inject
    private Caller<DataServiceEntryPoint> dataServices;
    
    @Inject
    private Caller<VFSService> fileServices;
    
    private ListDataProvider<VariableSummary> dataProvider = new ListDataProvider<VariableSummary>();
    
    private String processInstanceId = "";
    
    private String processDefId = "";
    
    public ProcessVariableListPresenter() {
        makeMenuBar();
    }
    
    public static final ProvidesKey<VariableSummary> KEY_PROVIDER = new ProvidesKey<VariableSummary>() {
        @Override
        public Object getKey(VariableSummary item) {
            return item == null ? null : item.getVariableId();
        }
    };
    
    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Variables();
    }
    
    @WorkbenchPartView
    public UberView<ProcessVariableListPresenter> getView() {
        return view;
    }
    
    public void refreshVariableListData(final String processId,
            final String processDefId) {
        
        dataServices.call(new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback(ProcessInstanceSummary process) {
                
                view.setProcessInstance(process);
                
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getProcessInstanceById(Long.parseLong(processId));
        
        dataServices.call(new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback(ProcessSummary process) {
                
                view.getProcessDefinitionIdText().setText(process.getProcessDefId());
                view.getProcessNameText().setText(process.getName());
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getProcessDesc(processDefId);
        loadVariables(processId, processDefId);
        
    }
    
    public void addDataDisplay(HasData<VariableSummary> display) {
        dataProvider.addDataDisplay(display);
    }
    
    public ListDataProvider<VariableSummary> getDataProvider() {
        return dataProvider;
    }
    
    public void refreshData() {
        dataProvider.refresh();
    }
    
    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }
    
    @OnOpen
    public void onOpen() {
        this.processInstanceId = place.getParameter("processInstanceId", "");
        this.processDefId = place.getParameter("processDefId", "");
        view.getProcessInstanceIdText().setText(processInstanceId);
        view.getProcessNameText().setText(processDefId);
        refreshVariableListData(processInstanceId, processDefId);
    }
    
    public void loadVariables(final String processId,
            final String processDefId) {
        dataServices.call(new RemoteCallback<List<VariableSummary>>() {
            @Override
            public void callback(List<VariableSummary> variables) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll(variables);
                dataProvider.refresh();
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getVariablesCurrentState(Long.parseLong(processId), processDefId);
    }
    
    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }
    
    private void makeMenuBar() {
        menus = MenuFactory
                .newSimpleItem(constants.Refresh()).respondsWith(new Command() {
                    
                    @Override
                    public void execute() {
                        loadVariables( view.getProcessInstanceIdText().getText(), 
                                view.getProcessDefinitionIdText().getText() );
                        view.displayNotification( constants.Process_Variables_Refreshed() );
                    }
                })
                .endMenu().build();
        
    }
    
}
