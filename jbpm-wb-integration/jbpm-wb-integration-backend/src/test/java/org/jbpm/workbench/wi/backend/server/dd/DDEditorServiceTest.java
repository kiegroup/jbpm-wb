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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.validation.DeploymentDescriptorValidationMessage;
import org.jgroups.util.UUID;
import org.junit.Test;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.test.util.compare.ComparePair;

import static org.assertj.core.api.Assertions.*;

public class DDEditorServiceTest extends DDEditorServiceImpl {

    private static Random random = new Random();

    private static ObjectModel getObjectModelParameter(String resolver) {
        return new ObjectModel(resolver,
                               UUID.randomUUID().toString(),
                               Integer.toString(random.nextInt(100000)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void marshalUnmarshalTest() throws Exception {
        // setup
        Map<String, Field> depDescFields = new HashMap<>();
        Map<String, Field> depDescModelFields = new HashMap<>();

        Map<String, Field>[] fieldMaps = new Map[]{depDescFields, depDescModelFields};
        Field[][] fields = new Field[][]{DeploymentDescriptorImpl.class.getDeclaredFields(),
                DeploymentDescriptorModel.class.getDeclaredFields()};

        for (int i = 0; i < fieldMaps.length; ++i) {
            for (Field field : fields[i]) {
                fieldMaps[i].put(field.getName(),
                                 field);
            }
        }

        // test data
        DeploymentDescriptorImpl origDepDesc = new DeploymentDescriptorImpl();
        origDepDesc.setAuditMode(AuditMode.JMS);
        origDepDesc.setAuditPersistenceUnit("audit-persist");
        origDepDesc.setClasses(Collections.singletonList("class1"));

        origDepDesc.setLimitSerializationClasses(true);
        origDepDesc.setPersistenceMode(PersistenceMode.JPA);
        origDepDesc.setPersistenceUnit("save-thingy");
        origDepDesc.setRequiredRoles(Collections.singletonList("roles"));
        origDepDesc.setRuntimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);

        String resolver = "config-resolver";
        origDepDesc.setConfiguration(Collections.singletonList(
                new NamedObjectModel(resolver,
                                     "config-name",
                                     "classname",
                                     getObjectModelParameter(resolver))));
        resolver = "env-resolver";
        origDepDesc.setEnvironmentEntries(Collections.singletonList(
                new NamedObjectModel(resolver,
                                     "env-name",
                                     "classname",
                                     getObjectModelParameter(resolver))));
        resolver = "event-resolver";
        origDepDesc.setEventListeners(
                Collections.singletonList(new ObjectModel(resolver,
                                                          "listener-id",
                                                          getObjectModelParameter(resolver))));
        resolver = "glob-resolver";
        origDepDesc.setGlobals(Collections.singletonList(
                new NamedObjectModel(resolver,
                                     "glob-name",
                                     "classname",
                                     getObjectModelParameter(resolver))));
        resolver = "marsh-resolver";
        origDepDesc.setMarshallingStrategies(
                Collections.singletonList(new ObjectModel(resolver,
                                                          "marsh-id",
                                                          getObjectModelParameter(resolver))));
        resolver = "task-resolver";
        origDepDesc.setTaskEventListeners(
                Collections.singletonList(new ObjectModel(resolver,
                                                          "listener-id",
                                                          getObjectModelParameter(resolver))));
        resolver = "work-resolver";
        origDepDesc.setWorkItemHandlers(Collections.singletonList(
                new NamedObjectModel(resolver,
                                     "item-name",
                                     "handler-classname",
                                     getObjectModelParameter(resolver))));

        // round trip DeploymenDescriptor
        DeploymentDescriptorModel origModel = marshal(origDepDesc);

        // path argument not used in unmarshal??
        DeploymentDescriptor copyDepDesc = unmarshal(null,
                                                     origModel);

        // compare round-tripped DeploymentDescriptor
        ComparePair.compareObjectsViaFields(origDepDesc,
                                            copyDepDesc,
                                            "mappedRoles");

        // round trip DeploymenDescriptorModel
        DeploymentDescriptorModel copyModel = marshal(copyDepDesc);

        // compare round-tripped DeploymentDescriptorModel
        new ComparePair(origModel,
                        copyModel)
                .useFields()
                .addNullFields("ItemObjectModel.name",
                               "DeploymentDescriptorModel.overview")
                .compare();
    }

    @Test
    public void testValidateEmptyResolver() {
        List<ValidationMessage> validationMessages = validateObjectModels(null,
                                                                          Collections.singletonList(
                                                                                  new NamedObjectModel(null,
                                                                                                       "item-name",
                                                                                                       "handler-classname")));

        assertThat(validationMessages).hasSize(1);

        ValidationMessage error = validationMessages.get(0);
        assertThat(error.getLevel()).isEqualTo(Level.ERROR);
        assertThat(error.getText()).startsWith("No resolver selected");

        validationMessages = validateObjectModels(null,
                                                  Collections.singletonList(
                                                          new NamedObjectModel("",
                                                                               "item-name",
                                                                               "handler-classname")));
        assertThat(validationMessages.size()).isEqualTo(1);

        error = validationMessages.get(0);
        assertThat(error.getLevel()).isEqualTo(Level.ERROR);
        assertThat(error.getText()).startsWith("Not valid resolver selected");

        assertThat(error).isInstanceOf(DeploymentDescriptorValidationMessage.class);
        assertThat(((DeploymentDescriptorValidationMessage) error).getKey()).isEqualTo(I18N_KEY_NOT_VALID_RESOLVER);
    }

    @Test
    public void testValidateReflectionResolver() {

        List<ValidationMessage> validationMessages = validateObjectModels(null,
                                                                          Collections.singletonList(
                                                                                  new NamedObjectModel(ItemObjectModel.REFLECTION_RESOLVER,
                                                                                                       "item-name",
                                                                                                       "java.lang.String")));
        assertThat(validationMessages).isEmpty();
    }

    @Test
    public void testValidateReflectionResolverInvalid() {
        List<ValidationMessage> validationMessages = validateObjectModels(null,
                                                                          Collections.singletonList(
                                                                                  new NamedObjectModel(ItemObjectModel.REFLECTION_RESOLVER,
                                                                                                       "item-name",
                                                                                                       "handler-classname")));

        assertThat(validationMessages).hasSize(1);

        ValidationMessage error = validationMessages.get(0);
        assertThat(error.getLevel()).isEqualTo(Level.ERROR);
        assertThat(error.getText()).startsWith("Identifier is not valid Java class which is required by reflection resolver");

        assertThat(error).isInstanceOf(DeploymentDescriptorValidationMessage.class);
        assertThat(((DeploymentDescriptorValidationMessage) error).getKey()).isEqualTo(I18N_KEY_NOT_VALID_REFLECTION_IDENTIFIER);
    }

    @Test
    public void testValidateReflectionResolverMissingIdentifier() {

        List<ValidationMessage> validationMessages = validateObjectModels(null,
                                                                          Collections.singletonList(
                                                                                  new NamedObjectModel(ItemObjectModel.REFLECTION_RESOLVER,
                                                                                                       "item-name",
                                                                                                       "")));
        assertThat(validationMessages).hasSize(2);

        ValidationMessage error = validationMessages.get(0);
        assertThat(error.getLevel()).isEqualTo(Level.ERROR);
        assertThat(error.getText()).startsWith("Identifier cannot be empty");

        assertThat(error).isInstanceOf(DeploymentDescriptorValidationMessage.class);
        assertThat(((DeploymentDescriptorValidationMessage) error).getKey()).isEqualTo(I18N_KEY_MISSING_IDENTIFIER);

        error = validationMessages.get(1);
        assertThat(error.getLevel()).isEqualTo(Level.ERROR);
        assertThat(error.getText()).startsWith("Identifier is not valid Java class which is required by reflection resolver");

        assertThat(error).isInstanceOf(DeploymentDescriptorValidationMessage.class);
        assertThat(((DeploymentDescriptorValidationMessage) error).getKey()).isEqualTo(I18N_KEY_NOT_VALID_REFLECTION_IDENTIFIER);
    }

    @Test
    public void testValidateReflectionResolverNameEmpty() {

        List<ValidationMessage> validationMessages = validateObjectModels(null,
                                                                          Collections.singletonList(
                                                                                  new NamedObjectModel(ItemObjectModel.REFLECTION_RESOLVER,
                                                                                                       "",
                                                                                                       "java.lang.String")));
        assertThat(validationMessages).hasSize(1);

        ValidationMessage error = validationMessages.get(0);
        assertThat(error.getLevel()).isEqualTo(Level.ERROR);
        assertThat(error.getText()).startsWith("Name cannot be empty");

        assertThat(error).isInstanceOf(DeploymentDescriptorValidationMessage.class);
        assertThat(((DeploymentDescriptorValidationMessage) error).getKey()).isEqualTo(I18N_KEY_MISSING_NAME);
    }

    @Test
    public void testValidateMvelResolver() {

        List<ValidationMessage> validationMessages = validateObjectModels(null,
                                                                          Collections.singletonList(
                                                                                  new NamedObjectModel(ItemObjectModel.MVEL_RESOLVER,
                                                                                                       "item-name",
                                                                                                       "new String()")));
        assertThat(validationMessages).isEmpty();
    }

    @Test
    public void testValidateMvelResolverWithSupportedParameters() {
        List<ValidationMessage> validationMessages = validateObjectModels(null,
                                                                          Collections.singletonList(
                                                                                  new NamedObjectModel(ItemObjectModel.MVEL_RESOLVER,
                                                                                                       "item-name",
                                                                                                       "java.util.Arrays.asList(ksession, taskService, runtimeManager, classLoader, entityManagerFactory);")));
        assertThat(validationMessages).isEmpty();
    }

    @Test
    public void testValidateMvelResolverWithUnSupportedParameters() {

        List<ValidationMessage> validationMessages = validateObjectModels(null,
                                                                          Collections.singletonList(
                                                                                  new NamedObjectModel(ItemObjectModel.MVEL_RESOLVER,
                                                                                                       "item-name",
                                                                                                       "java.util.Arrays.asList(unsupportedParamName);")));

        assertThat(validationMessages).hasSize(1);

        ValidationMessage error = validationMessages.get(0);
        assertThat(error.getLevel()).isEqualTo(Level.WARNING);
        assertThat(error.getText()).startsWith("Could not compile mvel expression");

        assertThat(error).isInstanceOf(DeploymentDescriptorValidationMessage.class);
        assertThat(((DeploymentDescriptorValidationMessage) error).getKey()).isEqualTo(I18N_KEY_NOT_VALID_MVEL_IDENTIFIER);
    }

    @Test
    public void testValidateMvelResolverInvalid() {

        List<ValidationMessage> validationMessages = validateObjectModels(null,
                                                                          Collections.singletonList(
                                                                                  new NamedObjectModel(ItemObjectModel.MVEL_RESOLVER,
                                                                                                       "item-name",
                                                                                                       "handler-classname")));
        assertThat(validationMessages).hasSize(1);

        ValidationMessage error = validationMessages.get(0);
        assertThat(error.getLevel()).isEqualTo(Level.WARNING);
        assertThat(error.getText()).startsWith("Could not compile mvel expression");

        assertThat(error).isInstanceOf(DeploymentDescriptorValidationMessage.class);
        assertThat(((DeploymentDescriptorValidationMessage) error).getKey()).isEqualTo(I18N_KEY_NOT_VALID_MVEL_IDENTIFIER);
    }
}