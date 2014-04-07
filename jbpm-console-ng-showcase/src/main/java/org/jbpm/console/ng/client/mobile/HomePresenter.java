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
package org.jbpm.console.ng.client.mobile;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import javax.inject.Inject;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jbpm.console.ng.mobile.ht.client.TaskClientFactory;
import org.jbpm.console.ng.mobile.pr.client.definition.ProcessClientFactory;


/**
 *
 * @author livthomas
 */
public class HomePresenter {

    public interface HomeView extends IsWidget {

        HasTapHandlers getProcessDefinitionsButton();

        HasTapHandlers getProcessInstancesButton();

        HasTapHandlers getTasksListButton();

    }

    @Inject
    private TaskClientFactory taskClientFactory;

    @Inject
    private ProcessClientFactory processClientFactory;

    @Inject
    private HomeView view;

    public HomeView getView() {
        return view;
    }

    @AfterInitialization
    public void init() {
        view.getTasksListButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                AnimationHelper animationHelper = new AnimationHelper();
                RootPanel.get().clear();
                RootPanel.get().add(animationHelper);
                animationHelper.goTo(taskClientFactory.getTaskListPresenter().getView(), Animation.SLIDE);
            }
        });

        taskClientFactory.getTaskListPresenter().getView().getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                AnimationHelper animationHelper = new AnimationHelper();
                RootPanel.get().clear();
                RootPanel.get().add(animationHelper);
                animationHelper.goTo(getView(), Animation.SLIDE_REVERSE);
            }
        });
        
        processClientFactory.getProcessDefinitionListPresenter().getView().getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                AnimationHelper animationHelper = new AnimationHelper();
                RootPanel.get().clear();
                RootPanel.get().add(animationHelper);
                animationHelper.goTo(getView(), Animation.SLIDE_REVERSE);
            }
        });
    }

}
