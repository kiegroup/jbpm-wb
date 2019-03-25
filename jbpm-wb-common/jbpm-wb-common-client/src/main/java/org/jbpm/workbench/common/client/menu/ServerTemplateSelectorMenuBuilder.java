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

package org.jbpm.workbench.common.client.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.events.ServerTemplateSelected;
import org.jbpm.workbench.ks.events.KieServerDataSetRegistered;
import org.kie.server.controller.api.model.events.ServerTemplateDeleted;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
public class ServerTemplateSelectorMenuBuilder implements MenuFactory.CustomMenuBuilder {

    @Inject
    private ServerTemplateSelectorWidgetView widgetView;

    @Inject
    private ServerTemplateSelectorElementView view;

    @Inject
    private Caller<SpecManagementService> specManagementService;

    @Inject
    private Event<ServerTemplateSelected> serverTemplateSelectedEvent;

    private Map<String, ServerTemplate> serverTemplateMap = new HashMap<>();

    @PostConstruct
    public void init() {
        widgetView.setServerTemplateChangeHandler(e -> {
            view.updateSelectedValue(e);
            serverTemplateSelectedEvent.fire(new ServerTemplateSelected(serverTemplateMap.get(e)));
        });
        view.setServerTemplateChangeHandler(e -> {
            widgetView.updateSelectedValue(e);
            serverTemplateSelectedEvent.fire(new ServerTemplateSelected(serverTemplateMap.get(e)));
        });

        loadServerTemplates();
    }

    protected void loadServerTemplates() {
        serverTemplateMap.clear();
        widgetView.removeAllServerTemplates();
        view.removeAllServerTemplates();
        specManagementService.call((ServerTemplateList serverTemplates) -> {
            FluentIterable.from(serverTemplates.getServerTemplates())
                    .filter(s -> s.getServerInstanceKeys() != null && !s.getServerInstanceKeys().isEmpty())
                    .forEach(s -> serverTemplateMap.put(s.getId(), s));

            final List<String> ids = serverTemplateMap.keySet().stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());

            for (String id : ids) {
                widgetView.addServerTemplate(id);
                view.addServerTemplate(id);
            }

            if (ids.size() == 1) {
                widgetView.selectServerTemplate(ids.iterator().next());
                view.selectServerTemplate(ids.iterator().next());
            } else {
                final ServerTemplate selectedServerTemplate = getSelectedServerTemplate();
                if (selectedServerTemplate != null && ids.contains(selectedServerTemplate.getId())) {
                    widgetView.selectServerTemplate(selectedServerTemplate.getId());
                    view.selectServerTemplate(selectedServerTemplate.getId());
                } else {
                    widgetView.clearSelectedServerTemplate();
                    view.clearSelectedServerTemplate();
                    if (ids.size() > 1) {
                        String defaultServerTemplate = ids.get(0);
                        widgetView.selectServerTemplate(defaultServerTemplate);
                        view.selectServerTemplate(defaultServerTemplate);
                    }
                }
            }

            widgetView.setVisible(ids.size() > 1);
            view.setVisible(ids.size() > 1);
        }).listServerTemplates();
    }

    @Override
    public void push(MenuFactory.CustomMenuBuilder element) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return widgetView;
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

    public ServerTemplateSelectorElementView getView() {
        return view;
    }

    public void onServerTemplateDeleted(@Observes final ServerTemplateDeleted serverTemplateDeleted) {
        loadServerTemplates();
    }

    public void onServerTemplateUpdated(@Observes final ServerTemplateUpdated serverTemplateUpdated) {
        if (serverTemplateUpdated.getServerTemplate().getServerInstanceKeys().isEmpty()) {
            loadServerTemplates();
        }
    }

    public void onKieServerDataSetRegistered(@Observes final KieServerDataSetRegistered kieServerDataSetRegistered) {
        loadServerTemplates();
    }

    @Inject
    public void setSpecManagementService(final Caller<SpecManagementService> specManagementService) {
        this.specManagementService = specManagementService;
    }

    public ServerTemplate getSelectedServerTemplate() {
        return serverTemplateMap.get(view.getSelectedServerTemplate());
    }

    public String getSelectedServerTemplateId(){
        ServerTemplate serverTemplate = getSelectedServerTemplate();
        return serverTemplate == null ? "" : serverTemplate.getId();
    }

    public interface ServerTemplateSelectorView {

        void selectServerTemplate(String serverTemplateId);

        void updateSelectedValue(String serverTemplateId);

        void setVisible(boolean visible);

        void clearSelectedServerTemplate();

        String getSelectedServerTemplate();

        void addServerTemplate(String serverTemplateId);

        void removeAllServerTemplates();

        void setServerTemplateChangeHandler(ParameterizedCommand<String> command);
    }

    public interface ServerTemplateSelectorWidgetView extends ServerTemplateSelectorView,
                                                              IsWidget {

    }

    public interface ServerTemplateSelectorElementView extends ServerTemplateSelectorView,
                                                               UberElement<ServerTemplateSelectorMenuBuilder> {

    }
}
