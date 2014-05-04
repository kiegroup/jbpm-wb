/*
 * Copyright 2014 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.mobile.core.client.home;

import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;

/**
 *
 * @author livthomas
 */
@Dependent
public class HomeViewImpl extends AbstractView implements HomePresenter.HomeView {

    private final CellList<String> cellList;

    private HomePresenter presenter;

    @Inject
    private MGWTPlaceManager placeManager;

    public HomeViewImpl() {
        title.setHTML("jBPM Mobile");
        headerBackButton.setVisible(false);

        cellList = new CellList<String>(new BasicCell<String>() {
            @Override
            public String getDisplayString(String model) {
                return model;
            }
        });
        cellList.setRound(true);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidget(cellList);
        scrollPanel.setScrollingEnabledX(false);
        layoutPanel.add(scrollPanel);
    }

    @Override
    public void init(HomePresenter presenter) {
        this.presenter = presenter;

        cellList.addCellSelectedHandler(new CellSelectedHandler() {
            @Override
            public void onCellSelected(CellSelectedEvent event) {
                String nextScreen = null;
                switch (event.getIndex()) {
                    case 0:
                        nextScreen = "Process Definitions List";
                        break;
                    case 1:
                        nextScreen = "Process Instances List";
                        break;
                    case 2:
                        nextScreen = "Tasks List";
                        break;
                    default:
                        return;
                }
                placeManager.goTo(nextScreen, Animation.SLIDE, null);
            }
        });

        List<String> sections = new ArrayList<String>();
        sections.add("Process Definitions");
        sections.add("Process Instances");
        sections.add("Tasks List");
        cellList.render(sections);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void setParameters(Map<String, Object> params) {

    }

}
