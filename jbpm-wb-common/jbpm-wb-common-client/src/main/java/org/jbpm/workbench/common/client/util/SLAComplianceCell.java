/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.common.client.util;

import java.util.ArrayList;
import java.util.List;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.kie.api.runtime.process.ProcessInstance;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class SLAComplianceCell extends AbstractCell<Integer> {

    private final Constants constants = Constants.INSTANCE;

    private static final String SLA_STYLE = "label";
    private static final String SLA_NA_PRESENT_STYLE = "label-default";
    private static final String SLA_PENDING_PRESENT_STYLE = "label-primary";
    private static final String SLA_MET_PRESENT_STYLE = "label-success";
    private static final String SLA_ABORTED_PRESENT_STYLE = "label-warning";
    private static final String SLA_VIOLATED_PRESENT_STYLE = "label-danger";

    @Override
    public void render(Context context,
                       Integer value,
                       SafeHtmlBuilder sb) {

        List<String> tooltipClasses = new ArrayList<>();

        tooltipClasses.add(SLA_STYLE);
        String description;
        switch (value) {
            case ProcessInstance.SLA_NA:
                description = constants.SlaNA();
                tooltipClasses.add(SLA_NA_PRESENT_STYLE);

                break;
            case ProcessInstance.SLA_PENDING:
                description = constants.SlaPending();
                tooltipClasses.add(SLA_PENDING_PRESENT_STYLE);
                break;
            case ProcessInstance.SLA_MET:
                description = constants.SlaMet();
                tooltipClasses.add(SLA_MET_PRESENT_STYLE);
                break;
            case ProcessInstance.SLA_ABORTED:
                description = constants.SlaAborted();
                tooltipClasses.add(SLA_ABORTED_PRESENT_STYLE);
                break;
            case ProcessInstance.SLA_VIOLATED:
                description = constants.SlaViolated();
                tooltipClasses.add(SLA_VIOLATED_PRESENT_STYLE);
                break;
            default:
                description = constants.Unknown();
                tooltipClasses.add(SLA_NA_PRESENT_STYLE);
                break;
        }

        Element span = DomGlobal.document.createElement("span");
        span.textContent = description;
        tooltipClasses.forEach(c -> span.classList.add(c));
        Element content = DomGlobal.document.createElement("span");
        content.appendChild(span);
        sb.appendHtmlConstant(content.innerHTML);
    }
}