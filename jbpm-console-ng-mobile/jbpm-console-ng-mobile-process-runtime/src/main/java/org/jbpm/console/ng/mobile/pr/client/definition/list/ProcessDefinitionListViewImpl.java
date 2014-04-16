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
package org.jbpm.console.ng.mobile.pr.client.definition.list;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.base.HasRefresh;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowHeader;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowWidget;
import com.googlecode.mgwt.ui.client.widget.base.PullPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;
import java.util.List;
import java.util.Map;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;
import org.jbpm.console.ng.mobile.generic.client.AbstractView;
import org.jbpm.console.ng.pr.model.ProcessSummary;

/**
 *
 * @author livthomas
 */
public class ProcessDefinitionListViewImpl extends AbstractView implements ProcessDefinitionListPresenter.ProcessDefinitionListView {

    private PullPanel pullPanel;

    private PullArrowHeader pullArrowHeader;

    private CellList<ProcessSummary> definitionsList;

    private ProcessDefinitionListPresenter presenter;
    @Inject
    private MGWTPlaceManager placeManager;

    public ProcessDefinitionListViewImpl() {
        title.setHTML("Process Definitions");

        pullPanel = new PullPanel();
        pullArrowHeader = new PullArrowHeader();
        pullPanel.setHeader(pullArrowHeader);
        layoutPanel.add(pullPanel);

        definitionsList = new CellList<ProcessSummary>(new BasicCell<ProcessSummary>() {
            @Override
            public String getDisplayString(ProcessSummary model) {
                return model.getName() + " : " + model.getVersion();
            }
        });
        pullPanel.add(definitionsList);

        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Home", Animation.SLIDE_REVERSE);
            }
        });
    }

    @Override
    public void render(List<ProcessSummary> definitions) {
        definitionsList.render(definitions);
        pullPanel.refresh();
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
    public void init(final ProcessDefinitionListPresenter presenter) {
        this.presenter = presenter;
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

        presenter.refresh();

    }

    @Override
    public void refresh() {
        presenter.refresh();
    }

    @Override
    public void setParameters(Map<String, Object> params) {

    }

}
