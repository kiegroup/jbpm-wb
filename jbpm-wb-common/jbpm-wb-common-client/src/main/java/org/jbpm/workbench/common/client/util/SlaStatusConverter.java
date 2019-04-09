/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.databinding.client.api.Converter;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.kie.api.runtime.process.ProcessInstance;

public class SlaStatusConverter implements Converter<Integer, String> {

    private final Constants constants = Constants.INSTANCE;

    @Override
    public Integer toModelValue(final String widgetValue) {
        if (widgetValue == null || widgetValue.equals("")) {
            return null;
        }

        return Integer.valueOf(widgetValue);
    }

    @Override
    public String toWidgetValue(final Integer status) {
        if (status == null) {
            return constants.Unknown();
        }

        switch (status) {
            case ProcessInstance.SLA_NA:
                return constants.SlaNA();
            case ProcessInstance.SLA_PENDING:
                return constants.SlaPending();
            case ProcessInstance.SLA_MET:
                return constants.SlaMet();
            case ProcessInstance.SLA_ABORTED:
                return constants.SlaAborted();
            case ProcessInstance.SLA_VIOLATED:
                return constants.SlaViolated();
            default:
                return constants.Unknown();
        }
    }

    @Override
    public Class<Integer> getModelType() {
        return Integer.class;
    }

    @Override
    public Class<String> getComponentType() {
        return String.class;
    }

    public static Map<String, String> getSLAComplianceAliasMap() {
        Map<String, String> aliasMap = new HashMap();
        aliasMap.put(String.valueOf(ProcessInstance.SLA_ABORTED), Constants.INSTANCE.SlaAborted());
        aliasMap.put(String.valueOf(ProcessInstance.SLA_MET), Constants.INSTANCE.SlaMet());
        aliasMap.put(String.valueOf(ProcessInstance.SLA_NA), Constants.INSTANCE.SlaNA());
        aliasMap.put(String.valueOf(ProcessInstance.SLA_PENDING), Constants.INSTANCE.SlaPending());
        aliasMap.put(String.valueOf(ProcessInstance.SLA_VIOLATED), Constants.INSTANCE.SlaViolated());
        return aliasMap;
    }
}