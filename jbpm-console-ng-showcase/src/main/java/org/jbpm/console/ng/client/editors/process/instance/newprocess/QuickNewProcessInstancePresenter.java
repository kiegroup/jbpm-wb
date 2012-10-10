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
package org.jbpm.console.ng.client.editors.process.instance.newprocess;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import javax.annotation.PostConstruct;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.client.model.ProcessSummary;
import org.jbpm.console.ng.shared.KnowledgeDomainServiceEntryPoint;
import org.jbpm.console.ng.shared.StatefulKnowledgeSessionEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Quick New Process Instance")
public class QuickNewProcessInstancePresenter {

    public interface InboxView
            extends
            UberView<QuickNewProcessInstancePresenter> {

        void displayNotification(String text);
    }
    @Inject
    InboxView view;
    
    @Inject
    Caller<StatefulKnowledgeSessionEntryPoint> ksessionServices;

    @Inject
    Caller<KnowledgeDomainServiceEntryPoint> domainServices;
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Quick New Process Instance";
    }

    @WorkbenchPartView
    public UberView<QuickNewProcessInstancePresenter> getView() {
        return view;
    }

    public QuickNewProcessInstancePresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void startProcessInstance(final String processId) {
        
        ksessionServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long processId) {
                view.displayNotification("Process Created (id = " + processId + ")");
                
            }
        }).startProcess(processId);

    }
    
     public void listProcesses() {
        
        domainServices.call(new RemoteCallback<List<ProcessSummary>>() {
            @Override
            public void callback(List<ProcessSummary> processes) {
                for(ProcessSummary ps : processes){
                    view.displayNotification("Process (id = " + ps.getId() + " - name = "+ps.getName()+")");
                }
                
            }
        }).getProcesses();

    }
}
