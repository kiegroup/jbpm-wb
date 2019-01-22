/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.instance.diagram;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.editors.diagram.ProcessDiagramWidgetViewImpl;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.views.pfly.widgets.Select;

@Dependent
@Templated(stylesheet = "ProcessInstanceDiagram.css")
public class ProcessInstanceDiagramViewImpl extends Composite implements ProcessInstanceDiagramView {

    @Inject
    @DataField("diagram")
    private ProcessDiagramWidgetViewImpl diagram;

    @Inject
    @DataField("available-nodes")
    private Select processNodes;

    @Inject
    @DataField("node-details-panel")
    private ProcessNodeItemView processNodeSummaryView;

    @Inject
    @DataField("node-instances")
    private NodeInstancesView nodeInstancesView;

    @Inject
    @DataField("timer-instances")
    private TimerInstancesView timerInstancesView;

    @Inject
    @DataField("node-actions-panel")
    private HTMLDivElement nodeActionsPanel;

    @Inject
    @DataField("process-diagram-panel")
    private HTMLDivElement processDiagramPanel;

    private Callback<String> onProcessNodeSelectedCallback;

    @Override
    public void setOnProcessNodeSelectedCallback(Callback<String> callback) {
        this.onProcessNodeSelectedCallback = callback;
    }

    @Override
    public void setOnDiagramNodeSelectionCallback(Callback<String> callback) {
        diagram.setOnDiagramNodeSelectionCallback(callback);
    }

    @Override
    public void setProcessNodes(final List<ProcessNodeSummary> nodes) {
        processNodes.removeAllOptions();
        nodes.forEach(node -> processNodes.addOption(node.getLabel(), node.getUniqueId()));
        processNodes.refresh();
    }

    @Override
    public ProcessNodeSummary getValue() {
        return processNodeSummaryView.getValue();
    }

    @Override
    public void setValue(final ProcessNodeSummary node) {
        processNodeSummaryView.setValue(node);
        if (node.getId() == null) {
            processNodeSummaryView.getElement().classList.add("hidden");
            processNodes.setValue("");
        } else {
            processNodeSummaryView.getElement().classList.remove("hidden");
            processNodes.setValue(node.getUniqueId());
        }
    }

    @Override
    public void setNodeInstances(final List<NodeInstanceSummary> nodes) {
        if(nodes.isEmpty()){
            nodeInstancesView.getElement().classList.add("hidden");
        } else {
            nodeInstancesView.getElement().classList.remove("hidden");
        }
        nodeInstancesView.setValue(nodes);
    }

    @Override
    public void setTimerInstances(final List<TimerInstanceSummary> timers) {
        if(timers.isEmpty()){
            timerInstancesView.getElement().classList.add("hidden");
        } else {
            timerInstancesView.getElement().classList.remove("hidden");
        }
        timerInstancesView.setValue(timers);
    }

    @EventHandler("available-nodes")
    public void onProcessNodeChange(@ForEvent("change") Event e) {
        processNodes.toggle();
        if (onProcessNodeSelectedCallback != null) {
            final String node = processNodes.getValue();
            onProcessNodeSelectedCallback.callback(node == null || node.trim().isEmpty() ? null : node);
        }
    }

    @Override
    public void hideNodeActions() {
        nodeActionsPanel.classList.remove("col-md-2");
        nodeActionsPanel.classList.add("hidden");
        processDiagramPanel.classList.remove("col-md-10");
        processDiagramPanel.classList.add("col-md-12");
    }

    @Override
    public void displayImage(final String svgContent) {
        diagram.displayImage(svgContent);
    }

    @Override
    public void displayMessage(final String message) {
        diagram.displayMessage(message);
    }

    @Override
    public void showBusyIndicator(final String message) {
        diagram.showBusyIndicator(message);
    }

    @Override
    public void hideBusyIndicator() {
        diagram.hideBusyIndicator();
    }
}
