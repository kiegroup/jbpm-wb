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
package org.jbpm.console.ng.ht.forms.client.editors.taskform.generic;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskDisplayerConfig;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskFormDisplayProvider;
import org.jbpm.console.ng.ht.forms.display.process.api.ProcessDisplayerConfig;
import org.jbpm.console.ng.ht.forms.display.process.api.StartProcessFormDisplayProvider;
import org.jbpm.console.ng.ht.forms.display.view.FormDisplayerView;
import org.jbpm.console.ng.ht.forms.service.FormServiceEntryPoint;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.uberfire.mvp.Command;

/**
 * @author salaboy
 */
@ApplicationScoped
public class GenericFormDisplayPresenter {

    @Inject
    private GenericFormDisplayView view;

    @Inject
    private Caller<FormServiceEntryPoint> formServices;

    @Inject
    private StartProcessFormDisplayProvider processFormDisplayProvider;

    @Inject
    private HumanTaskFormDisplayProvider humanTaskFormDisplayProvider;

    public interface GenericFormDisplayView extends IsWidget {

        FormDisplayerView getDisplayerView();

        void displayNotification(final String text);
    }

    @PostConstruct
    public void init() {
    }

    public void setup( final long currentTaskId,
                       final String currentProcessId,
                       final String currentDeploymentId,
                       final String opener,
                       final Command onClose ) {

        view.getDisplayerView().setOnCloseCommand(onClose);

        if (currentTaskId != -1) {
            TaskKey key = new TaskKey(currentTaskId);
            HumanTaskDisplayerConfig config = new HumanTaskDisplayerConfig(key);
            config.setFormOpener(opener);
            humanTaskFormDisplayProvider.setup(config, view.getDisplayerView());
        } else if (!currentProcessId.equals("none")) {
            ProcessDefinitionKey key = new ProcessDefinitionKey(currentDeploymentId, currentProcessId);
            ProcessDisplayerConfig config = new ProcessDisplayerConfig(key, "");
            config.setFormOpener(opener);
            processFormDisplayProvider.setup(config, view.getDisplayerView());
        }
    }


    public IsWidget getView() {
        return view;
    }

}
