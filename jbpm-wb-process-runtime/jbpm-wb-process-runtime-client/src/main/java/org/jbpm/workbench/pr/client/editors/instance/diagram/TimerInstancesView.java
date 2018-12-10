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

import com.google.gwt.user.client.TakesValue;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;

@Templated
@Dependent
public class TimerInstancesView implements TakesValue<List<TimerInstanceSummary>>,
                                           IsElement {

    @Inject
    @DataField("container")
    private HTMLDivElement container;

    @Inject
    @DataField("timer-instances-counter")
    private Span timerInstancesCounter;

    @Inject
    @Bound
    @DataField("timer-instances")
    @ListContainer("ul")
    @SuppressWarnings("unused")
    private ListComponent<TimerInstanceSummary, TimerInstanceItemView> timers;

    @Inject
    @AutoBound
    private DataBinder<List<TimerInstanceSummary>> timerList;

    @Override
    public void setValue(List<TimerInstanceSummary> timers) {
        timerList.setModel(timers);
        timerInstancesCounter.setTextContent(String.valueOf(timers.size()));
    }

    @Override
    public List<TimerInstanceSummary> getValue() {
        return timerList.getModel();
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }
}
