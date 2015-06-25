/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.wi.client.editors.deployment.descriptor;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.wi.dd.model.DeploymentDescriptorModel;

import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface DeploymentDescriptorView extends KieEditorView, IsWidget {

    void setContent(final DeploymentDescriptorModel deploymentDescriptorModel);

    void updateContent(final DeploymentDescriptorModel deploymentDescriptorModel);

    boolean confirmClose();
}
