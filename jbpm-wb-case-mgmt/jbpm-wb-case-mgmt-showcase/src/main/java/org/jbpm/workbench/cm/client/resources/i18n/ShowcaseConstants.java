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

package org.jbpm.workbench.cm.client.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface ShowcaseConstants {

    @TranslationKey(defaultValue = "")
    String LOGO_BANNER_ERROR = "LogoBannerError";

    @TranslationKey(defaultValue = "")
    String LOG_OUT = "LogOut";

    @TranslationKey(defaultValue = "")
    String ROLE = "Role";

    @TranslationKey(defaultValue = "")
    String KIE_SERVER_ERROR_403 = "KieServerError403";

    @TranslationKey(defaultValue = "")
    String KIE_SERVER_ERROR_401 = "KieServerError401";
}