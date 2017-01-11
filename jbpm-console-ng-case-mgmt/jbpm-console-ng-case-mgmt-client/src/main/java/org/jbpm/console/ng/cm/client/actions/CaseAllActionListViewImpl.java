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

import java.util.List;
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
import org.jbpm.console.ng.cm.model.CaseActionSummary;
import org.jbpm.console.ng.cm.util.CaseActionsFilterBy;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class CaseAllActionListViewImpl extends AbstractView<CaseAllActionListPresenter> implements CaseAllActionListPresenter.CaseAllActionListView {

    @Inject
    @DataField("actions")
    private Div actionsContainer;

    @Inject
    @DataField("available-actions")
    CaseSimpleActionListViewImpl availableActions;

    @Inject
    @DataField("inprogress-actions")
    CaseSimpleActionListViewImpl inprogressActions;

    @Inject
    @DataField("completed-actions")
    CaseSimpleActionListViewImpl completedActions;

    private Command taskAddCommand;

    @Override
    public void init(final CaseAllActionListPresenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void setActionAddCommand(Command taskAddCommand) {
        this.taskAddCommand = taskAddCommand;
    }


    @Override
    public HTMLElement getElement() {
        return actionsContainer;
    }

    @EventHandler("AddTask")
    public void onUserAddClick(@ForEvent("click") Event e) {
        if (taskAddCommand != null) {
            taskAddCommand.execute();
        }
    }

    @Override
    public void removeAllTasks() {
        availableActions.removeAllTasks();
        inprogressActions.removeAllTasks();
        completedActions.removeAllTasks();
    }

    @Override
    public void setCaseActionList(CaseActionsFilterBy filterBy, List<CaseActionSummary> caseActionList) {
        switch (filterBy) {
            case AVAILABLE: {
                availableActions.setCaseActionList(caseActionList);
                break;
            }
            case IN_PROGRESS: {
                inprogressActions.setCaseActionList(caseActionList);
                break;
            }
            case COMPLETED: {
                completedActions.setCaseActionList(caseActionList);
                break;
            }
        }

    }

    @Override
    public void updateListHeaders() {
        availableActions.updateListHeader(null, CaseActionsFilterBy.AVAILABLE);
        inprogressActions.updateListHeader(null, CaseActionsFilterBy.IN_PROGRESS);
        completedActions.updateListHeader(null, CaseActionsFilterBy.COMPLETED);
    }

    public void onCaseStartAdhocFragmentEvent(@Observes CaseStartAdhocFragmentEvent event) {
        presenter.triggerAdhocFragment(event.getAdhocFragmentName());
    }
}