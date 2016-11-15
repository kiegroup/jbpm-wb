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

package org.jbpm.console.ng.cm.server;

import java.util.Properties;

import org.junit.Test;

import static org.jbpm.console.ng.cm.server.JGitFileSystemProvider.*;
import static org.junit.Assert.*;

public class JGitFileSystemProviderTest {

    @Test
    public void testDefaultProperties() {
        final JGitFileSystemProvider.DefaultConfigProperties gitPrefs = new JGitFileSystemProvider.DefaultConfigProperties(new Properties());

        assertEquals(false, gitPrefs.get(GIT_DAEMON_ENABLED, DAEMON_DEFAULT_ENABLED).getBooleanValue());
        assertEquals(false, gitPrefs.get(GIT_SSH_ENABLED, SSH_DEFAULT_ENABLED).getBooleanValue());
    }

    @Test
    public void testPropertiesOverride() {
        final Properties configuredValues = new Properties();
        configuredValues.put(GIT_DAEMON_ENABLED, "true");
        configuredValues.put(GIT_SSH_ENABLED, "true");
        final JGitFileSystemProvider.DefaultConfigProperties gitPrefs = new JGitFileSystemProvider.DefaultConfigProperties(configuredValues);

        assertEquals(true, gitPrefs.get(GIT_DAEMON_ENABLED, DAEMON_DEFAULT_ENABLED).getBooleanValue());
        assertEquals(true, gitPrefs.get(GIT_SSH_ENABLED, SSH_DEFAULT_ENABLED).getBooleanValue());
    }

}
