/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.ht.forms.client.display.displayers.task;

import java.util.Map;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import org.jbpm.console.ng.gc.forms.client.display.displayers.util.PlaceManagerFormActivitySearcher;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.enterprise.event.Event;
import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.RequestFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.SetFormParamsEvent;

@Dependent
public class PlaceManagerTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer {

    @Inject
    private PlaceManagerFormActivitySearcher placeManagerFormActivitySearcher;

    @Inject
    private Event<SetFormParamsEvent> setFormParamsEvent;

    @Inject
    private Event<RequestFormParamsEvent> requestFormParamsEvent;

    public PlaceManagerTaskDisplayerImpl() {
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

            String taskStatus = params.get("taskStatus");

            placeManagerFormActivitySearcher.findFormActivityWidget(destination, formContainer);

            if ("InProgress".equals( taskStatus )) {
                setFormParamsEvent.fire(new SetFormParamsEvent(params, false));
            } else {
                setFormParamsEvent.fire(new SetFormParamsEvent(params, true));
            }
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
            return false;
        }
    }

    public void complete() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent("completeTask"));
        close();
    }

    public void saveState() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent("saveTask"));
    }

    @Override
    protected void startFromDisplayer() {
        start();
    }

    @Override
    protected void claimFromDisplayer() {
        claim();
    }

    @Override
    protected void releaseFromDisplayer() {
        release();
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    protected void completeFromDisplayer() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent("completeTask"));

    }

    protected void completeOrSaveFromEvent(@Observes GetFormParamsEvent event) {

        if (taskId == -1) return;

        if (event.getAction().equals("completeTask")) {
            complete(event.getParams());
        } else if (event.getAction().equals("saveTask")) {
            saveState(event.getParams());
        }
    }

    @Override
    protected void saveStateFromDisplayer() {
        requestFormParamsEvent.fire(new RequestFormParamsEvent("saveTask"));

    }

}
