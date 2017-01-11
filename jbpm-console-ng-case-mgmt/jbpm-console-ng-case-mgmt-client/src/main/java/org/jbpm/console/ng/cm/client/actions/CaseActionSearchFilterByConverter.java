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

package org.jbpm.console.ng.cm.client.actions;

import org.jboss.errai.databinding.client.api.Converter;
import org.jbpm.console.ng.cm.util.CaseActionsFilterBy;


public class CaseActionSearchFilterByConverter implements Converter<CaseActionsFilterBy, String> {

    @Override
    public Class<CaseActionsFilterBy> getModelType() {
        return CaseActionsFilterBy.class;
    }

    @Override
    public Class<String> getComponentType() {
        return String.class;
    }

    @Override
    public CaseActionsFilterBy toModelValue(final String componentValue) {
        return CaseActionsFilterBy.valueOf(componentValue);
    }

    @Override
    public String toWidgetValue(final CaseActionsFilterBy modelValue) {
        return modelValue == null ? null : modelValue.name();
    }
}