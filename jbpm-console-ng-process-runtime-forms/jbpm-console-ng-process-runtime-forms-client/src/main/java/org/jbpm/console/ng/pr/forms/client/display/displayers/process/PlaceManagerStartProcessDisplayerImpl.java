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

package org.jbpm.console.ng.pr.forms.client.display.displayers.process;

import java.util.Map;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.enterprise.event.Event;
import org.jbpm.console.ng.gc.forms.client.display.displayers.util.PlaceManagerFormActivitySearcher;
import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.RequestFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.SetFormParamsEvent;

/**
 *
 * @author salaboy
 */
@Dependent
public class PlaceManagerStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer {

    @Inject
    private PlaceManagerFormActivitySearcher placeManagerFormActivitySearcher;
    
    @Inject
    private Event<SetFormParamsEvent> setFormParamsEvent;
    
    @Inject
    private Event<RequestFormParamsEvent> requestFormParamsEvent;

    public PlaceManagerStartProcessDisplayerImpl() {

    }
    

    @Override
    protected void initDisplayer() {
        JSONValue jsonValue = JSONParser.parseStrict( formContent );

        JSONObject jsonObject = jsonValue.isObject();

        if (jsonObject != null) {
            formContainer.setWidth("100%");
            formContainer.setHeight("400px");

            JSONValue jsonDestination = jsonObject.get( "destination" );

            if (jsonDestination == null) return;

            String destination = jsonDestination.isString().stringValue();

            JSONObject jsonParams = jsonObject.get( "params" ).isObject();

            if (jsonParams == null) return;

            Map<String, String> params = jsniHelper.parseParams( jsonParams );

            placeManagerFormActivitySearcher.findFormActivityWidget(destination, formContainer);
            setFormParamsEvent.fire(new SetFormParamsEvent(params, false));
        }
    }

    @Override
    public boolean supportsContent(String content) {
        try {
            JSONValue jsonValue = JSONParser.parseStrict( content );

            JSONObject jsonObject;

            if ((jsonObject = jsonValue.isObject()) == null) return false;

            jsonValue = jsonObject.get( "handler" );

            if (jsonValue.isString() == null) return false;

            return jsonValue.isString().stringValue().equals( "handledByPlaceManagerFormProvider" );
        } catch ( Exception e ) {
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public void close() {
        super.close();
    }

    public void startProcess() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent("startProcess"));
    }
    
    public void startProcessCallback(@Observes GetFormParamsEvent event){

        if (processDefId == null || deploymentId == null) return;

        if(event.getAction().equals("startProcess")){
            sessionServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
                    .startProcess(deploymentId, processDefId, event.getParams());
        }
    }

    @Override
    public void startProcessFromDisplayer() {
        startProcess();
    }

}
