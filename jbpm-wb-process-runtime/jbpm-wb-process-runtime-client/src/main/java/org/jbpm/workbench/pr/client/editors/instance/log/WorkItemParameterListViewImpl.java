/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.instance.log;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.model.WorkItemParameterSummary;

@Dependent
@Templated
public class WorkItemParameterListViewImpl implements TakesValue<WorkItemParameterSummary>,
                                                      IsElement {

    @Inject
    @DataField("param")
    Div param;

    @Inject
    @DataField("id")
    @Bound
    Span id;

    @Inject
    @DataField("name")
    @Bound
    Span name;

    @Inject
    @AutoBound
    private DataBinder<WorkItemParameterSummary> workItemParameter;

    @Override
    public HTMLElement getElement() {
        return param;
    }

    @Override
    public WorkItemParameterSummary getValue() {
        return this.workItemParameter.getModel();
    }

    @Override
    public void setValue(final WorkItemParameterSummary model) {
        this.workItemParameter.setModel(model);
    }
}
