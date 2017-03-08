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

package org.jbpm.workbench.cm.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;

@Bundle("resources/i18n/Constants.properties")
@EntryPoint
public class CaseManagementEntryPoint {

    @AfterInitialization
    public void init() {
        PatternFlyBootstrapper.ensurejQueryIsAvailable();
        ScriptInjector.fromUrl(GWT.getModuleBaseURL() + "js/bootstrap-select.min.js")
                .setWindow(ScriptInjector.TOP_WINDOW).inject();
    }

}