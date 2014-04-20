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

import com.google.gwt.user.client.ui.ScrollPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;
import org.jbpm.console.ng.mobile.core.client.AbstractView;

/**
 *
 * @author livthomas
 */
@Dependent
public class HomeViewImpl extends AbstractView implements HomePresenter.HomeView {

    private final Button processDefinitionsButton;

    private final Button processInstancesButton;

    private final Button tasksListButton;

    private HomePresenter presenter;

    @Inject
    private MGWTPlaceManager placeManager;

    public HomeViewImpl() {
        title.setHTML("jBPM Mobile");
        headerBackButton.setVisible(false);

        ScrollPanel scrollPanel = new ScrollPanel();
        layoutPanel.add(scrollPanel);

        RoundPanel homePanel = new RoundPanel();

        processDefinitionsButton = new Button("Process Definitions");
        homePanel.add(processDefinitionsButton);

        processInstancesButton = new Button("Process Instances");
        homePanel.add(processInstancesButton);

        tasksListButton = new Button("Tasks List");
        homePanel.add(tasksListButton);

        scrollPanel.add(homePanel);
    }

    @Override
    public void init(HomePresenter presenter) {
        this.presenter = presenter;

        processDefinitionsButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Process Definitions List", Animation.SLIDE);
            }
        });

        tasksListButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Tasks List", Animation.SLIDE);
            }
        });
    }

    @Override
    public void refresh() {

    }

    @Override
    public void setParameters(Map<String, Object> params) {

    }

}
