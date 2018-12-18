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
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.RadioInput;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.client.views.pfly.widgets.SanitizedNumberInput;

@Dependent
@Templated
public class TimerInstanceRescheduleView implements TakesValue<TimerInstanceSummary>,
                                                    IsElement {

    @Inject
    @DataField("modal")
    private Modal modal;

    @Inject
    @DataField("cancel")
    @SuppressWarnings("unused")
    private HTMLButtonElement cancel;

    @Inject
    @DataField("reschedule")
    @SuppressWarnings("unused")
    private HTMLButtonElement reschedule;

    @Inject
    @DataField("timer-delay-input")
    @Bound
    @SuppressWarnings("unused")
    private SanitizedNumberInput delay;

    @Inject
    @DataField("timer-period-input")
    @Bound
    @SuppressWarnings("unused")
    private SanitizedNumberInput period;

    @Inject
    @DataField("timer-repeat-input")
    @Bound
    @SuppressWarnings("unused")
    private SanitizedNumberInput repeatLimit;

    @Inject
    @DataField("timer-relative")
    @Bound
    @SuppressWarnings("unused")
    private RadioInput relative;

    @Inject
    @AutoBound
    private DataBinder<TimerInstanceSummary> timerInstance;

    private Callback<TimerInstanceSummary> onReschedule;

    public void show() {
        cleanForm();
        modal.show();
    }

    public void cleanForm() {
    }

    public void setOnReschedule(Callback<TimerInstanceSummary> onReschedule) {
        this.onReschedule = onReschedule;
    }

    @Override
    public TimerInstanceSummary getValue() {
        return timerInstance.getModel();
    }

    @Override
    public void setValue(TimerInstanceSummary timer) {
        timerInstance.setModel(timer);
    }

    @EventHandler("cancel")
    public void onCancelClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }

    @EventHandler("reschedule")
    public void onCloseClick(final @ForEvent("click") MouseEvent event) {
        hide();
        if (onReschedule != null) {
            onReschedule.callback(timerInstance.getModel());
        }
    }

    public void hide() {
        modal.hide();
    }

    public HTMLElement getElement() {
        return modal.getElement();
    }
}
