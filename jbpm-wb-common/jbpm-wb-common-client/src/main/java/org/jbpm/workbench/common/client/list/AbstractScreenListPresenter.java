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
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.common.events.ServerTemplateSelected;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Commands;
import org.uberfire.mvp.PlaceRequest;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @param <T> data type for the AsyncDataProvider
 */
public abstract class AbstractScreenListPresenter<T> extends AbstractListPresenter<T> {

    protected User identity;

    private String detailScreenId;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    UberfireBreadcrumbs breadcrumbs;

    protected PlaceRequest place;

    protected ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    private String selectedServerTemplate = "";

    @OnOpen
    public void onOpen() {
        createListBreadcrumb();
        setSelectedServerTemplate(serverTemplateSelectorMenuBuilder.getSelectedServerTemplate());
    }

    public void onDetailScreenClosed(@Observes ClosePlaceEvent closed) {
        if (detailScreenId.equals(closed.getPlace().getIdentifier())) {
            createListBreadcrumb();
        }
    }

    @OnFocus
    public void onFocus() {
        setSelectedServerTemplate(serverTemplateSelectorMenuBuilder.getSelectedServerTemplate());
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
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

    public void onServerTemplateSelected(@Observes final ServerTemplateSelected serverTemplateSelected) {
        setSelectedServerTemplate(serverTemplateSelected.getServerTemplateId());
    }

    public String getSelectedServerTemplate() {
        return selectedServerTemplate;
    }

    public void setSelectedServerTemplate(final String selectedServerTemplate) {
        final String newServerTemplate = Optional.ofNullable(selectedServerTemplate).orElse("").trim();
        if (this.selectedServerTemplate.equals(newServerTemplate) == false) {
            this.selectedServerTemplate = newServerTemplate;
            refreshGrid();
        }
    }

    public abstract void createListBreadcrumb();

    public void setupListBreadcrumb(PlaceManager placeManager,
                                    String perspectiveId,
                                    String listLabel) {
        breadcrumbs.clearBreadcrumbs(perspectiveId);

        breadcrumbs.addBreadCrumb(perspectiveId,
                                  Constants.INSTANCE.Home(),
                                  () -> placeManager.goTo(PerspectiveIds.HOME));
        breadcrumbs.addBreadCrumb(perspectiveId,
                                  listLabel,
                                  Commands.DO_NOTHING);
    }

    public void setupDetailBreadcrumb(PlaceManager placeManager,
                                      String perspectiveId,
                                      String listLabel,
                                      String detailLabel,
                                      String detailScreenId) {
        breadcrumbs.clearBreadcrumbs(perspectiveId);
        breadcrumbs.addBreadCrumb(perspectiveId,
                                  Constants.INSTANCE.Home(),
                                  () -> placeManager.goTo(PerspectiveIds.HOME));
        breadcrumbs.addBreadCrumb(perspectiveId,
                                  listLabel,
                                  () -> closeDetails(detailScreenId));
        breadcrumbs.addBreadCrumb(perspectiveId,
                                  detailLabel,
                                  Commands.DO_NOTHING);
        this.detailScreenId = detailScreenId;
    }

    private void closeDetails(String detailScreenId) {
        placeManager.closePlace(detailScreenId);
        createListBreadcrumb();
    }

    public void setUberfireBreadcrumbs(UberfireBreadcrumbs breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }
}