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

package org.jbpm.workbench.pr.client.util;

import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.ht.util.TaskStatus;

public class TaskStatusConverter implements Converter<TaskStatus, String> {

    @Override
    public TaskStatus toModelValue(final String widgetValue) {
        if (widgetValue == null) {
            return null;
        } else {
            return TaskStatus.fromStatus(widgetValue);
        }
    }

    @Override
    public String toWidgetValue(final TaskStatus modelValue) {
        if (modelValue == null) {
            return "";
        } else {
            return getTranslationService().format(modelValue.getIdentifier());
        }
    }

    @Override
    public Class<TaskStatus> getModelType() {
        return TaskStatus.class;
    }

    @Override
    public Class<String> getComponentType() {
        return String.class;
    }

    private TranslationService getTranslationService() {
        return IOC.getBeanManager().lookupBean(TranslationService.class).getInstance();
    }
}