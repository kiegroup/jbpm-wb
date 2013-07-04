/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.udc.client.util;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jbpm.console.ng.udc.client.event.ActionsUsageData;
import org.jbpm.console.ng.udc.model.UsageEventSummary;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.i18n.client.DateTimeFormat;

public class UtilUsageData {

    public static final String patternDateTime = "yyyy-MM-dd HH:mm:ss";
    public static final String HEADER_TITLE_CSV = "Timestamp, Module, User, Component, Action, key, Level, Status";

    public static String getDateTime(Date date, String pattern) {
        DateTimeFormat fmt = DateTimeFormat.getFormat(patternDateTime);
        return fmt.format(date);
    }

    public static String getComponentFormated(Set<String> setInfo){
        StringBuilder componentFormated = new StringBuilder();
        for(String component : setInfo){
            componentFormated.append(component);
            componentFormated.append(" ");
        }
        return componentFormated.toString();
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Set<String>> getAllComponentByModule() {
        Map<String, Set<String>> auditions = Maps.newHashMap();
        for (ActionsUsageData actionHuman : ActionsUsageData.values()) {
            Set<String> setComponent = (Set<String>) (auditions.get(actionHuman.getModule()) == null ? Sets
                    .newHashSetWithExpectedSize(ActionsUsageData.values().length) : auditions.get(actionHuman.getModule()));
            setComponent.add(actionHuman.getComponent());
            auditions.put(actionHuman.getModule(), setComponent);
        }
        return auditions;
    }
    
    public static String getRowFormatted(UsageEventSummary usage){
        StringBuilder rowFormatted = new StringBuilder();
        rowFormatted.append("\n");
        rowFormatted.append(usage.getTimestamp() + ",");
        rowFormatted.append(usage.getModule() + ",");
        rowFormatted.append(usage.getUser() + ",");
        rowFormatted.append(usage.getUser() + ",");
        rowFormatted.append(usage.getComponent() + ",");
        rowFormatted.append(usage.getAction() + ",");
        rowFormatted.append(usage.getKey() + ",");
        rowFormatted.append(usage.getLevel() + ",");
        rowFormatted.append(usage.getStatus());
        return rowFormatted.toString();
    }
    
}
