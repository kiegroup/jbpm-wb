/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.common.client.filters.advanced;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.workbench.common.client.filters.saved.SavedFilterSelectedEvent;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.df.client.filter.AdvancedFilterEditor;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.jbpm.workbench.df.client.filter.SavedFilter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AdvancedFiltersPresenter {

    private final Constants constants = Constants.INSTANCE;

    protected FilterSettingsManager filterSettingsManager;

    protected AdvancedFilterEditor advancedFilterEditorView;

    protected PlaceManager placeManager;

    private Event<SavedFilterSelectedEvent> savedFilterSelectedEvent;

    protected Event<NotificationEvent> notificationEvent;

    @WorkbenchPartView
    public AdvancedFilterEditor getView() {
        return advancedFilterEditorView;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.AdvancedFilters();
    }

    @OnOpen
    public void onOpen() {
        initAdvancedFilters();
    }

    protected void setFilterSettingsManager(final FilterSettingsManager filterSettingsManager) {
        this.filterSettingsManager = filterSettingsManager;
    }

    public abstract String getAdvancedFiltersEditorScreenId();

    @Inject
    public void setAdvancedFilterEditorView(AdvancedFilterEditor advancedFilterEditorView) {
        this.advancedFilterEditorView = advancedFilterEditorView;
    }

    @Inject
    public void setSavedFilterSelectedEvent(Event<SavedFilterSelectedEvent> savedFilterSelectedEvent) {
        this.savedFilterSelectedEvent = savedFilterSelectedEvent;
    }

    @Inject
    public void setNotificationEvent(Event<NotificationEvent> notificationEvent) {
        this.notificationEvent = notificationEvent;
    }

    @Inject
    public void setPlaceManager(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    public void displayNotification(String text) {
        notificationEvent.fire(new NotificationEvent(text,
                                                NotificationEvent.NotificationType.SUCCESS));
    }

    private void initAdvancedFilters() {
        advancedFilterEditorView.init(filterSettingsManager.createFilterSettingsPrototype(),
                                      filterSettings ->
                                              filterSettingsManager.saveFilterIntoPreferences(filterSettings,
                                                                                              state -> {
                                                                                                  if (state) {
                                                                                                      displayNotification(Constants.INSTANCE.SavedFilterCorrectlyWithName(filterSettings.getTableName()));
                                                                                                      savedFilterSelectedEvent.fire(new SavedFilterSelectedEvent(new SavedFilter(filterSettings.getKey(),
                                                                                                                                                                                 filterSettings.getTableName())));

                                                                                                      placeManager.closePlace(getAdvancedFiltersEditorScreenId());
                                                                                                  } else {
                                                                                                      advancedFilterEditorView.setTableNameError(Constants.INSTANCE.FilterWithSameNameAlreadyExists());
                                                                                                  }
                                                                                              })
        );
    }
}