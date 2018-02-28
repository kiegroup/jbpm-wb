/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.client;

import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.client.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.admin.DefaultAdminPageHelper;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.ConstantsAnswerMock;
import org.uberfire.mocks.IocTestingUtils;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ShowcaseEntryPointTest {

    @Mock
    private AppConfigService appConfigService;
    private CallerMock<AppConfigService> appConfigServiceCallerMock;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private User identity;

    @Mock
    private DefaultAdminPageHelper adminPageHelper;

    @Spy
    private DefaultWorkbenchFeaturesMenusHelper menusHelper;

    @Mock
    private WorkbenchMegaMenuPresenter menuBar;

    private ShowcaseEntryPoint showcaseEntryPoint;

    @Before
    public void setup() {
        appConfigServiceCallerMock = new CallerMock<>(appConfigService);

        showcaseEntryPoint = spy(new ShowcaseEntryPoint(appConfigServiceCallerMock,
                                                        activityBeansCache,
                                                        iocManager,
                                                        identity,
                                                        adminPageHelper,
                                                        menusHelper,
                                                        menuBar));
        mockMenuHelper();
        mockConstants();
        IocTestingUtils.mockIocManager(iocManager);
    }

    @Test
    public void setupMenuTest() {
        showcaseEntryPoint.setupMenu();

        ArgumentCaptor<Menus> menusCaptor = ArgumentCaptor.forClass(Menus.class);
        verify(menuBar).addMenus(menusCaptor.capture());

        Menus menus = menusCaptor.getValue();

        assertEquals(6,
                     menus.getItems().size());

        assertEquals(showcaseEntryPoint.constants.Authoring(),
                     menus.getItems().get(0).getCaption());
        assertEquals(showcaseEntryPoint.constants.Deploy(),
                     menus.getItems().get(1).getCaption());
        assertEquals(showcaseEntryPoint.constants.Process_Management(),
                     menus.getItems().get(2).getCaption());
        assertEquals(showcaseEntryPoint.constants.Work(),
                     menus.getItems().get(3).getCaption());
        assertEquals(showcaseEntryPoint.constants.Dashboards(),
                     menus.getItems().get(4).getCaption());
        assertEquals(showcaseEntryPoint.constants.Extensions(),
                     menus.getItems().get(5).getCaption());

        verify(menusHelper).addRolesMenuItems();
        verify(menusHelper).addGroupsMenuItems();
        verify(menusHelper).addWorkbenchConfigurationMenuItem();
        verify(menusHelper).addUtilitiesMenuItems();
    }

    @Test
    public void getAuthoringViewsTest() {
        List<? extends MenuItem> authoringMenuItems = showcaseEntryPoint.getAuthoringViews();

        assertEquals(3,
                     authoringMenuItems.size());
        assertEquals(showcaseEntryPoint.constants.Project_Authoring(),
                     authoringMenuItems.get(0).getCaption());
        assertEquals(showcaseEntryPoint.constants.artifactRepository(),
                     authoringMenuItems.get(1).getCaption());
        assertEquals(showcaseEntryPoint.constants.Administration(),
                     authoringMenuItems.get(2).getCaption());
    }

    @Test
    public void getProcessManagementViewsTest() {
        List<? extends MenuItem> processManagementMenuItems = showcaseEntryPoint.getProcessManagementViews();

        assertEquals(5,
                     processManagementMenuItems.size());
        assertEquals(showcaseEntryPoint.constants.Process_Definitions(),
                     processManagementMenuItems.get(0).getCaption());
        assertEquals(showcaseEntryPoint.constants.Process_Instances(),
                     processManagementMenuItems.get(1).getCaption());
        assertEquals(showcaseEntryPoint.constants.Process_Instances_Admin(),
                     processManagementMenuItems.get(2).getCaption());
        assertEquals(showcaseEntryPoint.constants.Tasks_Admin(),
                     processManagementMenuItems.get(3).getCaption());
        assertEquals(showcaseEntryPoint.constants.ExecutionErrors(),
                     processManagementMenuItems.get(4).getCaption());
    }

    @Test
    public void getDeploymentViewsTest() {
        List<? extends MenuItem> deploymentMenuItems = showcaseEntryPoint.getDeploymentViews();

        assertEquals(2,
                     deploymentMenuItems.size());
        assertEquals(showcaseEntryPoint.constants.Execution_Servers(),
                     deploymentMenuItems.get(0).getCaption());
        assertEquals(showcaseEntryPoint.constants.Jobs(),
                     deploymentMenuItems.get(1).getCaption());
    }

    @Test
    public void getWorkViewsTest() {
        List<? extends MenuItem> workMenuItems = showcaseEntryPoint.getWorkViews();

        assertEquals(3,
                     workMenuItems.size());
        assertEquals(showcaseEntryPoint.constants.Task_Inbox(),
                     workMenuItems.get(0).getCaption());
        assertEquals(showcaseEntryPoint.constants.Tasks_List_Admin(),
                     workMenuItems.get(1).getCaption());
        assertEquals(showcaseEntryPoint.constants.Data_Sets(),
                     workMenuItems.get(2).getCaption());
    }

    @Test
    public void getDashboardsViewsTest() {
        List<? extends MenuItem> dashboardsMenuItems = showcaseEntryPoint.getDashboardsViews();

        assertEquals(2,
                     dashboardsMenuItems.size());
        assertEquals(showcaseEntryPoint.constants.Process_Reports(),
                     dashboardsMenuItems.get(0).getCaption());
        assertEquals(showcaseEntryPoint.constants.Task_Reports(),
                     dashboardsMenuItems.get(1).getCaption());
    }

    private void mockMenuHelper() {
        doReturn(Collections.emptyList()).when(menusHelper).getRoles();
        doReturn(Collections.emptyList()).when(menusHelper).getGroups();
        doNothing().when(menusHelper).addWorkbenchConfigurationMenuItem();
        doNothing().when(menusHelper).addUtilitiesMenuItems();
    }

    private void mockConstants() {
        showcaseEntryPoint.constants = mock(Constants.class,
                                            new ConstantsAnswerMock());
    }
}
