/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.ht.client.editors.taskslist;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.ht.client.editors.taskslist.TaskListPresenter;
import org.jbpm.workbench.ht.client.editors.taskslist.TaskListViewImpl;
import org.jbpm.workbench.ht.client.editors.taskslist.AbstractTaskListView.ConditionalActionHasCell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.jbpm.workbench.common.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskListViewImplTest extends AbstractTaskListViewTest {

    @InjectMocks
    private TaskListViewImpl view;

    @Mock
    private TaskListPresenter presenter;
    
    @Override
    public AbstractTaskListView getView(){
        return view;
    }
    
    @Override
    public AbstractTaskListPresenter getPresenter(){
        return presenter;
    }

    @Override
    public String getDataSetId(){
        return HUMAN_TASKS_WITH_USER_DATASET;
    }

    @Override
    public int getExpectedDefaultTabFilterCount(){
        return 4;
    }
    
    @Test
    @Override
    public void testResumeActionHasCell(){
        ConditionalActionHasCell resumeCell = new ConditionalActionHasCell("", cellDelegate, view.getResumeActionCondition());

        //Status test
        for(String taskStatus : TASK_STATUS_LIST){
            boolean shouldRender = (taskStatus == TASK_STATUS_SUSPENDED);
            
            //Actual owner
            runActionHasCellTest(taskStatus, TEST_USER_ID, null, resumeCell, shouldRender);
            runActionHasCellTest(taskStatus, "otheruser", null, resumeCell, false);
            runActionHasCellTest(taskStatus, null, null, resumeCell, false);
            
            //Potential owners
            runActionHasCellTest(taskStatus, null, POSITIVE_POTENTIAL_OWNERS, resumeCell, shouldRender);
            runActionHasCellTest(taskStatus, "otheruser", POSITIVE_POTENTIAL_OWNERS, resumeCell, shouldRender);
            runActionHasCellTest(taskStatus, null, NEGATIVE_POTENTIAL_OWNERS, resumeCell, false);
            runActionHasCellTest(taskStatus, "otheruser", NEGATIVE_POTENTIAL_OWNERS, resumeCell, false);
        }
    }
    
    @Test
    @Override
    public void testSuspendActionHasCell(){
        ConditionalActionHasCell suspendCell = new ConditionalActionHasCell("", cellDelegate, view.getSuspendActionCondition());

        //Actual owner vs status tests
        for(String taskStatus : TASK_STATUS_LIST){
            boolean shouldRender = (taskStatus == TASK_STATUS_RESERVED || taskStatus == TASK_STATUS_INPROGRESS);
            runActionHasCellTest(taskStatus, TEST_USER_ID, null, suspendCell, shouldRender);
            runActionHasCellTest(taskStatus, "otheruser", null, suspendCell, false);
            runActionHasCellTest(taskStatus, null, null, suspendCell, false);
        }

        //Potential owners vs status tests
        for(String taskStatus : TASK_STATUS_LIST){
            boolean shouldRender = (taskStatus == TASK_STATUS_READY);
            runActionHasCellTest(taskStatus, null, POSITIVE_POTENTIAL_OWNERS, suspendCell, shouldRender);
            runActionHasCellTest(taskStatus, "otheruser", POSITIVE_POTENTIAL_OWNERS, suspendCell, shouldRender);
            runActionHasCellTest(taskStatus, null, NEGATIVE_POTENTIAL_OWNERS, suspendCell, false);
            runActionHasCellTest(taskStatus, "otheruser", NEGATIVE_POTENTIAL_OWNERS, suspendCell, false);
        }
    }

}
