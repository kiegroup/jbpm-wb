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

import static org.junit.Assert.assertEquals;

import org.dashbuilder.json.JsonFactory;
import org.dashbuilder.json.JsonObject;
import org.junit.Test;

public class RemoteDefJSONMarshallerTest {

    
    @Test
    public void testFromJson() {
                
        RemoteDataSetDef remoteDataSetDef = new RemoteDataSetDef();
        JsonObject json = new JsonObject(new JsonFactory());
        
        json.put(RemoteDefJSONMarshaller.DATA_SOURCE, "test");
        json.put(RemoteDefJSONMarshaller.DB_SCHEMA, "testDB");
        json.put(RemoteDefJSONMarshaller.DB_SQL, "select * from test");
        json.put(RemoteDefJSONMarshaller.QUERY_TARGET, "PROCESS");
        json.put(RemoteDefJSONMarshaller.SERVER_TEMPLATE_ID, "server1");
        
        RemoteDefJSONMarshaller marhsaller = new RemoteDefJSONMarshaller();
        marhsaller.fromJson(remoteDataSetDef, json);
        
        assertEquals("test", remoteDataSetDef.getDataSource());
        assertEquals("testDB", remoteDataSetDef.getDbSchema());
        assertEquals("select * from test", remoteDataSetDef.getDbSQL());        
        assertEquals("PROCESS", remoteDataSetDef.getQueryTarget());     
        assertEquals("server1", remoteDataSetDef.getServerTemplateId());
    }
    
    @Test
    public void testToJson() {
        
        RemoteDataSetDef remoteDataSetDef = new RemoteDataSetDef();
        remoteDataSetDef.setDataSource("test");
        remoteDataSetDef.setDbSchema("testDB");
        remoteDataSetDef.setDbSQL("select * from test");
        remoteDataSetDef.setQueryTarget("PROCESS");
        remoteDataSetDef.setServerTemplateId("server1");
        JsonObject json = new JsonObject(new JsonFactory());
        
        RemoteDefJSONMarshaller marhsaller = new RemoteDefJSONMarshaller();
        marhsaller.toJson(remoteDataSetDef, json);
        
        assertEquals("test", json.getString(RemoteDefJSONMarshaller.DATA_SOURCE));
        assertEquals("testDB", json.getString(RemoteDefJSONMarshaller.DB_SCHEMA));
        assertEquals("select * from test", json.getString(RemoteDefJSONMarshaller.DB_SQL));        
        assertEquals("PROCESS", json.getString(RemoteDefJSONMarshaller.QUERY_TARGET));     
        assertEquals("server1", json.getString(RemoteDefJSONMarshaller.SERVER_TEMPLATE_ID));
    }
}
