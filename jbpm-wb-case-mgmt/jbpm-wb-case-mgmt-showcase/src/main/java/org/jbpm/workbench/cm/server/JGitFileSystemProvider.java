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
import java.util.concurrent.Executors;

import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public class JGitFileSystemProvider extends org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider {

    public static final String NIOGIT_CASEAPP = ".niogit-caseapp";

    public JGitFileSystemProvider() {
        super(new ConfigProperties(new DefaultProperties()),
              Executors.newCachedThreadPool(new DescriptiveThreadFactory()));
    }

    public JGitFileSystemProvider(final ConfigProperties gitPrefs) {
        super(gitPrefs,
              Executors.newCachedThreadPool(new DescriptiveThreadFactory()));
    }

    public static class DefaultProperties extends Properties {

        public DefaultProperties() {
            this(System.getProperties());
        }

        public DefaultProperties(final Properties defaults) {
            super(defaults);
            setProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED,
                        "false");
            setProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED,
                        "false");
            setProperty(JGitFileSystemProviderConfiguration.GIT_NIO_DIR_NAME,
                        NIOGIT_CASEAPP);
        }
    }
}
