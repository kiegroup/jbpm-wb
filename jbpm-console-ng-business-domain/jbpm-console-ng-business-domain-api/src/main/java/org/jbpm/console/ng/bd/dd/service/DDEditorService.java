package org.jbpm.console.ng.bd.dd.service;

import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.guvnor.common.services.shared.validation.ValidationService;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.bd.dd.model.DeploymentDescriptorModel;
import org.kie.workbench.common.services.shared.source.ViewSourceService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;

@Remote
public interface DDEditorService extends ViewSourceService<DeploymentDescriptorModel>,
                                         ValidationService<DeploymentDescriptorModel>,
                                         SupportsRead<DeploymentDescriptorModel>,
                                         SupportsUpdate<DeploymentDescriptorModel> {

    void createIfNotExists(Path path);
}
