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

package org.jbpm.workbench.wi.dd.validation;

import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class DeploymentDescriptorValidationMessage extends ValidationMessage {

    private String key;

    private Object[] args;

    public DeploymentDescriptorValidationMessage() {
        super();
    }

    public DeploymentDescriptorValidationMessage(long id, Level level, Path path, int line, int column, String text, String key, Object... args) {
        super(id, level, path, line, column, text);
        this.key = key;
        this.args = args;
    }

    public DeploymentDescriptorValidationMessage(long id, Level level, int line, int column, String text, String key, Object... args) {
        super(id, level, line, column, text);
        this.key = key;
        this.args = args;
    }

    public DeploymentDescriptorValidationMessage(Level level, String text, String key, Object... args) {
        super(level, text);
        this.key = key;
        this.args = args;
    }

    public DeploymentDescriptorValidationMessage(ValidationMessage other, String key, Object... args) {
        super(other);
        this.key = key;
        this.args = args;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
