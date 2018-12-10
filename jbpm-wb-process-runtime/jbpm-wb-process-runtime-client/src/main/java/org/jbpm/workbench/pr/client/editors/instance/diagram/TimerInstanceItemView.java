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
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;

import static org.jboss.errai.common.client.dom.Window.getDocument;

@Dependent
@Templated(stylesheet = "ProcessInstanceDiagram.css")
public class TimerInstanceItemView implements TakesValue<TimerInstanceSummary>,
                                              IsElement {

    private Constants constants = Constants.INSTANCE;

    @Inject
    @DataField("list-item")
    private ListItem listItem;

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
    @DataField("details")
    private HTMLDivElement details;

    @Inject
    @DataField("timer-id")
    @Bound
    @SuppressWarnings("unused")
    private Span id;

    @Inject
    @DataField("timer-delay")
    @Bound(converter = TimerInstanceDelayConverter.class)
    @SuppressWarnings("unused")
    private Span delay;

    @Inject
    @DataField("timer-period")
    @Bound
    @SuppressWarnings("unused")
    private Span period;

    @Inject
    @DataField("timer-repeat-limit")
    @Bound
    @SuppressWarnings("unused")
    private Span repeatLimit;

    @Inject
    @DataField("timer-last-fire-time")
    @Bound(converter = DateTimeNAConverter.class)
    @SuppressWarnings("unused")
    private Span lastFireTime;

    @Inject
    @DataField("timer-next-fire-time")
    @Bound(converter = DateTimeConverter.class)
    @SuppressWarnings("unused")
    private Span nextFireTime;

    @Inject
    @DataField("timer-activation-time")
    @Bound(converter = DateTimeConverter.class)
    @SuppressWarnings("unused")
    private Span activationTime;

    @Inject
    @AutoBound
    private DataBinder<TimerInstanceSummary> timerInstance;

    @Override
    public HTMLElement getElement() {
        return listItem;
    }

    @Override
    public TimerInstanceSummary getValue() {
        return timerInstance.getModel();
    }

    @Override
    public void setValue(final TimerInstanceSummary timer) {
        this.timerInstance.setModel(timer);

        if (timer.hasCallbacks()) {
            for (GenericSummary.LabeledCommand callback : timer.getCallbacks()) {
                addAction(e -> callback.getCommand().execute(),
                          constants.Reschedule());
            }
        } else {
            actionsItems.getClassList().add("hidden");
        }

        String id = Document.get().createUniqueId();
        label.setAttribute("href",
                           "#timer-instance-" + id);
        details.id = "timer-instance-" + id;
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
