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

package org.jbpm.console.ng.he.client.info;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.he.client.i8n.Constants;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;

import com.google.gwt.core.client.GWT;

@Dependent
@WorkbenchPopup(identifier = "Info Human Events")
public class InfoHumanEventPresenter {

    private Constants constants = GWT.create(Constants.class);

    public interface InfoHumanEventView extends UberView<InfoHumanEventPresenter> {

        void displayNotification(String text);

    }

    @Inject
    InfoHumanEventView view;

    @Inject
    Identity identity;

}
