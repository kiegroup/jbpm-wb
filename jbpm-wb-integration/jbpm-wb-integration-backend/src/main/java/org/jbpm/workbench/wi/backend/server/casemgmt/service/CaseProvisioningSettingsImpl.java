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

package org.jbpm.workbench.wi.backend.server.casemgmt.service;

import java.io.InputStream;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Service
public class CaseProvisioningSettingsImpl implements CaseProvisioningSettings {

    public static final String SHOWCASE_DEPLOY = "org.jbpm.casemgmt.showcase.deploy";
    public static final String SHOWCASE_PATH = "org.jbpm.casemgmt.showcase.path";
    public static final String SHOWCASE_URL = "org.jbpm.casemgmt.showcase.url";
    public static final String SHOWCASE_GAV = "showcase.gav";
    public static final String WIDLFLY_MANAGEMENT_PORT = "org.jbpm.casemgmt.showcase.wildfly.management-port";
    public static final String WIDLFLY_MANAGEMENT_HOST = "org.jbpm.casemgmt.showcase.wildfly.management-host";
    public static final String WIDLFLY_USER_NAME = "org.jbpm.casemgmt.showcase.wildfly.username";
    public static final String WIDLFLY_PASSWORD = "org.jbpm.casemgmt.showcase.wildfly.password";
    public static final String CASEMGMT_PROPERTIES = "casemgmt.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseProvisioningSettingsImpl.class);
    private String showcaseGAV;

    @PostConstruct
    public void init() {
        final Properties properties = new Properties();
        try (final InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(CASEMGMT_PROPERTIES)) {
            properties.load(resource);
        } catch (Exception ex) {
            LOGGER.error("Failed to load {} file",
                         CASEMGMT_PROPERTIES,
                         ex);
        }
        showcaseGAV = properties.getProperty(SHOWCASE_GAV);
    }

    @Override
    public boolean isProvisioningEnabled() {
        return "true".equalsIgnoreCase(System.getProperty(SHOWCASE_DEPLOY));
    }

    @Override
    public boolean isDeployFromLocalPath() {
        return System.getProperty(SHOWCASE_PATH) != null;
    }

    @Override
    public String getPath() {
        return System.getProperty(SHOWCASE_PATH);
    }

    @Override
    public String getHost() {
        return System.getProperty(WIDLFLY_MANAGEMENT_HOST,
                                  "localhost");
    }

    @Override
    public String getManagementPort() {
        return System.getProperty(WIDLFLY_MANAGEMENT_PORT,
                                  "9990");
    }

    @Override
    public String getUsername() {
        return System.getProperty(WIDLFLY_USER_NAME,
                                  "admin");
    }

    @Override
    public String getPassword() {
        return System.getProperty(WIDLFLY_PASSWORD,
                                  "admin");
    }

    @Override
    public String getGAV() {
        return showcaseGAV;
    }

    @Override
    public String getURL() {
        return System.getProperty(SHOWCASE_URL);
    }
}