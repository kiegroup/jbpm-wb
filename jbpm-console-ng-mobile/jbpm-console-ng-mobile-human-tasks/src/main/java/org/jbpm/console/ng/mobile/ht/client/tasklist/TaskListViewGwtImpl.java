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


import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.base.HasRefresh;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowHeader;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowWidget;
import com.googlecode.mgwt.ui.client.widget.base.PullPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;
import org.jbpm.console.ng.mobile.ht.client.AbstractTaskView;


/**
 *
 * @author livthomas
 * @author salaboy
 */
@Dependent
public class TaskListViewGwtImpl extends AbstractTaskView implements TaskListPresenter.TaskListView {

    private final HeaderButton newTaskButton;

    private PullPanel pullPanel;
    private PullArrowHeader pullArrowHeader;
    
    private final CellList<TaskSummary> taskList;

    @Inject
    private MGWTPlaceManager placeManager;
   
    private TaskListPresenter presenter;

    public TaskListViewGwtImpl() {
        title.setHTML("Task List");

        newTaskButton = new HeaderButton();
        newTaskButton.setText("New task");
        headerPanel.setRightWidget(newTaskButton);

        pullPanel = new PullPanel();
        pullArrowHeader = new PullArrowHeader();
        pullPanel.setHeader(pullArrowHeader);
        layoutPanel.add(pullPanel);

        taskList = new CellList<TaskSummary>(new BasicCell<TaskSummary>() {
            @Override
            public String getDisplayString(TaskSummary model) {
                return model.getId() + " : " + model.getName();
            }
        });
        pullPanel.add(taskList);
    }
    
    @Override
    public void init(final TaskListPresenter presenter) {
        this.presenter = presenter;
        getNewTaskButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("New Task", Animation.SLIDE);
            }
        });

        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Home", Animation.SLIDE_REVERSE);
            }
        });
        
        getPullHeader().setHTML("pull down");

        PullArrowStandardHandler headerHandler = new PullArrowStandardHandler(getPullHeader(), getPullPanel());

        headerHandler.setErrorText("Error");
        headerHandler.setLoadingText("Loading");
        headerHandler.setNormalText("pull down");
        headerHandler.setPulledText("release to load");
        headerHandler.setPullActionHandler(new PullArrowStandardHandler.PullActionHandler() {
            @Override
            public void onPullAction(final AsyncCallback<Void> callback) {
                new Timer() {
                    @Override
                    public void run() {
                        presenter.refresh();
                    }
                }.schedule(1000);

            }
        });
        setHeaderPullHandler(headerHandler);

        getTaskList().addCellSelectedHandler(new CellSelectedHandler() {
            @Override
            public void onCellSelected(CellSelectedEvent event) {
                 //TODO ----> taskDetailsView.setTaskId(event.getIndex());
                placeManager.goTo("Task Details", Animation.SLIDE); 
            }
        });

        presenter.refresh();
    }

    @Override
    public HasTapHandlers getNewTaskButton() {
        return newTaskButton;
    }

    @Override
    public HasRefresh getPullPanel() {
        return pullPanel;
    }

    @Override
    public void setHeaderPullHandler(PullPanel.Pullhandler pullHandler) {
        pullPanel.setHeaderPullhandler(pullHandler);
    }

    @Override
    public PullArrowWidget getPullHeader() {
        return pullArrowHeader;
    }

    @Override
    public void render(List<TaskSummary> tasks) {
        taskList.render(tasks);
        pullPanel.refresh();
    }

    @Override
    public HasCellSelectedHandler getTaskList() {
        return taskList;
    }

    @Override
    public void refresh() {
        presenter.refresh();
    }

}
