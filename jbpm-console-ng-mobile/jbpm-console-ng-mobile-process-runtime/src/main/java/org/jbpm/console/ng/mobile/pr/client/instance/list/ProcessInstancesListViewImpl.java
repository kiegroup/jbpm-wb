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
package org.jbpm.console.ng.mobile.pr.client.instance.list;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowHeader;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;

/**
 *
 * @author livthomas
 */
public class ProcessInstancesListViewImpl extends AbstractView implements 
        ProcessInstancesListPresenter.ProcessInstancesListView {

    @Inject
    private MGWTPlaceManager placeManager;
    
    private ProcessInstancesListPresenter presenter;
    
    private String definitionId;

    private PullPanel pullPanel;

    private PullArrowHeader pullArrowHeader;

    private final CellList<ProcessInstanceSummary> cellList;

    private List<ProcessInstanceSummary> instancesList;

    public ProcessInstancesListViewImpl() {
        title.setHTML("Process Instances");

        pullPanel = new PullPanel();
        pullArrowHeader = new PullArrowHeader();
        pullPanel.setHeader(pullArrowHeader);
        layoutPanel.add(pullPanel);

        cellList = new CellList<ProcessInstanceSummary>(new BasicCell<ProcessInstanceSummary>() {
            @Override
            public String getDisplayString(ProcessInstanceSummary model) {
                return model.getId() + " : " + model.getProcessName();
            }
        });
        pullPanel.add(cellList);

        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Home", Animation.SLIDE_REVERSE);
            }
        });
    }

    @Override
    public void init(final ProcessInstancesListPresenter presenter) {
        this.presenter = presenter;

        pullArrowHeader.setHTML("pull down");

        PullArrowStandardHandler headerHandler = new PullArrowStandardHandler(pullArrowHeader, pullPanel);

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
        pullPanel.setHeaderPullhandler(headerHandler);

        cellList.addCellSelectedHandler(new CellSelectedHandler() {
            @Override
            public void onCellSelected(CellSelectedEvent event) {
                Map<String, Object> params = new HashMap<String, Object>();
                ProcessInstanceSummary instance = instancesList.get(event.getIndex());
                params.put("instanceId", instance.getId());
                params.put("definitionId", instance.getProcessId());
                placeManager.goTo("Process Instance Details", Animation.SLIDE, params);
            }
        });

        presenter.refresh();
    }

    @Override
    public void render(List<ProcessInstanceSummary> instances) {
        instancesList = instances;
        cellList.render(instances);
        pullPanel.refresh();
    }

    @Override
    public void refresh() {
        if (definitionId == null) {
            presenter.refresh();
        } else {
            presenter.refresh(definitionId);
        }
    }

    @Override
    public void setParameters(Map<String, Object> params) {
        if (params == null || !params.containsKey("definitionId")) {
            definitionId = null;
        } else {
            definitionId = (String) params.get("definitionId");
        }
    }

}
