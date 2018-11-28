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

import com.google.gwt.user.client.TakesValue;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface ProcessInstanceDiagramView extends HasBusyIndicator,
                                                    UberView<ProcessInstanceDiagramPresenter>,
                                                    TakesValue<ProcessNodeSummary> {

    void displayImage(String svgContent);

    void displayMessage(String message);

    void setProcessNodes(List<ProcessNodeSummary> nodes);
}
