/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.ht.forms.client.display.providers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.console.ng.ga.forms.display.FormRenderingSettings;
import org.jbpm.console.ng.ga.forms.service.shared.FormServiceEntryPoint;
import org.jbpm.console.ng.gc.forms.client.display.views.FormDisplayerView;
import org.jbpm.console.ng.ht.forms.client.display.ht.api.HumanTaskFormDisplayProvider;
import org.jbpm.console.ng.ht.forms.client.display.ht.api.HumanTaskFormDisplayer;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskDisplayerConfig;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class HumanTaskFormDisplayProviderImpl implements HumanTaskFormDisplayProvider {

    @Inject
    private Caller<FormServiceEntryPoint> formServices;

    @Inject
    protected SyncBeanManager iocManager;

    private Map<Class<? extends FormRenderingSettings>, HumanTaskFormDisplayer> taskDisplayers = new HashMap<>();


    @PostConstruct
    public void init() {
        taskDisplayers.clear();

        final Collection<SyncBeanDef<HumanTaskFormDisplayer>> taskDisplayersBeans = iocManager.lookupBeans(
                HumanTaskFormDisplayer.class );
        if ( taskDisplayersBeans != null ) {
            for ( final SyncBeanDef displayerDef : taskDisplayersBeans ) {

                HumanTaskFormDisplayer displayer = (HumanTaskFormDisplayer) displayerDef.getInstance();

                taskDisplayers.put( displayer.getSupportedRenderingSettings(), displayer );
            }
        }
    }

    @Override
    public void setup( final HumanTaskDisplayerConfig config, final FormDisplayerView view ) {
        display( config, view );
    }

    protected void display( final HumanTaskDisplayerConfig config, final FormDisplayerView view ) {
        if ( taskDisplayers != null ) {
            formServices.call( new RemoteCallback<FormRenderingSettings>() {
                @Override
                public void callback( FormRenderingSettings settings ) {

                    HumanTaskFormDisplayer displayer = taskDisplayers.get( settings.getClass() );

                    if ( displayer != null ) {
                        config.setRenderingSettings( settings );
                        displayer.init( config, view.getOnCloseCommand(), new Command() {
                            @Override
                            public void execute() {
                                display( config, view );
                            }
                        }, view.getResizeListener() );
                        view.display( displayer );
                    }
                }
            } ).getFormDisplayTask( config.getKey().getServerTemplateId(),
                                    config.getKey().getDeploymentId(),
                                    config.getKey().getTaskId() );
        }
    }

}
