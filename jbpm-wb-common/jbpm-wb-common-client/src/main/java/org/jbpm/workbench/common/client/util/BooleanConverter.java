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

package org.jbpm.workbench.common.client.util;

import org.jboss.errai.databinding.client.api.Converter;
import org.jbpm.workbench.common.client.resources.i18n.Constants;

public class BooleanConverter implements Converter<Boolean, String> {

    private final Constants constants = Constants.INSTANCE;

    @Override
    public Boolean toModelValue(final String widgetValue) {
        if (widgetValue == null) {
            return null;
        } else {
            return constants.Yes().equals(widgetValue);
        }
    }

    @Override
    public String toWidgetValue(final Boolean modelValue) {
        if (modelValue != null) {
            if (modelValue) {
                return constants.Yes();
            } else {
                return constants.No();
            }
        }
        return " ";
    }

    @Override
    public Class<Boolean> getModelType() {
        return Boolean.class;
    }

    @Override
    public Class<String> getComponentType() {
        return String.class;
    }
}