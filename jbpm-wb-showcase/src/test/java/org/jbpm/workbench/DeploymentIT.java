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

package org.jbpm.workbench;

import java.net.HttpURLConnection;
import java.net.URL;

import org.awaitility.Duration;
import org.junit.Test;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;

import static org.awaitility.Awaitility.await;
import static org.jbpm.workbench.ks.utils.KieServerUtils.createKieServicesClient;
import static org.junit.Assert.*;

public class DeploymentIT {

    public static final int TIMEOUT = 60000;

    @Test(timeout = TIMEOUT)
    public void testShowcaseDeployment() throws Exception {
        URL baseURL = new URL("http://localhost:8080/jbpm-wb-showcase/");
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) baseURL.openConnection();
            assertEquals(200,
                         c.getResponseCode());
        } finally {
            if (c != null) {
                c.disconnect();
            }
        }
    }

    @Test(timeout = TIMEOUT)
    public void testCaseManagementDeployment() throws Exception {
        final String context = System.getProperty("case-mgmt-showcase.context");
        assertNotNull(context);
        final URL cm = new URL("http://localhost:8080/" + context);
        await().atMost(Duration.ONE_MINUTE).untilAsserted(() -> {
            HttpURLConnection c = null;
            try {
                c = (HttpURLConnection) cm.openConnection();
                assertEquals(200, c.getResponseCode());
            } finally {
                if (c != null) {
                    c.disconnect();
                }
            }
        });
    }

    @Test(timeout = TIMEOUT)
    public void testCustomQueriesDeployedToKieServer() throws Exception {
        URL baseURL = new URL("http://localhost:8080/kie-server/");
        final String url = new URL(baseURL,
                                   "services/rest/server").toString();

        KieServicesClient client = createKieServicesClient(url,
                                                           null,
                                                           "kieserver",
                                                           "kieserver1!",
                                                           null);
        QueryServicesClient queryServicesClient = client.getServicesClient(QueryServicesClient.class);

        assertQueryDefinition(queryServicesClient,
                              "jbpmProcessInstances");
        assertQueryDefinition(queryServicesClient,
                              "jbpmProcessInstancesWithVariables");
        assertQueryDefinition(queryServicesClient,
                              "processesMonitoring");
        assertQueryDefinition(queryServicesClient,
                              "tasksMonitoring");
        assertQueryDefinition(queryServicesClient,
                              "jbpmRequestList");
        assertQueryDefinition(queryServicesClient,
                              "jbpmExecutionErrorList");
        assertQueryDefinition(queryServicesClient,
                              "jbpmHumanTasks");
        assertQueryDefinition(queryServicesClient,
                              "jbpmHumanTasksWithUser");
        assertQueryDefinition(queryServicesClient,
                              "jbpmHumanTasksWithAdmin");
        assertQueryDefinition(queryServicesClient,
                              "jbpmHumanTasksWithVariables");
    }

    private void assertQueryDefinition(final QueryServicesClient client,
                                       final String name) {
        QueryDefinition qd = client.getQuery(name);
        assertNotNull(qd);
    }

}