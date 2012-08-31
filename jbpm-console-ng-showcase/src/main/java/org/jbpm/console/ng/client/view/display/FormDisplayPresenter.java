/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.console.ng.client.view.display;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.fb.FormServiceEntryPoint;
import org.jbpm.console.ng.shared.fb.events.FormRenderedEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Form Display")
public class FormDisplayPresenter {

    @Inject
    private FormBuilderView view;
    @Inject
    private Caller<FormServiceEntryPoint> formServices;
    @Inject
    private Event<FormRenderedEvent> formRendered;

    
    
    
    public interface FormBuilderView
            extends
            UberView<FormDisplayPresenter> {
    }

    @PostConstruct
    public void init(){
        publish(this);
        publishGetFormValues();
    }
    
    
    
    public void renderForm(long taskId) {

        System.out.println("XXXXX  Calling Render Form Items" + this.hashCode());

        formServices.call(new RemoteCallback<String>() {
            @Override
            public void callback(String form) {
                System.out.println("XXXXX  RETURN load file  Items");
                formRendered.fire(new FormRenderedEvent(form));
            }
        }).getFormDisplay(taskId);

    }
    
   

    @WorkbenchPartTitle
    public String getTitle() {
        return "Form Display";
    }

    @WorkbenchPartView
    public UberView<FormDisplayPresenter> getView() {
        return view;
    }
    
    // Expose the following method into JavaScript.
    public void completeForm(String values) {
        Map<String, String> params = getUrlParameters(values);
        formServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                System.out.println("XXXXX  RETURN Complete Form");
               
            }
        }).completeForm(Long.parseLong(params.get("taskId")), params);

    }

   

    // Set up the JS-callable signature as a global JS function.
    private native void publish(FormDisplayPresenter fdp) /*-{
     
     $wnd.completeForm = function(from) {
        fdp.@org.jbpm.form.builder.ng.client.view.display.FormDisplayPresenter::completeForm(Ljava/lang/String;)(from);
     }
     
        
     }-*/;

    private native void publishGetFormValues() /*-{
     $wnd.getFormValues = function(form){
            var params = '';
            for(i=0; i<form.elements.length; i++)
            {
                var fieldName = form.elements[i].name;
                var fieldValue = form.elements[i].value;
                params += fieldName + '=' + fieldValue + '&';
            }
            return params;
            };
     }-*/;

    public static Map<String, String> getUrlParameters(String values){
        Map<String, String> params = new HashMap<String, String>();
        for (String param : values.split("&")) {
            String pair[] = param.split("=");
            String key = pair[0];
            String value = "";
            if (pair.length > 1) {
                value = pair[1];
            }
            params.put(key, value);
        }
        return params;
    }
    
   
}
