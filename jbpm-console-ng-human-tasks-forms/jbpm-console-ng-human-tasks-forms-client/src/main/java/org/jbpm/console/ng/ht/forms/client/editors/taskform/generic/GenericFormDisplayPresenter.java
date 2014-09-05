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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.console.ng.ht.forms.api.FormRefreshCallback;
import org.jbpm.console.ng.ht.forms.ht.api.HumanTaskFormDisplayer;
import org.jbpm.console.ng.ht.forms.process.api.StartProcessFormDisplayer;
import org.jbpm.console.ng.ht.forms.service.FormServiceEntryPoint;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.*;

/**
 * @author salaboy
 */
@Dependent
public class GenericFormDisplayPresenter implements FormRefreshCallback {

    @Inject
    protected SyncBeanManager iocManager;

    @Inject
    private GenericFormDisplayView view;

    @Inject
    private Caller<FormServiceEntryPoint> formServices;

    private List<HumanTaskFormDisplayer> taskDisplayers = new ArrayList<HumanTaskFormDisplayer>();
    private List<StartProcessFormDisplayer> processDisplayers = new ArrayList<StartProcessFormDisplayer>();

    private long currentTaskId = 0;

    private String currentProcessId;

    private String currentDeploymentId;

    protected String opener;

    private Command onClose;

    public interface GenericFormDisplayView extends IsWidget {

        void displayNotification(final String text);

        void render( final FlowPanel content );

        void onReadyToRender( final Command command );
    }

    @PostConstruct
    public void init() {
        taskDisplayers.clear();
        processDisplayers.clear();

        final Collection<IOCBeanDef<HumanTaskFormDisplayer>> taskDisplayersBeans = iocManager.lookupBeans(HumanTaskFormDisplayer.class);
        if (taskDisplayersBeans != null) {
            for (final IOCBeanDef displayerDef : taskDisplayersBeans) {
                taskDisplayers.add((HumanTaskFormDisplayer) displayerDef.getInstance());
            }

        }
        final Collection<IOCBeanDef<StartProcessFormDisplayer>> processDisplayersBeans = iocManager.lookupBeans(StartProcessFormDisplayer.class);
        if (processDisplayersBeans != null) {
            for (final IOCBeanDef displayerDef : processDisplayersBeans) {
                processDisplayers.add((StartProcessFormDisplayer) displayerDef.getInstance());
            }
        }
    }

    public void setup( final long currentTaskId,
                       final String currentProcessId,
                       final String currentDeploymentId,
                       final Command onClose ) {
        this.currentTaskId = currentTaskId;
        this.currentProcessId = currentProcessId;
        this.currentDeploymentId = currentDeploymentId;
        this.onClose = onClose;

        refresh();
    }

    public void setup( final long currentTaskId,
                       final String currentProcessId,
                       final String currentDeploymentId,
                       final Command onClose,
                       final Command onReadyToRender ) {
        this.currentTaskId = currentTaskId;
        this.currentProcessId = currentProcessId;
        this.currentDeploymentId = currentDeploymentId;
        this.onClose = onClose;
        view.onReadyToRender( onReadyToRender );

        refresh();
    }

    public IsWidget getView() {
        return view;
    }

    @Override
    public void refresh() {
        if (currentTaskId != -1) {
            if (taskDisplayers != null) {
                formServices.call(new RemoteCallback<String>() {
                    @Override
                    public void callback(String form) {
                        Collections.sort(taskDisplayers, new Comparator<HumanTaskFormDisplayer>() {

                            @Override
                            public int compare(HumanTaskFormDisplayer o1, HumanTaskFormDisplayer o2) {
                                if (o1.getPriority() < o2.getPriority()) {
                                    return -1;
                                } else if (o1.getPriority() > o2.getPriority()) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        });
                        for (HumanTaskFormDisplayer d : taskDisplayers) {
                            if (d.supportsContent(form)) {
                                d.init(new TaskKey(currentTaskId), form, opener);
                                d.addFormRefreshCallback(GenericFormDisplayPresenter.this);
                                view.render(d.getContainer());
                                return;
                            }
                        }
                    }
                }).getFormDisplayTask(currentTaskId);
            }

        } else if (!currentProcessId.equals("none")) {
            if (processDisplayers != null) {
                formServices.call(new RemoteCallback<String>() {
                    @Override
                    public void callback(String form) {
                        Collections.sort(processDisplayers, new Comparator<StartProcessFormDisplayer>() {

                            @Override
                            public int compare(StartProcessFormDisplayer o1, StartProcessFormDisplayer o2) {
                                if (o1.getPriority() < o2.getPriority()) {
                                    return -1;
                                } else if (o1.getPriority() > o2.getPriority()) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        });
                        for (StartProcessFormDisplayer d : processDisplayers) {
                            if (d.supportsContent(form)) {
                                d.init(new ProcessDefinitionKey(currentDeploymentId, currentProcessId), form, opener);
                                d.addFormRefreshCallback(GenericFormDisplayPresenter.this);
                                view.render(d.getContainer());
                                return;
                            }
                        }
                    }
                }).getFormDisplayProcess(currentDeploymentId, currentProcessId);
            }

        }
    }

    @Override
    public void close() {
        onClose.execute();
    }
}
