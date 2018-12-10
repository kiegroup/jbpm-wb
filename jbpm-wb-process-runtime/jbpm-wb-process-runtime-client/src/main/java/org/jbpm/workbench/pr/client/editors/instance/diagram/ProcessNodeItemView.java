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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;

@Dependent
@Templated(stylesheet = "ProcessInstanceDiagram.css")
public class ProcessNodeItemView implements TakesValue<ProcessNodeSummary>,
                                            IsElement {

    @Inject
    @DataField("container")
    private HTMLDivElement container;

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
    private HTMLButtonElement trigger;

    @Inject
    @AutoBound
    private DataBinder<ProcessNodeSummary> processNodeSummary;

    @Override
    public ProcessNodeSummary getValue() {
        return processNodeSummary.getModel();
    }

    @Override
    public void setValue(final ProcessNodeSummary node) {
        processNodeSummary.setModel(node);
        if(node.hasCallbacks() && node.getCallbacks().size() == 1){
            trigger.textContent = node.getCallbacks().get(0).getLabel();
            trigger.classList.remove("hidden");
        } else {
            trigger.classList.add("hidden");
        }
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }

    @EventHandler("trigger")
    public void onProcessNodeTrigger(@ForEvent("click") Event e) {
        processNodeSummary.getModel().getCallbacks().get(0).getCommand().execute();
    }
}
