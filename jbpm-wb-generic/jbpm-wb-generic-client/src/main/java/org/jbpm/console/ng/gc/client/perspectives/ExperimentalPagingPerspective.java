/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.gc.client.perspectives;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;


/**
 * A Perspective to show File Explorer
 */
@Templated
@WorkbenchPerspective(identifier = ExperimentalPagingPerspective.PERSPECTIVE_ID)
public class ExperimentalPagingPerspective extends AbstractPerspective implements IsElement {

    public static final String PERSPECTIVE_ID = "Experimental Paging";


    @Inject
    @DataField
    @WorkbenchPanel(parts = "Pagination For Tables")
    Div paginationTables;

    @PostConstruct
    protected void init() {
        super.init();
    }

    public String getPerspectiveId() {
        return PERSPECTIVE_ID;
    }

}