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

package org.jbpm.workbench.forms.client.display.providers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.workbench.forms.client.display.api.StartProcessFormDisplayer;
import org.jbpm.workbench.forms.display.FormRenderingSettings;
import org.jbpm.workbench.forms.service.shared.FormServiceEntryPoint;
import org.jbpm.workbench.forms.client.display.views.FormDisplayerView;
import org.jbpm.workbench.forms.client.display.views.GenericFormDisplayView;
import org.jbpm.workbench.forms.client.display.api.StartProcessFormDisplayProvider;
import org.jbpm.workbench.forms.client.i18n.Constants;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class StartProcessFormDisplayProviderImpl implements StartProcessFormDisplayProvider {

    protected Constants constants = GWT.create(Constants.class);

    @Inject
    protected SyncBeanManager iocManager;

    @Inject
    private GenericFormDisplayView view;

    @Inject
    private Caller<FormServiceEntryPoint> formServices;

    private Map<Class<? extends FormRenderingSettings>, StartProcessFormDisplayer> processDisplayers = new HashMap<>();

    @PostConstruct
    public void init() {
        processDisplayers.clear();

        final Collection<SyncBeanDef<StartProcessFormDisplayer>> processDisplayersBeans = iocManager.lookupBeans(StartProcessFormDisplayer.class);

        if (processDisplayersBeans != null) {
            for (final SyncBeanDef displayerDef : processDisplayersBeans) {
                StartProcessFormDisplayer displayer = (StartProcessFormDisplayer) displayerDef.getInstance();

                processDisplayers.put(displayer.getSupportedRenderingSettings(),
                                      displayer);
            }
        }
    }

    @Override
    public void setup(final ProcessDisplayerConfig config,
                      final FormDisplayerView view) {
        display(config,
                view);
    }

    protected void display(final ProcessDisplayerConfig config,
                           final FormDisplayerView view) {
        if (processDisplayers != null) {
            formServices.call(new RemoteCallback<FormRenderingSettings>() {
                @Override
                public void callback(FormRenderingSettings settings) {
                    if (settings == null) {
                        ErrorPopup.showMessage(constants.UnableToFindFormForProcess(config.getProcessName()));
                    } else {
                        StartProcessFormDisplayer displayer = processDisplayers.get(settings.getClass());

                        if (displayer != null) {
                            config.setRenderingSettings(settings);
                            displayer.init(config,
                                           view.getOnCloseCommand(),
                                           new Command() {
                                               @Override
                                               public void execute() {
                                                   display(config,
                                                           view);
                                               }
                                           });
                            view.display(displayer);
                        }
                    }
                }
            }).getFormDisplayProcess(config.getKey().getServerTemplateId(),
                                     config.getKey().getDeploymentId(),
                                     config.getKey().getProcessId());
        }
    }

    public IsWidget getView() {
        return view;
    }
}
