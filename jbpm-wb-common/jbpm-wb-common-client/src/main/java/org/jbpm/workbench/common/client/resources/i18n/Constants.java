/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.common.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.Messages.PluralCount;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-
 * toolkit-doc-1-5&t=DevGuideInternationalization (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the
 * properties file can still be used on the server). To use this, use
 * <code>GWT.create(Constants.class)</code>.
 */
public interface Constants extends Messages {

    Constants INSTANCE = GWT.create(Constants.class);

    String Actions();

    String Refresh();

    String No_Items_Found();

    String New_Item();

    String Select_Date();

    String Day();

    String Week();

    String Month();

    String Today();

    String Previous();

    String Next();

    String Loading();

    String RestoreDefaultFilters();

    String AreYouSureRestoreDefaultFilters();

    String DataSetNotFound(String dataSet);

    String DataSetError(String dataSet,
                        String errorMessage);

    String ServerTemplates();

    String Search();

    String SearchResults();

    String From();

    String To();

    String Yes();

    String No();

    String ActiveFilters();

    String ClearAll();

    String AllowedWildcardsForStrings();

    String ASubstituteForASingleCharacter();

    String ASubstituteForZeroOrMoreCharacters();

    String LastHour();

    String LastHours(Integer hours);

    String LastDays(Integer days);

    String Custom();

    String Home();

    String Filters();

    String Select();

    String Apply();

    String AdvancedFilters();

    String Process_Definitions();

    String Process_Instances();

    String Tasks();

    String Task_Inbox();

    String ExecutionErrors();

    String Jobs();

    String Manage();

    String Manage_Process_Definitions();

    String Manage_Process_Instances();

    String Manage_Tasks();

    String Manage_ExecutionErrors();

    String Manage_Jobs();

    String FilterBy();

    String SavedFilters();

    String RemoveSavedFilterTitle();

    String RemoveSavedFilterMessage(String filterName);

    String Remove();

    String ErrorCountNumberView(@PluralCount int errCount);

    String UnexpectedError(String message);

    String FilterWithSameNameAlreadyExists();

    String SavedFilterCorrectlyWithName(String filterName);

    String Bulk_Actions();

    String ExecutionServerUnavailable();

    String NoServerConnected();

    String MissingServerCapability();

    String MissingProcessCapability();

    String NA();

    String Unknown();

    String SlaNA();

    String SlaPending();

    String SlaMet();

    String SlaAborted();

    String SlaViolated();
}