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

package org.jbpm.workbench.ks.integration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.event.Event;

import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.workbench.ks.events.KieServerDataSetRegistered;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.common.rest.KieServerHttpRequestException;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesException;
import org.kie.server.client.QueryServicesClient;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KieServerDataSetManagerTest {


    private DataSetDefRegistry dataSetDefRegistry;

    private KieServerIntegration kieServerIntegration;

    private Event<KieServerDataSetRegistered> event;

    private final List<Object> receivedEvents = new ArrayList<Object>();
    private KieServicesClient kieClient;
    private QueryServicesClient queryClient;

    private KieServerDataSetManager kieServerDataSetManager;

    @Before
    public void setup() {
        this.dataSetDefRegistry = Mockito.mock(DataSetDefRegistry.class);
        this.kieServerIntegration = Mockito.mock(KieServerIntegration.class);
        this.event = new EventSourceMock<KieServerDataSetRegistered>() {

            @Override
            public void fire( KieServerDataSetRegistered event ) {
                receivedEvents.add(event);
            }

        };

        this.kieClient = Mockito.mock(KieServicesClient.class);
        this.queryClient = Mockito.mock(QueryServicesClient.class);
        when(kieClient.getServicesClient(any())).thenReturn(queryClient);

        when(kieServerIntegration.getAdminServerClient(anyString())).thenReturn(kieClient);

        this.kieServerDataSetManager = new KieServerDataSetManager(dataSetDefRegistry, kieServerIntegration, event);
    }

    @Test
    public void testRegisterQueriesWithoutRetry() throws Exception {


        QueryDefinition query = QueryDefinition.builder().name("test").expression("expression").source("jbpm").target("CUSTOM").build();
        Set<QueryDefinition> definitions = new HashSet<>();
        definitions.add(query);

        kieServerDataSetManager.registerQueriesWithRetry("template", "instance", definitions);

        verify(kieServerIntegration, times(1)).getAdminServerClient(anyString());
        verify(queryClient, times(1)).replaceQuery(any());

        assertEquals(1, receivedEvents.size());
    }

    @Test
    public void testRegisterQueriesWithRetryDueToKieServicesException() throws Exception {
        registerQueriesWithRetryException(new KieServicesException("KieServer still starting"));
    }

    @Test
    public void testRegisterQueriesWithRetryDueToKieRemoteHttpRequestException() throws Exception {
        registerQueriesWithRetryException(new KieServerHttpRequestException("KieServer endpoint down"));
    }

    private void registerQueriesWithRetryException(Exception exception) throws Exception {
        QueryDefinition query = QueryDefinition.builder().name("test").expression("expression").source("jbpm").target("CUSTOM").build();
        Set<QueryDefinition> definitions = new HashSet<>();
        definitions.add(query);

        KieServicesClient kieClientRecreated = Mockito.mock(KieServicesClient.class);
        QueryServicesClient queryClientRecreated = Mockito.mock(QueryServicesClient.class);
        when(kieClientRecreated.getServicesClient(any())).thenReturn(queryClientRecreated);

        when(kieServerIntegration.getAdminServerClientCheckEndpoints(anyString())).thenReturn(kieClientRecreated);

        doThrow(exception).when(queryClient).replaceQuery(any());

        kieServerDataSetManager.registerQueriesWithRetry("template", "instance", definitions);

        verify(kieServerIntegration, times(1)).getAdminServerClient(anyString());
        verify(kieServerIntegration, times(1)).getAdminServerClientCheckEndpoints(anyString());
        verify(queryClient, times(1)).replaceQuery(any());
        verify(queryClientRecreated, times(1)).replaceQuery(any());

        assertEquals(1, receivedEvents.size());
    }
}
