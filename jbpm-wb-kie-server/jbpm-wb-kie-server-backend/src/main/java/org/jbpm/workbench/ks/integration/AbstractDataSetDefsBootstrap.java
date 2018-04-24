/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ks.integration;

import java.util.function.Consumer;
import javax.inject.Inject;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.kie.server.api.model.definition.QueryDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDataSetDefsBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataSetDefsBootstrap.class);

    @Inject
    DataSetDefRegistry dataSetDefRegistry;

    protected void registerDataSetDefinition(final QueryDefinition queryDefinition,
                                             final Consumer<RemoteDataSetDefBuilder> consumer) {
        RemoteDataSetDefBuilder builder = RemoteDataSetDefBuilder.get()
                .uuid(queryDefinition.getName())
                .name(queryDefinition.getTarget() + "-" + queryDefinition.getName())
                .queryTarget(queryDefinition.getTarget())
                .dataSource(queryDefinition.getSource())
                .dbSQL(queryDefinition.getExpression(),
                       false);

        consumer.accept(builder);

        DataSetDef dataSetDef = builder.buildDef();

        dataSetDef.setPublic(false);
        dataSetDef.setProvider(KieServerDataSetProvider.TYPE);

        dataSetDefRegistry.registerDataSetDef(dataSetDef);
        LOGGER.info("Data Set registered {}",
                    dataSetDef);
    }
}
