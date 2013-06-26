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

package org.jbpm.console.ng.he.client.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import org.jbpm.console.ng.he.model.HumanEventSummary;
import org.kie.commons.java.nio.IOException;

import com.google.gwt.i18n.client.DateTimeFormat;

public class UtilEvent {

    public static final String patternDateTime = "yyyy-MM-dd HH:mm:ss";
    public static final String patternNameFile = "yyyy-MM-dd-HH:mm:ss";

    public static String getDateTime(Date date, String pattern) {
        DateTimeFormat fmt = DateTimeFormat.getFormat(patternDateTime);
        return fmt.format(date);
    }

    public static void exportToTxt(String userName) {
        /*File f;
        f = new File("/tmp/archivo_" + getDateTime(new Date(), patternNameFile) + ".txt");
        try {
            FileWriter w = new FileWriter(f);
            BufferedWriter bw = new

            BufferedWriter(w);
            PrintWriter wr = new PrintWriter(bw);

            wr.write("Human Events user:" + userName);
            for

            (HumanEventSummary human : allEventsSummaries) {

                wr.append(human.getDescriptionEvent() + " - " +

                human.getTypeEvent());
                wr.append("/n");
            }
            wr.close();
            bw.close();
        }

        catch (IOException e) {

        }*/

    }

}
