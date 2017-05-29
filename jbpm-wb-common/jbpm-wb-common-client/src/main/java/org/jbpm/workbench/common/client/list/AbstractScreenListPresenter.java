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
package org.jbpm.workbench.common.client.list;

import java.util.Optional;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.events.ServerTemplateSelected;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @param <T> data type for the AsyncDataProvider
 */
public abstract class AbstractScreenListPresenter<T> extends AbstractListPresenter<T> {

    protected User identity;

    private String selectedServerTemplate = "";

    @Inject
    protected PlaceManager placeManager;

    protected PlaceRequest place;

    protected ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    @OnOpen
    public void onOpen() {
        setSelectedServerTemplate(serverTemplateSelectorMenuBuilder.getSelectedServerTemplate());
    }

    @OnFocus
    public void onFocus() {
        setSelectedServerTemplate(serverTemplateSelectorMenuBuilder.getSelectedServerTemplate());
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @Inject
    public void setIdentity(final User identity) {
        this.identity = identity;
    }

    @Inject
    public void setServerTemplateSelectorMenuBuilder(final ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder) {
        this.serverTemplateSelectorMenuBuilder = serverTemplateSelectorMenuBuilder;
    }

    public void onServerTemplateSelected(@Observes final ServerTemplateSelected serverTemplateSelected ) {
        setSelectedServerTemplate(serverTemplateSelected.getServerTemplateId());
    }

    protected void setSelectedServerTemplate(final String selectedServerTemplate) {
        final String newServerTemplate = Optional.ofNullable(selectedServerTemplate).orElse("").trim();
        if(this.selectedServerTemplate.equals(newServerTemplate) == false){
            this.selectedServerTemplate = newServerTemplate;
            refreshGrid();
        }
    }

    public String getSelectedServerTemplate() {
        return selectedServerTemplate;
    }
}