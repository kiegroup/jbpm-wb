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

import org.uberfire.commons.config.ConfigProperties;

public class JGitFileSystemProvider extends org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider {

    public static final String GIT_DAEMON_ENABLED = "org.uberfire.nio.git.daemon.enabled";
    public static final String GIT_SSH_ENABLED = "org.uberfire.nio.git.ssh.enabled";

    public JGitFileSystemProvider() {
        super(new DefaultConfigProperties(System.getProperties()));
    }

    public JGitFileSystemProvider(final ConfigProperties gitPrefs) {
        super(gitPrefs);
    }

    public static class DefaultConfigProperties extends ConfigProperties {

        public DefaultConfigProperties(final Properties configuredValues) {
            super(configuredValues);
        }

        @Override
        public ConfigProperty get(final String name, final String defaultValue) {
            if(GIT_DAEMON_ENABLED.equals(name) || GIT_SSH_ENABLED.equals(name)){
                return super.get(name, "false");
            }
            return super.get(name, defaultValue);
        }
    }

}
