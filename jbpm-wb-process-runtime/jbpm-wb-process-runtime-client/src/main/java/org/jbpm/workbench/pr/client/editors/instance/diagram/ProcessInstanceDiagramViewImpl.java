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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
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
import org.uberfire.client.views.pfly.widgets.D3;
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
    private NodeCounterView nodeCounterView;

    private Callback<String> onProcessNodeSelectedCallback;

    private boolean renderBadges = true;

    private Map<String, Long> badges = new HashMap<>();

    @PostConstruct
    public void init(){
        nodeCounterView.setCallback(() -> showHideBadges());
    }

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
        diagram.expandDiagramContanier();
    }

    @Override
    public void displayImage(final String svgContent) {
        diagram.displayImage(svgContent);
        diagram.getElement().appendChild(nodeCounterView.getElement());
    }

    @Override
    public void setNodeBadges(final Map<String, Long> badges) {
        this.badges = badges;
        renderBadges = true;
    }

    @Override
    public void onShow() {
        renderBadges();
    }

    protected void showHideBadges(){
        final D3 d3 = D3.Builder.get();
        final D3 nodes = d3.selectAll("#processDiagramDiv svg [jbpm-node-badge]");
        nodes.attr("visibility", nodeCounterView.showBadges() ? "visible" : "hidden");
    }

    protected void renderBadges() {
        if(renderBadges == false){
            return;
        }

        final D3 d3 = D3.Builder.get();

        final D3 svg = d3.select("#processDiagramDiv svg");
        final D3.DOMRect svgRect = svg.node().getBoundingClientRect();
        if(svgRect.getWidth() == 0 && svgRect.getHeight() == 0){
            //SVG not visible
            return;
        }

        final boolean isOryx = svg.attr(":xmlns:oryx") != null;
        badges.forEach((nodeId, count) -> {
            final String path = "#processDiagramDiv svg [bpmn2nodeid=" + nodeId + "] " + (isOryx ? ".stencils" : "");
            final D3 node = d3.select(path);
            D3.DOMRect bb = node.node().getBoundingClientRect();
            final D3 group = node.append("g")
                    .attr("transform", "translate( " + (bb.getWidth() / 2 - 12.5) + ", " + (bb.getHeight() + 2) + ")")
                    .attr("jbpm-node-badge", nodeId);

            group.append("rect")
                    .attr("x", "0")
                    .attr("y", "0")
                    .attr("width", "25")
                    .attr("height", "20")
                    .attr("rx", "5")
                    .attr("ry", "5")
                    .attr("fill", "grey")
                    .attr("opacity", "0.5");
            group.append("text")
                    .attr("font-size", "10pt")
                    .attr("font-weight", "normal")
                    .attr("font-family", "Open Sans")
                    .attr("font-style", "normal")
                    .attr("text-anchor", "middle")
                    .attr("fill", "white")
                    .attr("x", "12")
                    .attr("y", "15")
                    .text(String.valueOf(count));
        });

        showHideBadges();

        renderBadges = false;
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
