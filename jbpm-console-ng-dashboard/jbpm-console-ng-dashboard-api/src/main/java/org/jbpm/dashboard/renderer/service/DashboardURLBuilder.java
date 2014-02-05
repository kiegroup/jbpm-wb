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
package org.jbpm.dashboard.renderer.service;

/**
 * This class builds the URL to navigate to a dashbuilder application.
 * Basically, adds the locale support,
 * so the locale of the gwt application and the locale for the dashbuilder app will be the same.
 */
public class DashboardURLBuilder {

    final static private String GWT_DEFAULT_LOCALE  = "default";
    final static private String DASHBUILDER_DEFAULT_LOCALE  = "en";
    final static private String SLASH  = "/";

    /**
     * <p>Returns the url for jBPM dashboard using the current locale.</p>
     * <p>As GWT does not emulate JRE String#format (https://code.google.com/p/google-web-toolkit/issues/detail?id=3945),
     * the URL is splitted in:a preffix and a suffix.</p>
     * @param preffix The URL value before the language parameter
     * @param suffix The URL value after the language parameter
     * @param localeName The GWT locale name.
     * @return The url for the jBPM dashboard.
     */
    public static String getDashboardURL(String preffix, String suffix, String localeName) {
        if (preffix == null && suffix == null) return null;

        StringBuilder result = new StringBuilder();

        if (preffix != null) result.append(preffix);

        if (localeName != null) {
            if (!preffix.endsWith(SLASH)) result.append(SLASH);
            if (GWT_DEFAULT_LOCALE.equals(localeName)) localeName = DASHBUILDER_DEFAULT_LOCALE;
            result. append(localeName);
        }

        if (suffix != null) {
            if (!result.toString().endsWith(SLASH)) result.append(SLASH);
            result.append(suffix);
        }

        return result.toString();
    }

}
