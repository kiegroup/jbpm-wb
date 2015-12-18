/**
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.

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
package org.jbpm.dashboard.renderer.client.panel.widgets;

import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.displayer.client.AbstractDisplayerListener;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.uberfire.client.mvp.UberView;

public class DisplayerContainer implements IsWidget {

    public interface View extends UberView<DisplayerContainer> {
        void setHeaderVisible(boolean visible);
        void setHeaderText(String text);
        void setSelectorVisible(boolean visible);
        void setDisplayerList(Set<String> displayerNames);
        void setDisplayerHeight(int h);
        void showLoading(Displayer displayer);
        void showDisplayer(Displayer displayer);
        void showEmpty(Displayer displayer);
        void showError(String message, String cause);
        Style getHeaderStyle();
        Style getBodyStyle();
    }

    protected View view = new DisplayerContainerView();
    protected Map<String,Displayer> displayers;
    protected Displayer currentDisplayer;
    protected boolean error = true;

    protected Timer loadingTimer = new Timer() {
        public void run() {
            view.showLoading(currentDisplayer);
        }
    };

    DisplayerListener displayerListener = new AbstractDisplayerListener() {
        public void onDataLookup(Displayer displayer) {
            if (displayer == currentDisplayer) {
                // If the data lookup lasts more than one second then show the loading screen
                loadingTimer.schedule(1000);
            }
        }
        public void onDraw(Displayer displayer) {
            if (displayer == currentDisplayer) {
                loadingTimer.cancel();
                updateDisplayer();
            }
        }
        public void onRedraw(Displayer displayer) {
            if (displayer == currentDisplayer) {
                loadingTimer.cancel();
                updateDisplayer();
            }
        }
        public void onError(Displayer displayer, ClientRuntimeError e) {
            if (displayer == currentDisplayer) {
                loadingTimer.cancel();
                showError(e);
            }
        }
    };

    public DisplayerContainer(Map<String,Displayer> displayers, boolean showHeader) {
        view.init(this);
        view.setHeaderVisible(showHeader);
        showDisplayers(displayers);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void showDisplayers(Map<String,Displayer> displayers) {
        this.displayers = displayers;
        view.setDisplayerList(displayers.keySet());

        for (Displayer displayer : displayers.values()) {
            displayer.addListener(displayerListener);
        }

        if (displayers.size() < 2) {
            view.setSelectorVisible(false);
        }
        int height = 0;
        for (Map.Entry<String, Displayer> entry : displayers.entrySet()) {
            int chartHeight = entry.getValue().getDisplayerSettings().getChartHeight();
            if (chartHeight > height) height = chartHeight;
        }

        if (!displayers.isEmpty()) {
            view.setDisplayerHeight(height);
            Displayer first = displayers.values().iterator().next();
            showDisplayer(first);
        }
    }

    public void selectDisplayer(String name) {
        Displayer displayer = displayers.get(name);
        if (displayer != null) {
            showDisplayer(displayer);
        } else {
            view.showError("Displayer not found: " + name, null);
        }
    }

    public View getView() {
        return view;
    }

    protected void showDisplayer(Displayer displayer) {
        error = false;
        currentDisplayer = displayer;
        view.setHeaderText(displayer.getDisplayerSettings().getTitle());
        updateDisplayer();
    }

    protected void updateDisplayer() {
        DataSet ds = currentDisplayer.getDataSetHandler().getLastDataSet();
        if (ds != null && ds.getRowCount() == 0) {
            view.showEmpty(currentDisplayer);
        } else {
            view.showDisplayer(currentDisplayer);
        }
    }

    protected void showError(ClientRuntimeError e) {
        error = true;
        GWT.log(e.getMessage(), e.getThrowable());
        view.showError(e.getMessage(), e.getCause());
    }
}
