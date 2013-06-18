/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.console.ng.ht.client.history;

import java.util.LinkedList;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.ht.client.i8n.Constants;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;

import com.google.gwt.core.client.GWT;

@Dependent
@WorkbenchScreen(identifier = "Actions Histories")
public class ActionHistoryPresenter {
    
    @Inject
    private ActionHistory actionHistory;
    
    @Inject
    private ActionHistoryView view;
    
    @Inject
    private Identity identity;
    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;
    
    @WorkbenchPartView
    public UberView<ActionHistoryPresenter> getView() {
        return view;
    }
    
    
    private Constants constants = GWT.create( Constants.class );
    
    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List();
    }
    
    

    public void saveHistory(@Observes @History PointHistory pointHistory) {
        updateHistory(pointHistory);
    }
    
    private void updateHistory(PointHistory pointHistory){
        if(actionHistory.getPoints()==null){
            actionHistory.setPoints(new LinkedList<PointHistory>());
        }
        actionHistory.getPoints().add(pointHistory);
    }

    public interface ActionHistoryView extends UberView<ActionHistoryPresenter> {
    
     void displayNotification( String text );
    
     //TaskListMultiDayBox getTaskListMultiDayBox();
    
     //MultiSelectionModel<TaskSummary> getSelectionModel();
    
     //TextBox getSearchBox();
    
     //void refreshTasks();
    }

}
