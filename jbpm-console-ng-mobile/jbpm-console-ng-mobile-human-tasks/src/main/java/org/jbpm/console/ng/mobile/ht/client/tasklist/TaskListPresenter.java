/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.mobile.ht.client.tasklist;

import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.base.HasRefresh;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowWidget;
import com.googlecode.mgwt.ui.client.widget.base.PullPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;
import org.jbpm.console.ng.mobile.ht.client.utils.TaskStatus;
import org.uberfire.security.Identity;


/**
 *
 * @author livthomas
 * @author salaboy
 */
@Dependent
public class TaskListPresenter {

    public interface TaskListView extends MGWTUberView<TaskListPresenter> {

        HasTapHandlers getNewTaskButton();

        HasRefresh getPullPanel();

        void setHeaderPullHandler(PullPanel.Pullhandler pullHandler);

        PullArrowWidget getPullHeader();

        void render(List<TaskSummary> tasks);

        HasCellSelectedHandler getTaskList();

        HasTapHandlers getBackButton();
    }

    @Inject
    private TaskListView view;
    
    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;
    
    @Inject
    private Identity identity;
    

    public TaskListPresenter() {
        
    }

    public TaskListView getView() {
        return view;
    }

    public void refresh() {
        List<String> statuses = new ArrayList<String>();
        for (TaskStatus status : TaskStatus.values()) {
            statuses.add(status.toString());
        }
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.render(tasks);
            }
        }).getTasksAssignedAsPotentialOwnerByExpirationDateOptional(identity.getName(), statuses, null, "en-UK");
    }

}
