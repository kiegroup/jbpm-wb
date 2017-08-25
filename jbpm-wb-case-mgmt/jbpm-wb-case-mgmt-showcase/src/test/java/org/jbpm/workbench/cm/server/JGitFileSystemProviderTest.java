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

package org.jbpm.workbench.cm.server;

import java.util.Properties;

import org.junit.Test;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

import static org.jbpm.workbench.cm.server.JGitFileSystemProvider.*;
import static org.junit.Assert.*;

public class JGitFileSystemProviderTest {

    @Test
    public void testDefaultProperties() {
        final ConfigProperties gitPrefs = new ConfigProperties(new JGitFileSystemProvider.DefaultProperties());

        assertEquals(false,
                     gitPrefs.get(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED,
                                  null).getBooleanValue());
        assertEquals(false,
                     gitPrefs.get(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED,
                                  null).getBooleanValue());
        assertEquals(NIOGIT_CASEAPP,
                     gitPrefs.get(JGitFileSystemProviderConfiguration.GIT_NIO_DIR_NAME,
                                  null).getValue());
    }

    @Test
    public void testPropertiesOverride() {
        final Properties configuredValues = new Properties();
        configuredValues.put(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED,
                             "true");
        configuredValues.put(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED,
                             "true");
        configuredValues.put(JGitFileSystemProviderConfiguration.GIT_NIO_DIR_NAME,
                             ".niogit");
        final ConfigProperties gitPrefs = new ConfigProperties(new JGitFileSystemProvider.DefaultProperties(configuredValues));

        assertEquals(false,
                     gitPrefs.get(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED,
                                  null).getBooleanValue());
        assertEquals(false,
                     gitPrefs.get(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED,
                                  null).getBooleanValue());
        assertEquals(NIOGIT_CASEAPP,
                     gitPrefs.get(JGitFileSystemProviderConfiguration.GIT_NIO_DIR_NAME,
                                  null).getValue());
    }
}
