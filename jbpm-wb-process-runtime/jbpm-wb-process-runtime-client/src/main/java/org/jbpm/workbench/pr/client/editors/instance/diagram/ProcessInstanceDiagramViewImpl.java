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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.editors.diagram.ProcessDiagramWidgetViewImpl;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.views.pfly.widgets.Select;

@Dependent
@Templated(stylesheet = "ProcessInstanceDiagramViewImpl.css")
public class ProcessInstanceDiagramViewImpl extends Composite implements ProcessInstanceDiagramView {

    @Inject
    @DataField("diagram")
    private ProcessDiagramWidgetViewImpl diagram;

    @Inject
    @DataField("available-nodes")
    private Select processNodes;

    @Inject
    @DataField("node-instances-counter")
    private Span nodeInstancesCounter;

    @Inject
    @DataField("node-details-panel")
    private ProcessNodeItemView processNodeSummaryView;

    @Inject
    @Bound
    @DataField("node-instances")
    @ListContainer("ul")
    @SuppressWarnings("unused")
    private ListComponent<NodeInstanceSummary, NodeInstanceItemView> nodes;

    @Inject
    @AutoBound
    private DataBinder<List<NodeInstanceSummary>> nodeList;

    private Callback<Long> onProcessNodeSelectedCallback;

    private Callback<Long> onNodeInstanceCancelCallback;

    private Callback<Long> onNodeInstanceReTriggerCallback;

    @PostConstruct
    protected void init(){
        nodes.addComponentCreationHandler(view -> {
           view.setOnNodeInstanceCancelCallback(onNodeInstanceCancelCallback);
           view.setOnNodeInstanceReTriggerCallback(onNodeInstanceReTriggerCallback);
        });
    }

    @Override
    public void setOnNodeInstanceCancelCallback(Callback<Long> onNodeInstanceCancelCallback) {
        this.onNodeInstanceCancelCallback = onNodeInstanceCancelCallback;
    }

    @Override
    public void setOnNodeInstanceReTriggerCallback(Callback<Long> onNodeInstanceReTriggerCallback) {
        this.onNodeInstanceReTriggerCallback = onNodeInstanceReTriggerCallback;
    }

    @Override
    public void setOnProcessNodeSelectedCallback(Callback<Long> onProcessNodeSelectedCallback) {
        this.onProcessNodeSelectedCallback = onProcessNodeSelectedCallback;
    }

    @Override
    public void setOnProcessNodeTriggeredCallback(Callback<Long> onProcessNodeTriggeredCallback) {
        processNodeSummaryView.setOnProcessNodeTriggeredCallback(onProcessNodeTriggeredCallback);
    }

    @Override
    public void setProcessNodes(final List<ProcessNodeSummary> nodes) {
        processNodes.removeAllOptions();
        nodes.forEach(node -> processNodes.addOption(node.getLabel(),
                                                     String.valueOf(node.getId())));
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
        } else {
            processNodeSummaryView.getElement().classList.remove("hidden");
        }
    }

    @Override
    public void setNodeInstances(final List<NodeInstanceSummary> nodes) {
        nodeList.setModel(nodes);
        nodeInstancesCounter.setTextContent(String.valueOf(nodes.size()));
    }

    @EventHandler("available-nodes")
    public void onProcessNodeChange(@ForEvent("change") Event e) {
        processNodes.toggle();
        if (onProcessNodeSelectedCallback != null) {
            final String node = processNodes.getValue();
            onProcessNodeSelectedCallback.callback(node == null || node.trim().isEmpty() ? null : Long.valueOf(node));
        }
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
