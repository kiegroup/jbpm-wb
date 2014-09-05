/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util;

import javax.enterprise.context.Dependent;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pefernan
 */
@Dependent
public class JSNIHelper {
    public native void publishGetFormValues() /*-{
        $wnd.getFormValues = function (form) {
            var params = '';

            for (i = 0; i < form.elements.length; i++) {
                var fieldName = form.elements[i].name;
                var fieldValue = form.elements[i].value;
                if (fieldName != '') {
                    params += fieldName + '=' + fieldValue + '&';
                }
            }
            return params;
        };
    }-*/;

    public Map<String, Object> getUrlParameters(String values) {
        Map<String, Object> params = new HashMap<String, Object>();
        for (String param : values.split("&")) {
            String pair[] = param.split("=");
            String key = pair[0];
            String value = "";
            if (pair.length > 1) {
                value = pair[1];
            }
            if (!key.startsWith("btn_")) {
                params.put(key, value);
            }
        }

        return params;
    }

    public void notifyErrorMessage(String opener, String message) {
        if (opener != null) notifyOpener("error", message);
    }

    public void notifySuccessMessage(String opener, String message) {
        if (opener != null) notifyOpener("success", message);
    }

    protected native void notifyOpener(String status, String message) /*-{
        var response = '{"status":"' + status + '", "message":"' + message + '"}'
        $wnd.top.postMessage(response, $wnd.location.href);
    }-*/;
}
