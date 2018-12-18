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

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.TakesValue;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.DateTimeConverter;
import org.jbpm.workbench.common.client.util.DateTimeNAConverter;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.pr.client.util.SlaStatusConverter;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;

import static org.jboss.errai.common.client.dom.Window.getDocument;

@Dependent
@Templated(stylesheet = "ProcessInstanceDiagram.css")
public class NodeInstanceItemView implements TakesValue<NodeInstanceSummary>,
                                             IsElement {

    @Inject
    @DataField("list-item")
    private ListItem listItem;

    @Inject
    @DataField("actions-dropdown")
    private HTMLDivElement actionsDropdown;

    @Inject
    @Bound
    @DataField("label")
    @SuppressWarnings("unused")
    private HTMLAnchorElement label;

    @Inject
    @Bound
    @DataField("description")
    @SuppressWarnings("unused")
    private HTMLParagraphElement description;

    @Inject
    @DataField("actions-items")
    private UnorderedList actionsItems;

    @Inject
    @DataField("node-type")
    @Bound
    @SuppressWarnings("unused")
    private Span type;

    @Inject
    @DataField("details")
    private HTMLDivElement details;

    @Inject
    @DataField("node-id")
    @Bound
    @SuppressWarnings("unused")
    private Span id;

    @Inject
    @DataField("node-unique-id")
    @Bound
    @SuppressWarnings("unused")
    private Span nodeUniqueName;

    @Inject
    @DataField("node-time")
    @Bound(converter = DateTimeConverter.class)
    @SuppressWarnings("unused")
    private Span timestamp;

    @Inject
    @Bound(converter = DateTimeNAConverter.class)
    @DataField("node-sla-due-date")
    @SuppressWarnings("unused")
    private Span slaDueDate;

    @Inject
    @DataField("node-sla-compliance")
    @Bound(converter = SlaStatusConverter.class)
    @SuppressWarnings("unused")
    private Span slaCompliance;

    @Inject
    @AutoBound
    private DataBinder<NodeInstanceSummary> nodeInstance;

    @Override
    public HTMLElement getElement() {
        return listItem;
    }

    @Override
    public NodeInstanceSummary getValue() {
        return nodeInstance.getModel();
    }

    @Override
    public void setValue(final NodeInstanceSummary model) {
        this.nodeInstance.setModel(model);

        if(model.hasCallbacks()){
            for (GenericSummary.LabeledCommand callback : model.getCallbacks()) {
                addAction(e -> callback.getCommand().execute(),
                          callback.getLabel());
            }
        } else {
            actionsDropdown.classList.add("hidden");
        }

        String id = Document.get().createUniqueId();
        label.setAttribute("href",
                           "#node-instance-" + id);
        details.id = "node-instance-" + id;
    }

    public void addAction(final EventListener<MouseEvent> onclick,
                          final String label) {
        final HTMLElement a = getDocument().createElement("a");
        a.setTextContent(label);
        a.setOnclick(onclick);

        final HTMLElement li = getDocument().createElement("li");
        li.appendChild(a);
        actionsItems.appendChild(li);
    }
}
