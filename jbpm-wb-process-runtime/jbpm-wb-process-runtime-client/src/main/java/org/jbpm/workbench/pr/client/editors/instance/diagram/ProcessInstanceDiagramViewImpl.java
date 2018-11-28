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
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.editors.diagram.ProcessDiagramWidgetViewImpl;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.uberfire.client.views.pfly.widgets.Select;

@Dependent
@Templated
public class ProcessInstanceDiagramViewImpl extends Composite implements ProcessInstanceDiagramView {

    @Inject
    @DataField("diagram")
    private ProcessDiagramWidgetViewImpl diagram;

    @Inject
    @DataField("node-details-panel")
    private HTMLDivElement nodeDetails;

    @Inject
    @DataField("available-nodes")
    private Select processNodes;

    @Inject
    @DataField("node-name")
    @Bound
    @SuppressWarnings("unused")
    private HTMLParagraphElement name;

    @Inject
    @DataField("node-type")
    @Bound
    @SuppressWarnings("unused")
    private HTMLParagraphElement type;

    @Inject
    @DataField("node-id")
    @Bound
    @SuppressWarnings("unused")
    private HTMLParagraphElement id;

    @Inject
    @DataField("trigger")
    @SuppressWarnings("unused")
    private HTMLButtonElement trigger;

    @Inject
    @AutoBound
    private DataBinder<ProcessNodeSummary> processNodeSummary;

    private ProcessInstanceDiagramPresenter presenter;

    @Override
    public void init(final ProcessInstanceDiagramPresenter presenter) {
        this.presenter = presenter;
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
        return processNodeSummary.getModel();
    }

    @Override
    public void setValue(final ProcessNodeSummary node) {
        processNodeSummary.setModel(node);
        if(node.getId() == null){
            nodeDetails.classList.add("hidden");
        } else {
            nodeDetails.classList.remove("hidden");
        }
    }

    @EventHandler("available-nodes")
    public void onProcessNodeChange(@ForEvent("change") Event e) {
        processNodes.toggle();
        presenter.onProcessNodeSelected(processNodes.getValue());
    }

    @EventHandler("trigger")
    public void onProcessNodeTrigger(@ForEvent("click") Event e) {
        presenter.onProcessNodeTrigger(processNodes.getValue());
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
