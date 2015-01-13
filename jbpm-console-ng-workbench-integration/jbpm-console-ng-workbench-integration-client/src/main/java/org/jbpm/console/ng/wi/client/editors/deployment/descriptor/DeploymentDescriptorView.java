package org.jbpm.console.ng.wi.client.editors.deployment.descriptor;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.wi.dd.model.DeploymentDescriptorModel;

import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface DeploymentDescriptorView extends KieEditorView, IsWidget {

    void setContent(final DeploymentDescriptorModel deploymentDescriptorModel);

    void updateContent(final DeploymentDescriptorModel deploymentDescriptorModel);

    boolean confirmClose();
}
