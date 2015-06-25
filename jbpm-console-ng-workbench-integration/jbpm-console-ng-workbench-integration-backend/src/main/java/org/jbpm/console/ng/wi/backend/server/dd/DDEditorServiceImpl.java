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

package org.jbpm.console.ng.wi.backend.server.dd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringEscapeUtils;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.console.ng.wi.dd.model.ItemObjectModel;
import org.jbpm.console.ng.wi.dd.model.Parameter;
import org.jbpm.console.ng.wi.dd.service.DDEditorService;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorIO;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorManager;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.workbench.common.services.backend.service.KieService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class DDEditorServiceImpl
        extends KieService<DeploymentDescriptorModel>
        implements DDEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public DeploymentDescriptorModel load(Path path) {
        return super.loadContent(path);
    }

    @Override
    protected DeploymentDescriptorModel constructContent(Path path, Overview overview) {

        InputStream input = ioService.newInputStream(Paths.convert(path));

        org.kie.internal.runtime.conf.DeploymentDescriptor originDD = DeploymentDescriptorIO.fromXml(input);

        DeploymentDescriptorModel ddModel = marshal(originDD);

        ddModel.setOverview(overview);

        return ddModel;
    }

    @Override
    public Path save(Path path, DeploymentDescriptorModel content, Metadata metadata, String comment) {

        try {
            String deploymentContent = unmarshal(path, content).toXml();

            Metadata currentMetadata = metadataService.getMetadata( path );
            ioService.write(Paths.convert(path),
                            deploymentContent,
                            metadataService.setUpAttributes(path,
                                                            metadata),
                            makeCommentedOption(comment));

            fireMetadataSocialEvents( path, currentMetadata, metadata );

            return path;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public List<ValidationMessage> validate(Path path, DeploymentDescriptorModel content) {
        final List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        try {
            unmarshal(path, content).toXml();
        } catch (Exception e) {
            final ValidationMessage msg = new ValidationMessage();
            msg.setPath(path);
            msg.setLevel(ValidationMessage.Level.ERROR);
            msg.setText(e.getMessage());
            validationMessages.add(msg);
        }

        return validationMessages;
    }

    @Override
    public String toSource(Path path, DeploymentDescriptorModel model) {
        try {
            return StringEscapeUtils.escapeXml(unmarshal(path, model).toXml());

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    // helper methods

    private DeploymentDescriptorModel marshal(org.kie.internal.runtime.conf.DeploymentDescriptor originDD) {
        DeploymentDescriptorModel ddModel = new DeploymentDescriptorModel();
        ddModel.setPersistenceUnitName(originDD.getPersistenceUnit());
        ddModel.setAuditPersistenceUnitName(originDD.getAuditPersistenceUnit());
        ddModel.setAuditMode(originDD.getAuditMode().toString());
        ddModel.setPersistenceMode(originDD.getPersistenceMode().toString());
        ddModel.setRuntimeStrategy(originDD.getRuntimeStrategy().toString());

        // marshaling strategies
        List<ObjectModel> marshallingStrategies = originDD.getMarshallingStrategies();
        ddModel.setMarshallingStrategies(processObjectModel(marshallingStrategies));

        // event listeners
        List<ObjectModel> eventListeners = originDD.getEventListeners();
        ddModel.setEventListeners(processObjectModel(eventListeners));

        // globals
        List<NamedObjectModel> globals = originDD.getGlobals();
        ddModel.setGlobals(processNamedObjectModel(globals));

        // work item handlers
        List<NamedObjectModel> workItemHandlers = originDD.getWorkItemHandlers();
        ddModel.setWorkItemHandlers(processNamedObjectModel(workItemHandlers));

        // event listeners
        List<ObjectModel> taskEventListeners = originDD.getTaskEventListeners();
        ddModel.setTaskEventListeners(processObjectModel(taskEventListeners));

        // environment entries
        List<NamedObjectModel> environmentEntries = originDD.getEnvironmentEntries();
        ddModel.setEnvironmentEntries(processNamedObjectModel(environmentEntries));

        // configuration
        List<NamedObjectModel> configuration = originDD.getConfiguration();
        ddModel.setConfiguration(processNamedObjectModel(configuration));

        // required roles
        ddModel.setRequiredRoles(originDD.getRequiredRoles());

        // remoteable classes
        ddModel.setRemotableClasses(originDD.getClasses());


        return ddModel;
    }


    private org.kie.internal.runtime.conf.DeploymentDescriptor unmarshal(Path path, DeploymentDescriptorModel model) {

        if (model == null) {
            return new DeploymentDescriptorManager().getDefaultDescriptor();
        }

        DeploymentDescriptor updated = new DeploymentDescriptorImpl();
        updated.getBuilder()
                .persistenceUnit(model.getPersistenceUnitName())
                .auditPersistenceUnit(model.getAuditPersistenceUnitName())
                .auditMode(AuditMode.valueOf(model.getAuditMode()))
                .persistenceMode(PersistenceMode.valueOf(model.getPersistenceMode()))
                .runtimeStrategy(RuntimeStrategy.valueOf(model.getRuntimeStrategy()));

        // marshalling strategies
        List<ItemObjectModel> marshallingStrategies = model.getMarshallingStrategies();
        updated.getBuilder().setMarshalingStrategies(processToObjectModel(marshallingStrategies));

        // event listeners
        List<ItemObjectModel> eventListeners = model.getEventListeners();
        updated.getBuilder().setEventListeners(processToObjectModel(eventListeners));

        // globals
        List<ItemObjectModel> globals = model.getGlobals();
        updated.getBuilder().setGlobals(processToNamedObjectModel(globals));

        // work item handlers
        List<ItemObjectModel> workItemHandlers = model.getWorkItemHandlers();
        updated.getBuilder().setWorkItemHandlers(processToNamedObjectModel(workItemHandlers));

        // task event listeners
        List<ItemObjectModel> taskEventListeners = model.getTaskEventListeners();
        updated.getBuilder().setTaskEventListeners(processToObjectModel(taskEventListeners));

        // environment entries
        List<ItemObjectModel> environmentEntries = model.getEnvironmentEntries();
        updated.getBuilder().setEnvironmentEntries(processToNamedObjectModel(environmentEntries));

        // configuration
        List<ItemObjectModel> configuration = model.getConfiguration();
        updated.getBuilder().setConfiguration(processToNamedObjectModel(configuration));

        // required roles
        updated.getBuilder().setRequiredRoles(model.getRequiredRoles());

        // remoteable classes
        updated.getBuilder().setClasses(model.getRemotableClasses());

        return updated;

    }

    private List<ItemObjectModel> processNamedObjectModel(List<NamedObjectModel> data) {
        List<ItemObjectModel> result = null;
        if (data != null) {
            result = new ArrayList<ItemObjectModel>();
            for (NamedObjectModel orig : data) {
                List<Parameter> parameters = collectParameters(orig.getParameters());

                result.add(new ItemObjectModel(orig.getName(), orig.getIdentifier(), orig.getResolver(), parameters));
            }
        }

        return result;
    }

    private List<ItemObjectModel> processObjectModel(List<ObjectModel> data) {
        List<ItemObjectModel> result = null;
        if (data != null) {
            result = new ArrayList<ItemObjectModel>();
            for (ObjectModel orig : data) {
                List<Parameter> parameters = collectParameters(orig.getParameters());

                result.add(new ItemObjectModel(null, orig.getIdentifier(), orig.getResolver(), parameters));
            }
        }

        return result;
    }

    private List<Parameter> collectParameters(List<Object> parameters) {
        List<Parameter> result = null;
        if (parameters != null && !parameters.isEmpty()) {
            result = new ArrayList<Parameter>();

            for (Object param : parameters) {
                if (param instanceof ObjectModel) {
                    ObjectModel model = (ObjectModel) param;
                    result.add(new Parameter(model.getIdentifier(), model.getParameters().get(0).toString()));
                }
            }
        }
        return result;
    }


    private List<ObjectModel> processToObjectModel(List<ItemObjectModel> data) {
        List<ObjectModel> result = null;
        if (data != null) {
            result = new ArrayList<ObjectModel>();
            for (ItemObjectModel item : data) {
                ObjectModel ms = new ObjectModel(item.getResolver(), item.getValue());
                if (item.getParameters() != null) {
                    for (Parameter param : item.getParameters()) {
                        ObjectModel p = new ObjectModel(item.getResolver(), param.getType(), param.getValue().trim());
                        ms.addParameter(p);
                    }
                }
                result.add(ms);
            }
        }

        return result;
    }

    private List<NamedObjectModel> processToNamedObjectModel(List<ItemObjectModel> data) {
        List<NamedObjectModel> result = null;
        if (data != null) {
            result = new ArrayList<NamedObjectModel>();
            for (ItemObjectModel item : data) {
                NamedObjectModel ms = new NamedObjectModel(item.getResolver(), item.getName(), item.getValue());
                if (item.getParameters() != null) {
                    for (Parameter param : item.getParameters()) {
                        ObjectModel p = new ObjectModel(item.getResolver(), param.getType(), param.getValue().trim());
                        ms.addParameter(p);
                    }
                }
                result.add(ms);
            }
        }

        return result;
    }

    @Override
    public void createIfNotExists(Path path) {
        org.uberfire.java.nio.file.Path converted = Paths.convert(path);
        if (!ioService.exists(converted)) {
            // create descriptor
            DeploymentDescriptor dd = new DeploymentDescriptorManager( "org.jbpm.domain" ).getDefaultDescriptor();
            String xmlDescriptor = dd.toXml();
            ioService.write( converted, xmlDescriptor );
        }
    }
}
