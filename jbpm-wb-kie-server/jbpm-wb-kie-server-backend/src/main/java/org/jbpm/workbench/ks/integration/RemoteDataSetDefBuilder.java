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

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.impl.SQLDataSetDefBuilderImpl;


public class RemoteDataSetDefBuilder extends SQLDataSetDefBuilderImpl {

    public static RemoteDataSetDefBuilder get() {
        return new RemoteDataSetDefBuilder();
    }
    
    @Override
    protected DataSetDef createDataSetDef() {
        return new RemoteDataSetDef();
    }
    
    public RemoteDataSetDefBuilder queryTarget(String queryTarget) {
        ((RemoteDataSetDef) def).setQueryTarget(queryTarget);
        return this;
    }
    
    public RemoteDataSetDefBuilder serverTemplateId(String serverTemplateId) {
        ((RemoteDataSetDef) def).setServerTemplateId(serverTemplateId);
        return this;
    }

    @Override
    public RemoteDataSetDefBuilder dataSource(String dataSource) {
        super.dataSource(dataSource);
        return this;
    }

    @Override
    public RemoteDataSetDefBuilder dbSchema(String dbSchema) {        
        super.dbSchema(dbSchema);
        return this;
    }

    @Override
    public RemoteDataSetDefBuilder dbTable(String dbTable, boolean allColumns) {
        super.dbTable(dbTable, allColumns);
        return this;
    }

    @Override
    public RemoteDataSetDefBuilder dbSQL(String dbSQL, boolean allColumns) {
        super.dbSQL(dbSQL, allColumns);
        return this;
    }

    @Override
    public RemoteDataSetDefBuilder uuid(String uuid) {
        super.uuid(uuid);
        return this;
    }

    @Override
    public RemoteDataSetDefBuilder name(String name) {
        super.name(name);
        return this;
    }

}
