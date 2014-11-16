package org.jbpm.console.ng.wi.dd.service;

import org.jboss.errai.bus.server.annotations.Remote;

import org.guvnor.common.services.shared.file.SupportsRead;
import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.kie.workbench.common.services.shared.source.ViewSourceService;
import org.guvnor.common.services.shared.validation.ValidationService;
import org.jbpm.console.ng.wi.dd.model.DeploymentDescriptorModel;
import org.uberfire.backend.vfs.Path;

@Remote
public interface DDEditorService extends ViewSourceService<DeploymentDescriptorModel>,
                                         ValidationService<DeploymentDescriptorModel>,
                                         SupportsRead<DeploymentDescriptorModel>,
                                         SupportsUpdate<DeploymentDescriptorModel> {

    void createIfNotExists(Path path);
}
