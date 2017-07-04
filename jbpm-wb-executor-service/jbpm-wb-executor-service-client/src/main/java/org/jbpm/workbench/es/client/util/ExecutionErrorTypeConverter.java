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

package org.jbpm.workbench.es.client.util;

import org.jboss.errai.databinding.client.api.Converter;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.util.ExecutionErrorType;

public class ExecutionErrorTypeConverter implements Converter<ExecutionErrorType, String> {

    private final Constants constants = Constants.INSTANCE;

    @Override
    public ExecutionErrorType toModelValue(final String widgetValue) {
        if (widgetValue == null) {
            return null;
        } else {
            return ExecutionErrorType.fromType(widgetValue);
        }
    }

    @Override
    public String toWidgetValue(final ExecutionErrorType modelValue) {

        if (modelValue == null) {
            return "";
        } else {
            switch (modelValue) {
                case PROCESS:
                    return constants.Process();
                case TASK:
                    return constants.Task();
                case DB:
                    return constants.DB();
                case JOB:
                    return constants.Job();
            }
            return constants.Unknown();
        }
    }

    @Override
    public Class<ExecutionErrorType> getModelType() {
        return ExecutionErrorType.class;
    }

    @Override
    public Class<String> getComponentType() {
        return String.class;
    }
}