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
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.SlaStatusConverter;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.uberfire.client.callbacks.Callback;

import static org.jboss.errai.common.client.dom.Window.getDocument;

@Dependent
@Templated
public class NodeInstanceItemView implements TakesValue<NodeInstanceSummary>,
                                             IsElement {

    private Constants constants = Constants.INSTANCE;

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
    @DataField("time")
    private HTMLParagraphElement time;

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
    @DataField("node-sla-due-date")
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

    private Callback<Long> onNodeInstanceCancelCallback;

    private Callback<Long> onNodeInstanceReTriggerCallback;

    public void setOnNodeInstanceCancelCallback(Callback<Long> onNodeInstanceCancelCallback) {
        this.onNodeInstanceCancelCallback = onNodeInstanceCancelCallback;
    }

    public void setOnNodeInstanceReTriggerCallback(Callback<Long> onNodeInstanceReTriggerCallback) {
        this.onNodeInstanceReTriggerCallback = onNodeInstanceReTriggerCallback;
    }

    @Override
    public void setValue(final NodeInstanceSummary model) {
        this.nodeInstance.setModel(model);

        this.time.textContent = (model.isCompleted() ? constants.Completed() : constants.Started()) + " " + DateUtils.getPrettyTime(model.getTimestamp());

        if(model.isCompleted()){
            actionsDropdown.classList.add("hidden");
        } else {
            addAction(e-> onNodeInstanceCancelCallback.callback(nodeInstance.getModel().getId()), constants.Cancel());
            addAction(e -> onNodeInstanceReTriggerCallback.callback(nodeInstance.getModel().getId()), constants.ReTrigger());
        }

        label.setAttribute("href", "#" + String.valueOf(model.getId()));
        details.id = String.valueOf(model.getId());

        slaDueDate.setTextContent(model.getSlaDueDate() == null ? constants.SlaNA() : DateUtils.getDateTimeStr(model.getSlaDueDate()));
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
