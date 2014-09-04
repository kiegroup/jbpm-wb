package org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util;

import com.google.gwt.core.client.JavaScriptObject;

public class ActionRequest extends JavaScriptObject {

    protected ActionRequest() {
    }

    public final native String getAction() /*-{
        return this.action;
    }-*/;

    public final native String getTaskId() /*-{
        return this.taskId;
    }-*/;

    public final native String getProcessId() /*-{
        return this.processId;
    }-*/;

    public final native String getDomainId() /*-{
        return this.domainId;
    }-*/;

}
