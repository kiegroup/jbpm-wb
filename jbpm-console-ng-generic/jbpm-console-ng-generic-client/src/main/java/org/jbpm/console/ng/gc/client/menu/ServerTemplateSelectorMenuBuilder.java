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

package org.jbpm.console.ng.gc.client.menu;

import java.util.Collection;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ga.events.KieServerDataSetRegistered;
import org.jbpm.console.ng.ga.events.ServerTemplateSelected;
import org.kie.server.controller.api.model.events.ServerTemplateDeleted;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
public class ServerTemplateSelectorMenuBuilder implements MenuFactory.CustomMenuBuilder {

    @Inject
    private ServerTemplateSelectorView view;

    @Inject
    private Caller<SpecManagementService> specManagementService;

    @Inject
    private Event<ServerTemplateSelected> serverTemplateSelectedEvent;

    @PostConstruct
    public void init() {
        view.setServerTemplateChangeHandler(e -> serverTemplateSelectedEvent.fire(new ServerTemplateSelected(e)));
        loadServerTemplates();
    }

    protected void loadServerTemplates() {
        specManagementService.call((Collection<ServerTemplate> serverTemplates) -> {
            view.removeAllServerTemplates();

            final Set<String> ids = FluentIterable.from(serverTemplates)
                    .filter(s -> s.getServerInstanceKeys() != null && !s.getServerInstanceKeys().isEmpty())
                    .transform(s -> s.getId())
                    .toSortedSet(String.CASE_INSENSITIVE_ORDER);

            for (String id : ids) {
                view.addServerTemplate(id);
            }

            if(ids.size() == 1){
                view.selectServerTemplate(ids.iterator().next());
            } else {
                final String selectedServerTemplate = view.getSelectedServerTemplate();
                if (selectedServerTemplate != null) {
                    if (ids.contains(selectedServerTemplate)) {
                        view.selectServerTemplate(selectedServerTemplate);
                    } else {
                        view.clearSelectedServerTemplate();
                    }
                }
            }

            view.setVisible(ids.size() > 1);

        }, new DefaultErrorCallback()).listServerTemplates();
    }

    @Override
    public void push(MenuFactory.CustomMenuBuilder element) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return view;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void setEnabled(boolean enabled) {
            }
        };
    }

    public void onServerTemplateDeleted(@Observes final ServerTemplateDeleted serverTemplateDeleted) {
        loadServerTemplates();
    }

    public void onServerTemplateUpdated(@Observes final ServerTemplateUpdated serverTemplateUpdated) {
        loadServerTemplates();
    }

    public void onKieServerDataSetRegistered(@Observes final KieServerDataSetRegistered kieServerDataSetRegistered) {
        loadServerTemplates();
    }

    @Inject
    public void setSpecManagementService(final Caller<SpecManagementService> specManagementService) {
        this.specManagementService = specManagementService;
    }

    public String getSelectedServerTemplate() {
        return view.getSelectedServerTemplate();
    }

    public interface ServerTemplateSelectorView extends IsWidget {

        void selectServerTemplate(String serverTemplateId);

        void setVisible(boolean visible);

        void clearSelectedServerTemplate();

        String getSelectedServerTemplate();

        void addServerTemplate(String serverTemplateId);

        void removeAllServerTemplates();

        void setServerTemplateChangeHandler(ParameterizedCommand<String> command);

    }

}