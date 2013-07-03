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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.jbpm.console.ng.udc.model.UsageEventSummary;

import com.google.gwt.i18n.client.DateTimeFormat;

public class UtilUsageData {

    public static final String patternDateTime = "yyyy-MM-dd HH:mm:ss";
    public static final String patternNameFile = "yyyy-MM-dd-HH:mm:ss";

    public static String getDateTime(Date date, String pattern) {
        DateTimeFormat fmt = DateTimeFormat.getFormat(patternDateTime);
        return fmt.format(date);
    }

    public static void exportEventsToTxt(String userName, List<UsageEventSummary> allEventsSummaries) throws IOException {
        //TODO 
    }

}
