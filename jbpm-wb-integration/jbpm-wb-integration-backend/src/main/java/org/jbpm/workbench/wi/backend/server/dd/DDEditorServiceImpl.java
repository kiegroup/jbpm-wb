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

package org.jbpm.workbench.wi.backend.server.dd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.SourceVersion;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.model.Parameter;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.jbpm.workbench.wi.dd.validation.DeploymentDescriptorValidationMessage;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.internal.runtime.manager.deploy.DeploymentDescriptorIO;
import org.kie.internal.runtime.manager.deploy.DeploymentDescriptorImpl;
import org.kie.internal.runtime.manager.deploy.DeploymentDescriptorManager;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.kmodule.SingleValueItemObjectModel;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

@Service
@ApplicationScoped
public class DDEditorServiceImpl
        extends KieService<DeploymentDescriptorModel>
        implements DDEditorService {

    public static final String I18N_KEY_MISSING_IDENTIFIER = "DDValidationMissingIdentifier";
    public static final String I18N_KEY_MISSING_RESOLVER = "DDValidationMissingResolver";
    public static final String I18N_KEY_NOT_VALID_RESOLVER = "DDValidationNotValidResolver";
    public static final String I18N_KEY_NOT_VALID_REFLECTION_IDENTIFIER = "DDValidationNotValidReflectionIdentifier";
    public static final String I18N_KEY_NOT_VALID_MVEL_IDENTIFIER = "DDValidationNotValidMvelIdentifier";
    public static final String I18N_KEY_MISSING_NAME = "DDValidationMissingName";
    public static final String I18N_KEY_UNEXPECTED_ERROR = "DDValidationUnexpectedError";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private DDConfigUpdaterHelper configUpdaterHelper;

    @Inject
    private CommentedOptionFactory commentedOptionFactory;

    @Override
    public DeploymentDescriptorModel load(Path path) {
        return super.loadContent(path);
    }

    @Override
    protected DeploymentDescriptorModel constructContent(Path path,
                                                         Overview overview) {

        InputStream input = ioService.newInputStream(Paths.convert(path));

        DeploymentDescriptor originDD = DeploymentDescriptorIO.fromXml(input);

        DeploymentDescriptorModel ddModel = marshal(originDD);

        ddModel.setOverview(overview);

        return ddModel;
    }

    @Override
    public Path save(Path path,
                     DeploymentDescriptorModel content,
                     Metadata metadata,
                     String comment) {

        try {
            save(path,
                 content,
                 metadata,
                 commentedOptionFactory.makeCommentedOption(comment));
            return path;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    //Don't expose this method in the service API just in case we wants to remove the automatic updates for the descriptor.
    public Path save(Path path,
                     DeploymentDescriptorModel content,
                     Metadata metadata,
                     CommentedOption commentedOption) {

        try {
            String deploymentContent = unmarshal(path,
                                                 content).toXml();

            Metadata currentMetadata = metadataService.getMetadata(path);
            ioService.write(Paths.convert(path),
                            deploymentContent,
                            metadataService.setUpAttributes(path,
                                                            metadata),
                            commentedOption);

            fireMetadataSocialEvents(path,
                                     currentMetadata,
                                     metadata);

            return path;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public List<ValidationMessage> validate(Path path,
                                            DeploymentDescriptorModel content) {
        final List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        try {
            DeploymentDescriptor dd = unmarshal(path, content);

            // validate the content of the descriptor

            validationMessages.addAll(validateObjectModels(path, dd.getConfiguration()));
            validationMessages.addAll(validateObjectModels(path, dd.getEnvironmentEntries()));
            validationMessages.addAll(validateObjectModels(path, dd.getEventListeners()));
            validationMessages.addAll(validateObjectModels(path, dd.getGlobals()));
            validationMessages.addAll(validateObjectModels(path, dd.getMarshallingStrategies()));
            validationMessages.addAll(validateObjectModels(path, dd.getTaskEventListeners()));
            validationMessages.addAll(validateObjectModels(path, dd.getWorkItemHandlers()));

            // validate its structure
            dd.toXml();
        } catch (Exception e) {
            final ValidationMessage msg = new ValidationMessage();
            msg.setPath(path);
            msg.setLevel(Level.ERROR);
            msg.setText(e.getMessage());
            validationMessages.add(msg);
        }

        return validationMessages;
    }

    @Override
    public String toSource(Path path,
                           DeploymentDescriptorModel model) {
        try {
            return unmarshal(path,
                             model).toXml();
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    // helper methods

    protected List<ValidationMessage> validateObjectModels(Path path, List<? extends ObjectModel> objectModels) {

        final List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

        objectModels.forEach(model -> {

            String identifier = model.getIdentifier();

            if (identifier == null || identifier.isEmpty()) {
                validationMessages.add(
                        newMessage(
                                path,
                                "Identifier cannot be empty for " + model.getIdentifier(),
                                Level.ERROR,
                                I18N_KEY_MISSING_IDENTIFIER,
                                model.getIdentifier()));
            }

            String resolver = model.getResolver();
            if (resolver == null) {
                validationMessages.add(
                        newMessage(
                                path,
                                "No resolver selected for " + model.getIdentifier(),
                                Level.ERROR,
                                I18N_KEY_MISSING_RESOLVER,
                                model.getIdentifier()));
            }
            else if (resolver.equalsIgnoreCase(ItemObjectModel.MVEL_RESOLVER)) {
                try {
                    ParserContext parserContext = new ParserContext();
                    MVEL.compileExpression(identifier, parserContext);
                } catch (CompileException e) {
                    StringBuilder text = new StringBuilder();
                    text.append("Could not compile mvel expression '" + model.getIdentifier() +"'.")
                    .append(" this can be due to invalid syntax of missing classes")
                    .append("-")
                    .append(e.getMessage());
                    validationMessages.add(
                            newMessage(
                                    path,
                                    text.toString(),
                                    Level.WARNING,
                                    I18N_KEY_NOT_VALID_MVEL_IDENTIFIER,
                                    model.getIdentifier(),
                                    e.getMessage()));
                }
            } else if (resolver.equalsIgnoreCase(ItemObjectModel.REFLECTION_RESOLVER)) {
                if (!SourceVersion.isName(identifier)) {
                    validationMessages.add(
                            newMessage(
                                    path,
                                    "Identifier is not valid Java class which is required by reflection resolver " + model.getIdentifier(),
                                    Level.ERROR,
                                    I18N_KEY_NOT_VALID_REFLECTION_IDENTIFIER,
                                    model.getIdentifier()));
                }
            } else {
                validationMessages.add(
                        newMessage(
                                path,
                                "Not valid resolver selected for " + model.getIdentifier(),
                                Level.ERROR,
                                I18N_KEY_NOT_VALID_RESOLVER,
                                model.getIdentifier()));
            }


            if (model instanceof NamedObjectModel) {
                String name = ((NamedObjectModel) model).getName();
                if (name == null || name.isEmpty()) {
                    validationMessages.add(
                            newMessage(path,
                                    "Name cannot be empty for " + model.getIdentifier(),
                                    Level.ERROR,
                                    I18N_KEY_MISSING_NAME,
                                    model.getIdentifier()));
                }
            }
        });

        return validationMessages;
    }

    protected ValidationMessage newMessage(Path path, String text, Level level, String key, Object... args) {
        final DeploymentDescriptorValidationMessage msg = new DeploymentDescriptorValidationMessage();
        msg.setPath(path);
        msg.setLevel(level);
        msg.setText(text);
        msg.setKey(key);
        msg.setArgs(args);

        return msg;
    }

    protected DeploymentDescriptorModel marshal(DeploymentDescriptor originDD) {
        DeploymentDescriptorModel ddModel = new DeploymentDescriptorModel();
        ddModel.setPersistenceUnitName(originDD.getPersistenceUnit());
        ddModel.setAuditPersistenceUnitName(originDD.getAuditPersistenceUnit());
        ddModel.setAuditMode(originDD.getAuditMode().toString());
        ddModel.setPersistenceMode(originDD.getPersistenceMode().toString());

        if (originDD instanceof DeploymentDescriptorImpl && ((DeploymentDescriptorImpl) originDD).isEmpty()) {
            ddModel.setRuntimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE.toString());
        } else {
            ddModel.setRuntimeStrategy(originDD.getRuntimeStrategy().toString());
        }

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
        ddModel.setRequiredRoles(originDD.getRequiredRoles().stream().map( r -> new SingleValueItemObjectModel(r)).collect(Collectors.toList()));

        // remoteable classes
        ddModel.setRemotableClasses(originDD.getClasses().stream().map( c -> new SingleValueItemObjectModel(c)).collect(Collectors.toList()));

        ddModel.setLimitSerializationClasses(originDD.getLimitSerializationClasses());

        return ddModel;
    }

    protected DeploymentDescriptor unmarshal(Path path,
                                             DeploymentDescriptorModel model) {

        if (model == null) {
            return new DeploymentDescriptorManager().getDefaultDescriptor();
        }

        DeploymentDescriptor updated = new DeploymentDescriptorImpl();
        updated.getBuilder()
                .persistenceUnit(model.getPersistenceUnitName())
                .auditPersistenceUnit(model.getAuditPersistenceUnitName())
                .auditMode(AuditMode.valueOf(model.getAuditMode()))
                .persistenceMode(PersistenceMode.valueOf(model.getPersistenceMode()))
                .runtimeStrategy(RuntimeStrategy.valueOf(model.getRuntimeStrategy()))
                .setLimitSerializationClasses(model.getLimitSerializationClasses());

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
        updated.getBuilder().setRequiredRoles(model.getRequiredRoles().stream().map( r -> r.getValue()).collect(Collectors.toList()));

        // remoteable classes
        updated.getBuilder().setClasses(model.getRemotableClasses().stream().map( c -> c.getValue()).collect(Collectors.toList()));

        return updated;
    }

    private List<ItemObjectModel> processNamedObjectModel(List<NamedObjectModel> data) {
        List<ItemObjectModel> result = null;
        if (data != null) {
            result = new ArrayList<ItemObjectModel>();
            for (NamedObjectModel orig : data) {
                List<Parameter> parameters = collectParameters(orig.getParameters());

                result.add(new ItemObjectModel(orig.getName(),
                                               orig.getIdentifier(),
                                               orig.getResolver(),
                                               parameters));
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

                result.add(new ItemObjectModel(null,
                                               orig.getIdentifier(),
                                               orig.getResolver(),
                                               parameters));
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
                    result.add(new Parameter(model.getIdentifier(),
                                             model.getParameters().get(0).toString()));
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
                ObjectModel ms = new ObjectModel(item.getResolver(),
                                                 item.getValue());
                if (item.getParameters() != null) {
                    for (Parameter param : item.getParameters()) {
                        ObjectModel p = new ObjectModel(item.getResolver(),
                                                        param.getType(),
                                                        param.getValue().trim());
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
                NamedObjectModel ms = new NamedObjectModel(item.getResolver(),
                                                           item.getName(),
                                                           item.getValue());
                if (item.getParameters() != null) {
                    for (Parameter param : item.getParameters()) {
                        ObjectModel p = new ObjectModel(item.getResolver(),
                                                        param.getType(),
                                                        param.getValue().trim());
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
            DeploymentDescriptor dd = new DeploymentDescriptorManager("org.jbpm.domain").getDefaultDescriptor();

            if (configUpdaterHelper.hasPersistenceFile(path)) {
                //if current project has a persistence.xml file configured add the JPAMarshalling strategy.
                configUpdaterHelper.addJPAMarshallingStrategy(dd,
                                                              path);
            }

            String xmlDescriptor = dd.toXml();
            ioService.write(converted,
                            xmlDescriptor);
        }
    }

    @Override
    public boolean accepts(Path path) {
        return path.getFileName().equals("kie-deployment-descriptor.xml");
    }

    @Override
    public List<ValidationMessage> validate(Path path) {
        try {
            InputStream input = ioService.newInputStream(Paths.convert(path));
            DeploymentDescriptorModel ddModel = marshal(DeploymentDescriptorIO.fromXml(input));

            return validate(path, ddModel);
        } catch (Exception e) {
            return Arrays.asList(
                    newMessage(
                            path,
                            e.getMessage(),
                            Level.ERROR,
                            I18N_KEY_UNEXPECTED_ERROR,
                            e.getMessage()));
        }
    }
}
