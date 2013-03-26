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
package org.jbpm.console.ng.bd.client.editors.session.list;



import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jbpm.console.ng.bd.model.KieSessionSummary;

import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Kie Sessions List")
public class KieSessionsListPresenter {

  

    public interface KieSessionsListView
            extends
            UberView<KieSessionsListPresenter> {

        void displayNotification(String text);

       

       
    }
    @Inject
    private KieSessionsListView view;
//    @Inject
//    private Caller<KnowledgeDomainServiceEntryPoint> knowledgeServices;
    
    
    private ListDataProvider<KieSessionSummary> dataProvider = new ListDataProvider<KieSessionSummary>();

    @WorkbenchPartTitle
    public String getTitle() {
        return "Process Definition List";
    }

    @WorkbenchPartView
    public UberView<KieSessionsListPresenter> getView() {
        return view;
    }

    public KieSessionsListPresenter() {
    }

    @PostConstruct
    public void init() {
    }
    
//    void newKieSessionButton(String group, String artifact, String version, String kbaseName, final String kieSessionName) {
//      knowledgeServices.call(new RemoteCallback<Integer>() {
//                      @Override
//                      public void callback(Integer sessionId) {
//                          view.displayNotification(" KSession "+kieSessionName+" Created! with id = "+sessionId);
//
//                      }
//                  }).newKieSession(group, artifact, version, kbaseName, kieSessionName);
//    }
    
   

    public void addDataDisplay(HasData<KieSessionSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<KieSessionSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnReveal
    public void onReveal() {
       
    }
}
