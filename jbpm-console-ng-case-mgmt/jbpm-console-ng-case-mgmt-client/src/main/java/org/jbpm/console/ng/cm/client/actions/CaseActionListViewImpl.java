/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.cm.client.actions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.cm.client.events.CaseStartAdhocFragmentEvent;
import org.jbpm.console.ng.cm.client.util.AbstractView;

import org.jbpm.console.ng.cm.util.CaseActionSearchRequest;
import org.uberfire.mvp.Command;

@Dependent
@Templated(stylesheet = "CaseActionListViewImpl.css")
public class CaseActionListViewImpl extends AbstractView<CaseActionsPresenter> implements CaseActionsPresenter.CaseActionListView {


    @Inject
    @DataField("search-actions")
    private CaseActionListSearchViewImpl actions;

    @Inject
    @DataField("actions")
    private Div actionsContainer;

    @Inject
    @DataField("actions-list")
    private CaseSimpleActionListViewImpl actionsList;


    private Command taskAddCommand;

    private Command processAddCommand;


    @Override
    public void init(final CaseActionsPresenter presenter) {
        super.init(presenter);
        actions.init(presenter);
        actionsList.init(presenter);
        presenter.setSimpleActionListView(actionsList);
    }


    @Override
    public void setTaskAddCommand(Command taskAddCommand) {
        this.taskAddCommand = taskAddCommand;
    }

    @Override
    public void setProcessAddCommand(Command processAddCommand) {
        this.processAddCommand = processAddCommand;
    }


    @Override
    public HTMLElement getElement() {
        return actionsContainer;
    }

    public CaseActionSearchRequest getCaseActionSearchRequest() {
        return actions.getCaseActionSearchRequest();
    }


    @EventHandler("task-add")
    public void onUserAddClick(@ForEvent("click") Event e) {
        if (taskAddCommand != null) {
            taskAddCommand.execute();
        }
    }

    @EventHandler("process-add")
    public void onGroupAddClick(@ForEvent("click") Event e) {
        if (processAddCommand != null) {
            processAddCommand.execute();
        }
    }

    public void onCaseStartAdhocFragmentEvent(@Observes CaseStartAdhocFragmentEvent event) {
        presenter.triggerAdhocFragment(event.getAdhocFragmentName());
    }

}