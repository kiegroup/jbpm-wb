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

package org.jbpm.workbench.common.client.filters.saved;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jbpm.workbench.df.client.events.SavedFilterAddedEvent;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.jbpm.workbench.common.client.menu.RestoreDefaultFiltersMenuBuilder;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.df.client.filter.SavedFilter;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Commands;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

public class SavedFiltersPresenter implements RestoreDefaultFiltersMenuBuilder.SupportsRestoreDefaultFilters {

    private final Constants constants = Constants.INSTANCE;

    private FilterSettingsManager filterSettingsManager;

    @Inject
    private SavedFiltersViewImpl view;

    @WorkbenchPartView
    public IsElement getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.SavedFilters();
    }

    @PostConstruct
    public void init() {
        loadSavedFilters();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelCustomMenu(new RestoreDefaultFiltersMenuBuilder(this)).endMenu()
                .build();
    }

    public void setFilterSettingsManager(final FilterSettingsManager filterSettingsManager) {
        this.filterSettingsManager = filterSettingsManager;
    }

    protected void loadSavedFilters() {
        filterSettingsManager.loadSavedFilters(filters -> filters.forEach(f -> view.addSavedFilter(f)));
    }

    protected void removeSavedFilter(final SavedFilter savedFilter) {
        filterSettingsManager.removeSavedFilterFromPreferences(savedFilter.getKey());
        view.removeSavedFilter(savedFilter);
    }

    protected void onRestoreFilters() {
        view.removeAllSavedFilters();
        filterSettingsManager.resetDefaultSavedFilters(filters -> filters.forEach(f -> view.addSavedFilter(f)));
    }

    public void onSaveFilter(@Observes final SavedFilterAddedEvent event) {
        view.addSavedFilter(event.getFilter());
    }

    public void onRemoveSavedFilter(@Observes final SavedFilterRemoveEvent event) {
        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(constants.RemoveSavedFilterTitle(),
                                                                                 constants.RemoveSavedFilterMessage(event.getSavedFilter().getName()),
                                                                                 () -> removeSavedFilter(event.getSavedFilter()),
                                                                                 null,
                                                                                 Commands.DO_NOTHING);
        yesNoCancelPopup.show();
    }

    @Override
    public void onRestoreDefaultFilters() {
        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(constants.RestoreDefaultFilters(),
                                                                                 constants.AreYouSureRestoreDefaultFilters(),
                                                                                 () -> onRestoreFilters(),
                                                                                 null,
                                                                                 Commands.DO_NOTHING);
        yesNoCancelPopup.show();
    }

    public void onSaveDefaultActiveFilter(@Observes final SavedFilterAsDefaultActiveEvent event) {
        filterSettingsManager.saveDefaultActiveFilter(event.getSavedFilter().getKey(),
                                                      () -> view.updateSavedFiltersDefault(event.getSavedFilter().getKey())
        );
    }
}
