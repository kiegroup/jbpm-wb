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

package org.jbpm.workbench.cm.client.list;

import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.client.util.CaseStatusConverter;
import org.jbpm.workbench.cm.client.util.CaseStatusEnum;
import org.jbpm.workbench.cm.client.util.DateConverter;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated(stylesheet = "CaseInstanceViewImpl.css")
public class CaseInstanceViewImpl extends AbstractView<CaseInstanceListPresenter> implements TakesValue<CaseInstanceSummary>, IsElement {

    @Inject
    @DataField("list-item")
    private Div row;

    @Inject
    @DataField("name")
    @Bound(property = "caseId")
    @SuppressWarnings("unused")
    private Span caseId;

    @Inject
    @DataField("description")
    @Bound
    @SuppressWarnings("unused")
    private Div description;

    @Inject
    @DataField("owner")
    @Bound
    @SuppressWarnings("unused")
    private Span owner;

    @Inject
    @DataField("status")
    @Bound(converter = CaseStatusConverter.class)
    private Span status;

    @Inject
    @DataField("started")
    @Bound(converter = DateConverter.class)
    @SuppressWarnings("unused")
    private Span startedAt;

    @Inject
    @DataField("complete")
    private Button complete;

    @Inject
    @DataField("kebab")
    private Div kebab;

    @Inject
    @DataField("case-details")
    private Div details;

    @Inject
    @AutoBound
    private DataBinder<CaseInstanceSummary> caseInstanceSummary;

    @PostConstruct
    public void init() {
        tooltip(status);
    }

    @Override
    public CaseInstanceSummary getValue() {
        return caseInstanceSummary.getModel();
    }

    @Override
    public void setValue(final CaseInstanceSummary model) {
        this.caseInstanceSummary.setModel(model);
        executeOnlyIfActive((c) -> {
            addCSSClass(this.details, "active");
            addCSSClass(this.status, "label-success");
            removeCSSClass(this.status, "label-default");
            removeCSSClass(this.complete, "hidden");
            removeCSSClass(this.kebab, "hidden");
        });
    }

    @Override
    public HTMLElement getElement() {
        return row;
    }

    @EventHandler("complete")
    public void onCompleteClick(final @ForEvent("click") MouseEvent event) {
        executeOnlyIfActive((c) -> presenter.cancelCaseInstance(c));
    }

    @EventHandler("close")
    public void onCloseClick(final @ForEvent("click") MouseEvent event) {
        executeOnlyIfActive((c) -> presenter.destroyCaseInstance(c));
    }

    @EventHandler("case-details")
    public void onCaseInstanceClick(final @ForEvent("click") MouseEvent event) {
        executeOnlyIfActive((c) -> presenter.selectCaseInstance(c));
    }

    private void executeOnlyIfActive(final Consumer<CaseInstanceSummary> consumer){
        final CaseInstanceSummary caseInstanceSummary = this.caseInstanceSummary.getModel();
        final CaseStatusEnum status = CaseStatusEnum.fromStatus(caseInstanceSummary.getStatus());
        if(status == CaseStatusEnum.ACTIVE){
            consumer.accept(caseInstanceSummary);
        }
    }

}