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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jgroups.util.UUID;
import org.junit.Test;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.test.util.compare.ComparePair;

public class DDEditorServiceTest extends DDEditorServiceImpl {

    @Test
    @SuppressWarnings("unchecked")
    public void marshalUnmarshalTest() throws Exception {
        // setup
        Map<String, Field> depDescFields = new HashMap<String, Field>();
        Map<String, Field> depDescModelFields = new HashMap<String, Field>();

        Map<String, Field>[] fieldMaps = new Map[] { depDescFields, depDescModelFields };
        Field[][] fields = new Field[][] { DeploymentDescriptorImpl.class.getDeclaredFields(),
                DeploymentDescriptorModel.class.getDeclaredFields() };

        for( int i = 0; i < fieldMaps.length; ++i ) {
            for( Field field : fields[i] ) {
                fieldMaps[i].put(field.getName(), field);
            }
        }

        // test data
        DeploymentDescriptorImpl origDepDesc = new DeploymentDescriptorImpl();
        origDepDesc.setAuditMode(AuditMode.JMS);
        origDepDesc.setAuditPersistenceUnit("audit-persist");
        origDepDesc.setClasses(Arrays.asList(new String[] { "class1" }));

        origDepDesc.setLimitSerializationClasses(true);
        origDepDesc.setPersistenceMode(PersistenceMode.JPA);
        origDepDesc.setPersistenceUnit("save-thingy");
        origDepDesc.setRequiredRoles(Arrays.asList(new String[] { "roles" }));
        origDepDesc.setRuntimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);

        String resolver = "config-resolver";
        origDepDesc.setConfiguration(Arrays.asList(new NamedObjectModel[] {
                new NamedObjectModel(resolver, "config-name", "classname", getObjectModelParameter(resolver)) }));
        resolver = "env-resolver";
        origDepDesc.setEnvironmentEntries(Arrays.asList(new NamedObjectModel[] {
                new NamedObjectModel(resolver, "env-name", "classname", getObjectModelParameter(resolver)) }));
        resolver = "event-resolver";
        origDepDesc.setEventListeners(
                Arrays.asList(new ObjectModel[] { new ObjectModel(resolver, "listener-id", getObjectModelParameter(resolver)) }));
        resolver = "glob-resolver";
        origDepDesc.setGlobals(Arrays.asList(new NamedObjectModel[] {
                new NamedObjectModel(resolver, "glob-name", "classname", getObjectModelParameter(resolver)) }));
        resolver = "marsh-resolver";
        origDepDesc.setMarshallingStrategies(
                Arrays.asList(new ObjectModel[] { new ObjectModel(resolver, "marsh-id", getObjectModelParameter(resolver)) }));
        resolver = "task-resolver";
        origDepDesc.setTaskEventListeners(
                Arrays.asList(new ObjectModel[] { new ObjectModel(resolver, "listener-id", getObjectModelParameter(resolver)) }));
        resolver = "work-resolver";
        origDepDesc.setWorkItemHandlers(Arrays.asList(new NamedObjectModel[] {
                new NamedObjectModel(resolver, "item-name", "handler-classname", getObjectModelParameter(resolver)) }));

        // round trip DeploymenDescriptor
        DeploymentDescriptorModel origModel = marshal(origDepDesc);

        // path argument not used in unmarshal??
        DeploymentDescriptor copyDepDesc = unmarshal(null, origModel);

        // compare round-tripped DeploymentDescriptor
        ComparePair.compareObjectsViaFields(origDepDesc, copyDepDesc, "mappedRoles");

        // round trip DeploymenDescriptorModel
        DeploymentDescriptorModel copyModel = marshal(copyDepDesc);

        // compare round-tripped DeploymentDescriptorModel
        new ComparePair(origModel, copyModel)
            .useFields()
            .addNullFields("ItemObjectModel.name", "DeploymentDescriptorModel.overview")
            .compare();
    }

    private static Random random = new Random();

    private static ObjectModel getObjectModelParameter( String resolver ) {
        return new ObjectModel(resolver, UUID.randomUUID().toString(), Integer.toString(random.nextInt(100000)));
    }
}
