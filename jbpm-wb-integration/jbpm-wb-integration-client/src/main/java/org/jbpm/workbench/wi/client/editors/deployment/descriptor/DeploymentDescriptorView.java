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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;

import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface DeploymentDescriptorView extends KieEditorView, IsWidget {

    void setup();

    void setContent(final DeploymentDescriptorModel deploymentDescriptorModel);

    void updateContent(final DeploymentDescriptorModel deploymentDescriptorModel);

    boolean confirmClose();

    Widget getSourceEditor();

    void setSource( String source );

    void addRuntimeStrategy(String runtimeStrategyTitle, String runmimeStrategyValue);

    void addPersistenceMode(String persistenceModeTitle, String persistenceModeValue);

    void addAuditMode(String auditModeTitle, String auditModeValue);

    void setSourceTabReadOnly(boolean readOnly);

}
