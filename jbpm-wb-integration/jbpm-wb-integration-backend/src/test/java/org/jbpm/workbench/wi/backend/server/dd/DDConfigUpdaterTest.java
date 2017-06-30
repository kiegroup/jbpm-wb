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

package org.jbpm.workbench.wi.backend.server.dd;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.designer.notification.DesignerWorkitemInstalledEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceAddedEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DDConfigUpdaterTest {

    private static final String JPA_MARSHALLING_STRATEGY = "org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy";

    @Mock
    private IOService ioService;

    @Mock
    private KieProjectService projectService;

    @Mock
    private DDEditorServiceImpl ddEditorService;

    @Mock
    private DDConfigUpdaterHelper configUpdaterHelper;

    private DeploymentDescriptorModel model;
    private DDConfigUpdater ddConfigUpdater;

    @Before
    public void setup() {
        model = new DeploymentDescriptorModel();
        model.setOverview(new Overview());
        ddConfigUpdater = new DDConfigUpdater(ddEditorService,
                                              projectService,
                                              ioService,
                                              configUpdaterHelper);

        Path rootPath = Mockito.mock(Path.class);
        when(rootPath.toURI()).thenReturn("default://project");
        KieProject project = Mockito.mock(KieProject.class);
        when(project.getRootPath()).thenReturn(rootPath);

        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
        when(configUpdaterHelper.isPersistenceFile(any(Path.class))).thenReturn(true);
        when(configUpdaterHelper.buildJPAMarshallingStrategyValue(any(KieProject.class))).thenReturn(JPA_MARSHALLING_STRATEGY);
        when(projectService.resolveProject(any(Path.class))).thenReturn(project);
        when(ddEditorService.load(any(Path.class))).thenReturn(model);
    }

    @Test
    public void testProcessResourceAdd() {
        ddConfigUpdater.processResourceAdd(new ResourceAddedEvent(Mockito.mock(Path.class),
                                                                  "test resource",
                                                                  Mockito.mock(SessionInfo.class)));

        assertNotNull(model.getMarshallingStrategies());
        assertEquals(1,
                     model.getMarshallingStrategies().size());

        ItemObjectModel objectModel = model.getMarshallingStrategies().get(0);
        assertNotNull(objectModel);
        assertEquals(JPA_MARSHALLING_STRATEGY,
                     objectModel.getValue());
        assertEquals("mvel",
                     objectModel.getResolver());
    }

    @Test
    public void testProcessWorkitemInstall() {
        ddConfigUpdater.processWorkitemInstall(new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                                                  "mvel",
                                                                                  "new com.myhandlers.MyHandler()",
                                                                                  "MyWorkItem",
                                                                                  ""));

        assertNotNull(model.getWorkItemHandlers());
        assertEquals(1,
                     model.getWorkItemHandlers().size());

        ItemObjectModel objectModel = model.getWorkItemHandlers().get(0);
        assertNotNull(objectModel);
        assertEquals("MyWorkItem",
                     objectModel.getName());
        assertEquals("mvel",
                     objectModel.getResolver());
        assertEquals("new com.myhandlers.MyHandler()",
                     objectModel.getValue());

        // same name -- should not add
        ddConfigUpdater.processWorkitemInstall(new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                                                  "mvel",
                                                                                  "new com.myhandlers.MyHandler2()",
                                                                                  "MyWorkItem",
                                                                                  ""));

        assertNotNull(model.getWorkItemHandlers());
        assertEquals(1,
                     model.getWorkItemHandlers().size());
        // make sure the one we have is not this new one
        assertEquals("new com.myhandlers.MyHandler()",
                     model.getWorkItemHandlers().get(0).getValue());

        // different name - should add
        ddConfigUpdater.processWorkitemInstall(new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                                                  "reflection",
                                                                                  "com.myhandlers.MyHandler",
                                                                                  "MyWorkItem2",
                                                                                  ""));

        assertNotNull(model.getWorkItemHandlers());
        assertEquals(2,
                     model.getWorkItemHandlers().size());

        ItemObjectModel objectModel2 = model.getWorkItemHandlers().get(1);
        assertNotNull(objectModel2);
        assertEquals("MyWorkItem2",
                     objectModel2.getName());
        assertEquals("reflection",
                     objectModel2.getResolver());
        assertEquals("com.myhandlers.MyHandler",
                     objectModel2.getValue());

        // invalid (no name) - should not add
        ddConfigUpdater.processWorkitemInstall(new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                                                  "mvel",
                                                                                  "new com.myhandlers.MyHandler3()",
                                                                                  "",
                                                                                  ""));
        assertNotNull(model.getWorkItemHandlers());
        assertEquals(2,
                     model.getWorkItemHandlers().size());

        // invalid (no handler) - should not add
        ddConfigUpdater.processWorkitemInstall(new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                                                  "mvel",
                                                                                  "",
                                                                                  "MyWorkItem3",
                                                                                  ""));
        assertNotNull(model.getWorkItemHandlers());
        assertEquals(2,
                     model.getWorkItemHandlers().size());

        // invalid (no name and handler) - should not add
        ddConfigUpdater.processWorkitemInstall(new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                                                  "mvel",
                                                                                  "",
                                                                                  "",
                                                                                  ""));
        assertNotNull(model.getWorkItemHandlers());
        assertEquals(2,
                     model.getWorkItemHandlers().size());

        // test overwritten resolver from value
        ddConfigUpdater.processWorkitemInstall(new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                                                  "reflection",
                                                                                  "mvel: new com.myhandlers.MyHandler4()",
                                                                                  "MyWorkItem4",
                                                                                  ""));
        assertNotNull(model.getWorkItemHandlers());
        assertEquals(3,
                     model.getWorkItemHandlers().size());

        ItemObjectModel objectModel3 = model.getWorkItemHandlers().get(2);
        assertNotNull(objectModel3);
        assertEquals("MyWorkItem4",
                     objectModel3.getName());
        assertEquals("mvel",
                     objectModel3.getResolver());
        assertEquals("new com.myhandlers.MyHandler4()",
                     objectModel3.getValue());
    }

    @Test
    public void testIsValidWorkitem() {
        assertFalse(ddConfigUpdater.isValidWorkitem(null));

        assertFalse(ddConfigUpdater.isValidWorkitem(
                new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                   "",
                                                   "",
                                                   "",
                                                   "")
        ));

        assertFalse(ddConfigUpdater.isValidWorkitem(
                new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                   "mvel",
                                                   "",
                                                   "",
                                                   "")
        ));

        assertFalse(ddConfigUpdater.isValidWorkitem(
                new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                   "mvel",
                                                   "new com.myhandlers.MyHandler()",
                                                   "",
                                                   "")
        ));

        assertFalse(ddConfigUpdater.isValidWorkitem(
                new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                   "mvel",
                                                   "",
                                                   "MyHandler",
                                                   "")
        ));

        assertTrue(ddConfigUpdater.isValidWorkitem(
                new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                   "mvel",
                                                   "new com.myhandlers.MyHandler()",
                                                   "MyHandler",
                                                   "")
        ));

        assertTrue(ddConfigUpdater.isValidWorkitem(
                new DesignerWorkitemInstalledEvent(Mockito.mock(Path.class),
                                                   "",
                                                   "mvel: new com.myhandlers.MyHandler()",
                                                   "MyHandler",
                                                   "")
        ));
    }

    @Test
    public void testParseWorkitemValue() {
        assertEquals("new com.myhandlers.MyHandler()",
                     ddConfigUpdater.parseWorkitemValue("new com.myhandlers.MyHandler()"));
        assertEquals("new com.myhandlers.MyHandler()",
                     ddConfigUpdater.parseWorkitemValue("  new com.myhandlers.MyHandler()"));
        assertEquals("new com.myhandlers.MyHandler()",
                     ddConfigUpdater.parseWorkitemValue("new com.myhandlers.MyHandler()   "));

        assertEquals("new com.myhandlers.MyHandler()",
                     ddConfigUpdater.parseWorkitemValue("mvel: new com.myhandlers.MyHandler()"));
        assertEquals("new com.myhandlers.MyHandler()",
                     ddConfigUpdater.parseWorkitemValue("reflection: new com.myhandlers.MyHandler()"));

        assertEquals("new com.myhandlers.MyHandler()",
                     ddConfigUpdater.parseWorkitemValue("MVEL: new com.myhandlers.MyHandler()"));
        assertEquals("new com.myhandlers.MyHandler()",
                     ddConfigUpdater.parseWorkitemValue("REFLECTION: new com.myhandlers.MyHandler()"));

        assertEquals("new com.myhandlers.MyHandler()",
                     ddConfigUpdater.parseWorkitemValue("  MveL:     new com.myhandlers.MyHandler()"));
        assertEquals("new com.myhandlers.MyHandler()",
                     ddConfigUpdater.parseWorkitemValue("   ReFlEcTIoN:new com.myhandlers.MyHandler()   "));
    }

    @Test
    public void testGetWorkitemResolver() {
        assertEquals("mvel",
                     ddConfigUpdater.getWorkitemResolver("new com.myhandlers.MyHandler()",
                                                         "mvel"));
        assertEquals("mvel",
                     ddConfigUpdater.getWorkitemResolver("mvel:new com.myhandlers.MyHandler()",
                                                         "reflection"));
        assertEquals("reflection",
                     ddConfigUpdater.getWorkitemResolver("reflection:new com.myhandlers.MyHandler()",
                                                         "mvel"));
        assertEquals("reflection",
                     ddConfigUpdater.getWorkitemResolver("reflection:new com.myhandlers.MyHandler()",
                                                         ""));

        // test use of default when no resolver is specified
        assertEquals("reflection",
                     ddConfigUpdater.getWorkitemResolver("new com.myhandlers.MyHandler()",
                                                         ""));
        assertEquals("reflection",
                     ddConfigUpdater.getWorkitemResolver("new com.myhandlers.MyHandler()",
                                                         null));
    }
}