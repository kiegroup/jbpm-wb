/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.cm;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class DeploymentIT {

    public static final String ARCHIVE_NAME = "jbpm-wb-case-mgmt-showcase.war";
    public static final String URL = "http://127.0.0.1:8080/jbpm-cm";

    @Deployment(testable = false)
    public static WebArchive create() {
        return ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME)
                .importFrom(new File("target/" + ARCHIVE_NAME))
                .as(WebArchive.class);
    }

    @Test
    @RunAsClient
    public void testDeployment() throws Exception {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) new URL(URL).openConnection();
            assertEquals(200, c.getResponseCode());
        } finally {
            if (c != null) {
                c.disconnect();
            }
        }
    }

}