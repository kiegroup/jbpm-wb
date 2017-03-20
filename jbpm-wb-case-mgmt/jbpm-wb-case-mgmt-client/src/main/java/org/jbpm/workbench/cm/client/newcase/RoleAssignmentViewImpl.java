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

package org.jbpm.workbench.cm.client.newcase;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TableRow;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.util.CommaListValuesConverter;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;

@Dependent
@Templated
public class RoleAssignmentViewImpl implements TakesValue<CaseRoleAssignmentSummary>,
                                               IsElement {

    @Inject
    @DataField("role")
    TableRow role;

    @Inject
    @DataField("name")
    @Bound
    Span name;

    @Inject
    @DataField("users")
    @Bound(converter = CommaListValuesConverter.class)
    TextInput users;

    @Inject
    @DataField("groups")
    @Bound(converter = CommaListValuesConverter.class)
    TextInput groups;

    @Inject
    @AutoBound
    private DataBinder<CaseRoleAssignmentSummary> roleAssignment;

    @Override
    public HTMLElement getElement() {
        return role;
    }

    @Override
    public CaseRoleAssignmentSummary getValue() {
        return this.roleAssignment.getModel();
    }

    @Override
    public void setValue(final CaseRoleAssignmentSummary model) {
        this.roleAssignment.setModel(model);
    }
}
