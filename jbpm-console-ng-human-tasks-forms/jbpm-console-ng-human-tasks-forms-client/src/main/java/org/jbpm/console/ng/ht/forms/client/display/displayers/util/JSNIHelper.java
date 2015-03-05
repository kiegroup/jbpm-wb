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
package org.jbpm.console.ng.ht.forms.client.display.displayers.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import javax.enterprise.context.Dependent;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pefernan
 */
@Dependent
public class JSNIHelper {
    public static final String FORM_VALIDATOR_FUNCTION = "taskFormValidator()";

    public native void publishGetFormValues() /*-{
        $wnd.getFormValues = function (form) {
            var result = new Object()
            for (i = 0; i < form.elements.length; i++) {
                var fieldName = form.elements[i].name;
                var fieldValue = form.elements[i].value;
                if (fieldName != '') {
                    result[fieldName] = fieldValue;
                }
            }
            return result;
        };
    }-*/;

    public Map<String, Object> getParameters(JavaScriptObject values) {
        JSONObject jsonObject = new JSONObject(values);
        Map<String, Object> params = new HashMap<String, Object>();
        for (String key : jsonObject.keySet()) {
            if (!key.startsWith("btn_")) {
                params.put(key, jsonObject.get(key).isString().stringValue());
            }
        }
        return params;
    }

    public Map<String, String> parseParams(JSONObject jsonParams) {
        Map<String, String> params = new HashMap<String, String>(  );

        for (String key : jsonParams.keySet()) {
            JSONValue value = jsonParams.get( key );
            if (value != null) {
                if (value.isString() != null) params.put( key, value.isString().stringValue() );
                else params.put( key, value.toString() );
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

    protected void notifyOpener(String status, String message) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("status", new JSONString(status));
        jsonMessage.put("message", new JSONString(message));
        notifyOpener(jsonMessage.toString());
    }

    protected native void notifyOpener(String message) /*-{
        $wnd.top.postMessage(message, $wnd.location.href);
    }-*/;

    public void injectFormValidationsScripts(String html) {
        String formScripts = "";

        while (startOfScript(html) != -1) {
            int begin = startOfScript(html);
            int end = endOfScript(html);

            String fullScript = html.substring(begin, end);
            String script = fullScript.substring(fullScript.indexOf(">") + 1, fullScript.lastIndexOf("</"));

            formScripts += script;
            html = html.replace(fullScript, "");
        }

        if (formScripts == null || formScripts.length() == 0) {
            formScripts = "function taskFormValidator() {return true;}";
        }

        ScriptInjector.fromString(formScripts).setWindow(ScriptInjector.TOP_WINDOW).inject();
    }

    protected int startOfScript(String html) {
        return html.toLowerCase().indexOf("<script");
    }

    protected int endOfScript(String html) {
        int start = html.toLowerCase().indexOf("</script");

        int end = html.substring(start).indexOf(">") + 1;

        return  start + end;
    }
}
