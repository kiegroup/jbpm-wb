/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.forms.client;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.jbpm.workbench.forms.client.resources.AppResources;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;

@EntryPoint
@Bundle("i18n/FormsConstants.properties")
public class GenericFormsClientEntryPoint {

    @PostConstruct
    public void startApp() {
        AppResources.INSTANCE.style().ensureInjected();
        PatternFlyBootstrapper.ensureBootstrapSelectIsAvailable();
        loadMaskedInputScript();
    }
    
    private void loadMaskedInputScript() {
        // Load the masked input JavaScript
        ScriptElement script = Document.get().createScriptElement();
        // With <public path="org/jbpm/workbench/forms/client/resources"/> the JS is served under module base
        script.setSrc(GWT.getModuleBaseURL() + "masked-input.js");
        script.setType("text/javascript");
        Document.get().getHead().appendChild(script);
    }
}
