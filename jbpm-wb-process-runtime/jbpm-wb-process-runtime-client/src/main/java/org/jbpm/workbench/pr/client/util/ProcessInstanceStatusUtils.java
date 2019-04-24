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

package org.jbpm.workbench.pr.client.util;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.kie.api.runtime.process.ProcessInstance;

public class ProcessInstanceStatusUtils {

    private static final Constants constants = Constants.INSTANCE;

    public static Map<String, String> getStatesStrMapping() {
        Map<String, String> statesStrMapping = new HashMap<>();
        statesStrMapping.put(String.valueOf(ProcessInstance.STATE_ACTIVE), constants.Active());
        statesStrMapping.put(String.valueOf(ProcessInstance.STATE_ABORTED), constants.Aborted());
        statesStrMapping.put(String.valueOf(ProcessInstance.STATE_COMPLETED), constants.Completed());
        statesStrMapping.put(String.valueOf(ProcessInstance.STATE_PENDING), constants.Pending());
        statesStrMapping.put(String.valueOf(ProcessInstance.STATE_SUSPENDED), constants.Suspended());
        return statesStrMapping;
    }

    public static String toWidgetValue(final Integer status) {
        if (status == null) {
            return constants.Unknown();
        } else {
            switch (status) {
                case ProcessInstance.STATE_ACTIVE:
                    return constants.Active();
                case ProcessInstance.STATE_ABORTED:
                    return constants.Aborted();
                case ProcessInstance.STATE_COMPLETED:
                    return constants.Completed();
                case ProcessInstance.STATE_PENDING:
                    return constants.Pending();
                case ProcessInstance.STATE_SUSPENDED:
                    return constants.Suspended();
                default:
                    return constants.Unknown();
            }
        }
    }
}