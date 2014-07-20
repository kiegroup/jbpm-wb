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
package org.jbpm.console.ng.pr.client.editors.instance.log;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.ProvidesKey;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.model.RuntimeLogSummary;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.util.LogUtils.LogType;
import org.jbpm.console.ng.pr.client.util.LogUtils.LogOrder;
import org.jbpm.console.ng.pr.client.util.LogUtils;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Logs")
public class RuntimeLogPresenter {

    private Constants constants = GWT.create(Constants.class);

    private PlaceRequest place;


    public interface RuntimeLogView extends UberView<RuntimeLogPresenter> {

        void displayNotification(String text);

        HTML getLogTextArea();
        
        Label getProcessInstanceStatusText();
                
        Label getProcessInstanceNameText();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    private RuntimeLogView view;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    

    public RuntimeLogPresenter() {
       
    }

    public static final ProvidesKey<VariableSummary> KEY_PROVIDER = new ProvidesKey<VariableSummary>() {
        @Override
        public Object getKey(VariableSummary item) {
            return item == null ? null : item.getVariableId();
        }
    };

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Logs();
    }

    @WorkbenchPartView
    public UberView<RuntimeLogPresenter> getView() {
        return view;
    }

    public void refreshProcessInstanceData(final Long processInstanceId, final LogOrder logOrder, final LogType logType) {
        
        view.getProcessInstanceNameText().setText("");
        view.getProcessInstanceStatusText().setText("");
        view.getLogTextArea().setText("");
        
        dataServices.call(new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback(ProcessInstanceSummary instanceSummary) {                
                view.getProcessInstanceNameText().setText(instanceSummary.getProcessName());
                view.getProcessInstanceStatusText().setText(LogUtils.getInstanceStatus(instanceSummary.getState()));                
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).getProcessInstanceById(processInstanceId);
        
        if(LogType.TECHNICAL.equals(logType)){
            dataServices.call(new RemoteCallback<List<RuntimeLogSummary>>() {
                @Override
                public void callback(List<RuntimeLogSummary> logs) {                    
                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();

                    if(logOrder == LogOrder.DESC)
                        Collections.reverse(logs);

                    for (RuntimeLogSummary rls : logs) {           
                        safeHtmlBuilder.appendEscapedLines(rls.getTime() + ": " + rls.getLogLine() + " - " + rls.getType() + "\n");                            
                    }
                    view.getLogTextArea().setHTML(safeHtmlBuilder.toSafeHtml());
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message, Throwable throwable ) {                    
                    ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                    return true;
                }
            }).getAllRuntimeLogs(processInstanceId);
        }else{
            dataServices.call(new RemoteCallback<List<RuntimeLogSummary>>() {
                @Override
                public void callback(List<RuntimeLogSummary> logs) {
                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                    if(logOrder == LogOrder.DESC)
                        Collections.reverse(logs);

                    for (RuntimeLogSummary rls : logs) {                       
                        safeHtmlBuilder.appendEscapedLines(rls.getTime() + ": " + rls.getLogLine() + "\n");                            
                    }
                    view.getLogTextArea().setHTML(safeHtmlBuilder.toSafeHtml());
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message, Throwable throwable ) {                    
                    ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                    return true;
                }
            }).getBusinessLogs(processInstanceId);
        }        
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
       
    }

    
    
}
