/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.backend.server;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetGenerator;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.workbench.pr.model.ProcessDefinitionDataSetProviderType;
import org.uberfire.commons.services.cdi.Startup;

import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.*;

@Startup
@ApplicationScoped
public class ProcessDefinitionDataSetGenerator implements DataSetGenerator {

    @Inject
    protected DataSetDefRegistry dataSetDefRegistry;

    protected DataSetDef dataSetdef = ProcessDefinitionDataSetDefBuilder.get()
            .uuid(PROCESS_DEFINITION_DATASET)
            .generatorClass(ProcessDefinitionDataSetGenerator.class.getName())
            .name(PROCESS_DEFINITION_DATASET_NAME)
            .label(COL_ID_PROCESSNAME)
            .label(COL_ID_PROCESSVERSION)
            .label(COL_ID_PROJECT)
            .label(COL_ID_PROCESSDEF)
            .label(COL_DYNAMIC)
            .buildDef();

    @PostConstruct
    protected void init() {
        dataSetdef.setProvider(new ProcessDefinitionDataSetProviderType());
        dataSetdef.setPublic(false);
        dataSetDefRegistry.registerDataSetDef(dataSetdef);
    }

    @Override
    public DataSet buildDataSet(Map<String, String> params) {
        return null;
    }

    public DataSetDef getDataSetDef() {
        return dataSetdef;
    }
}
