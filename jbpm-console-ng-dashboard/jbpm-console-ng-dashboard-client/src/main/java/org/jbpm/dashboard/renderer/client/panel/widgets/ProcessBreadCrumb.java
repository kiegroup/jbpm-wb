/**
 * Copyright (C) 2015 JBoss Inc
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
package org.jbpm.dashboard.renderer.client.panel.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class ProcessBreadCrumb implements IsWidget {

    public interface View extends UberView<ProcessBreadCrumb> {

        void setRootTitle(String text);

        void setProcess(String name);
    }

    View view;
    Command onRootSelectedCommand = new Command() {public void execute() {}};

    public ProcessBreadCrumb() {
        this(new ProcessBreadCrumbView());
    }

    @Inject
    public ProcessBreadCrumb(View view) {
        this.view = view;
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setOnRootSelectedCommand(Command onRootSelectedCommand) {
        this.onRootSelectedCommand = onRootSelectedCommand;
    }

    public void gotoRoot() {
        onRootSelectedCommand.execute();
    }

    public void setRootTitle(String text) {
        view.setRootTitle(text);
    }

    public void setProcessName(String name) {
        view.setProcess(name);
    }
}
