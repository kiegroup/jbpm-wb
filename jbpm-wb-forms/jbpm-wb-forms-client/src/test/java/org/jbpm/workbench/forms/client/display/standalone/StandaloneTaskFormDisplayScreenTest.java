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

package org.jbpm.workbench.forms.client.display.standalone;

import org.assertj.core.api.Assertions;
import org.jbpm.workbench.forms.client.display.api.HumanTaskFormDisplayProvider;
import org.jbpm.workbench.forms.client.display.views.display.EmbeddedFormDisplayer;
import org.jbpm.workbench.forms.display.api.HumanTaskDisplayerConfig;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StandaloneTaskFormDisplayScreenTest {

    private static final String SERVER_TEMPLATE_ID = "test-kie-server";
    private static final String DOMAIN_ID = "test_1.0.0";
    private static final String TASK_ID = "1";

    @Mock
    private PlaceManager placeManager;

    @Mock
    private EmbeddedFormDisplayer displayer;

    @Mock
    private HumanTaskFormDisplayProvider humanTaskFormDisplayProvider;

    private StandaloneTaskFormDisplayScreen screen;

    @Before
    public void init() {
        screen = new StandaloneTaskFormDisplayScreen(placeManager, displayer, humanTaskFormDisplayProvider);
    }

    @Test
    public void testBasicFunctions() {
        Assertions.assertThat(screen.getTitle())
                .isNotNull()
                .isEmpty();

        Assertions.assertThat(screen.getView())
                .isNotNull()
                .isEqualTo(displayer);
    }

    @Test
    public void testOnOpen() {
        PlaceRequest place = new DefaultPlaceRequest();

        place.addParameter(StandaloneConstants.SERVER_TEMPLATE_PARAM, SERVER_TEMPLATE_ID);
        place.addParameter(StandaloneConstants.DOMAIN_ID_PARAM, DOMAIN_ID);
        place.addParameter(StandaloneConstants.TASK_ID_PARAM, TASK_ID);

        screen.onStartup(place);

        screen.onOpen();

        ArgumentCaptor<HumanTaskDisplayerConfig> configArgumentCaptor = ArgumentCaptor.forClass(HumanTaskDisplayerConfig.class);

        verify(humanTaskFormDisplayProvider).setup(configArgumentCaptor.capture(), any());

        HumanTaskDisplayerConfig config = configArgumentCaptor.getValue();

        Assertions.assertThat(config)
                .isNotNull();

        Assertions.assertThat(config.getKey())
                .isNotNull()
                .hasFieldOrPropertyWithValue("serverTemplateId", SERVER_TEMPLATE_ID)
                .hasFieldOrPropertyWithValue("deploymentId", DOMAIN_ID)
                .hasFieldOrPropertyWithValue("taskId", Long.decode(TASK_ID));

        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);

        verify(displayer).setOnCloseCommand(commandArgumentCaptor.capture());

        Command command = commandArgumentCaptor.getValue();

        Assertions.assertThat(command)
                .isNotNull();

        command.execute();

        verify(placeManager).closePlace(place);
    }
}
