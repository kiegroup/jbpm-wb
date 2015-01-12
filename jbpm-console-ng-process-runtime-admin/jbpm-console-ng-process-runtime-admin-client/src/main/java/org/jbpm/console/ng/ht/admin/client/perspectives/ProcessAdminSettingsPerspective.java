/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.ht.admin.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.util.Layouts;

/**
 * A Perspective
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "Process Admin")
public class ProcessAdminSettingsPerspective extends FlowPanel {

    @Inject
    @WorkbenchPanel(parts = "Process Admin Settings")
    FlowPanel tasksAdminSettings;

    @PostConstruct
    private void init() {
        Layouts.setToFillParent( tasksAdminSettings );
        add( tasksAdminSettings );
    }
}
