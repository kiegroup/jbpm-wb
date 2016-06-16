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

package org.jbpm.console.ng.client;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.client.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.ConstantsAnswerMock;
import org.uberfire.mocks.IocTestingUtils;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ShowcaseEntryPointTest {

    @Mock
    private AppConfigService appConfigService;
    private CallerMock<AppConfigService> appConfigServiceCallerMock;

    @Mock
    private PlaceManagerActivityService pmas;
    private CallerMock<PlaceManagerActivityService> pmasCallerMock;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private User identity;

    @Mock
    private DefaultWorkbenchFeaturesMenusHelper menusHelper;

    @Mock
    private WorkbenchMenuBarPresenter menuBar;

    private ShowcaseEntryPoint showcaseEntryPoint;

    @Before
    public void setup() {
        doNothing().when( pmas ).initActivities( anyList() );

        appConfigServiceCallerMock = new CallerMock<>( appConfigService );
        pmasCallerMock = new CallerMock<>( pmas );

        showcaseEntryPoint = spy( new ShowcaseEntryPoint( appConfigServiceCallerMock,
                                                          pmasCallerMock,
                                                          activityBeansCache,
                                                          iocManager,
                                                          identity,
                                                          menusHelper,
                                                          menuBar ) );
        mockMenuHelper();
        mockConstants();
        IocTestingUtils.mockIocManager( iocManager );
    }

    @Test
    public void setupMenuTest() {
        showcaseEntryPoint.setupMenu();

        ArgumentCaptor<Menus> menusCaptor = ArgumentCaptor.forClass( Menus.class );
        verify( menuBar ).addMenus( menusCaptor.capture() );

        Menus menus = menusCaptor.getValue();

        assertEquals( 9, menus.getItems().size() );

        assertEquals( showcaseEntryPoint.constants.Home(), menus.getItems().get( 0 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Authoring(), menus.getItems().get( 1 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Deploy(), menus.getItems().get( 2 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Case_Management(), menus.getItems().get( 3 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Process_Management(), menus.getItems().get( 4 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Work(), menus.getItems().get( 5 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Dashboards(), menus.getItems().get( 6 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Experimental(), menus.getItems().get( 7 ).getCaption() );

        verify( menusHelper ).addRolesMenuItems();
        verify( menusHelper ).addGroupsMenuItems();
        verify( menusHelper ).addWorkbenchConfigurationMenuItem();
        verify( menusHelper ).addUtilitiesMenuItems();
    }

    @Test
    public void getAuthoringViewsTest() {
        List<? extends MenuItem> authoringMenuItems = showcaseEntryPoint.getAuthoringViews();

        assertEquals( 1, authoringMenuItems.size() );
        assertEquals( showcaseEntryPoint.constants.Process_Authoring(), authoringMenuItems.get( 0 ).getCaption() );
    }

    @Test
    public void getCaseManagementViewsTest() {
        List<? extends MenuItem> caseManagementMenuItems = showcaseEntryPoint.getCaseManagementViews();

        assertEquals( 1, caseManagementMenuItems.size() );
        assertEquals( showcaseEntryPoint.constants.Cases(), caseManagementMenuItems.get( 0 ).getCaption() );
    }

    @Test
    public void getProcessManagementViewsTest() {
        List<? extends MenuItem> processManagementMenuItems = showcaseEntryPoint.getProcessManagementViews();

        assertEquals( 3, processManagementMenuItems.size() );
        assertEquals( showcaseEntryPoint.constants.Process_Definitions(), processManagementMenuItems.get( 0 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Process_Instances(), processManagementMenuItems.get( 1 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Process_Instances_Admin(), processManagementMenuItems.get( 2 ).getCaption() );
    }

    @Test
    public void getExperimentalViewsTest() {
        List<? extends MenuItem> experimentalMenuItems = showcaseEntryPoint.getExperimentalViews();

        assertEquals( 3, experimentalMenuItems.size() );
        assertEquals( showcaseEntryPoint.constants.Grid_Base_Test(), experimentalMenuItems.get( 0 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Logs(), experimentalMenuItems.get( 1 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Documents(), experimentalMenuItems.get( 2 ).getCaption() );
    }

    @Test
    public void getDeploymentViewsTest() {
        List<? extends MenuItem> deploymentMenuItems = showcaseEntryPoint.getDeploymentViews();

        assertEquals( 3, deploymentMenuItems.size() );
        assertEquals( showcaseEntryPoint.constants.Deployments(), deploymentMenuItems.get( 0 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Jobs(), deploymentMenuItems.get( 1 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Asset_Management(), deploymentMenuItems.get( 2 ).getCaption() );
    }

    @Test
    public void getWorkViewsTest() {
        List<? extends MenuItem> workMenuItems = showcaseEntryPoint.getWorkViews();

        assertEquals( 4, workMenuItems.size() );
        assertEquals( showcaseEntryPoint.constants.Tasks_List(), workMenuItems.get( 0 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Tasks_List_Drools(), workMenuItems.get( 1 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Tasks_List_Admin(), workMenuItems.get( 2 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Data_Sets(), workMenuItems.get( 3 ).getCaption() );
    }

    @Test
    public void getDashboardsViewsTest() {
        List<? extends MenuItem> dashboardsMenuItems = showcaseEntryPoint.getDashboardsViews();

        assertEquals( 2, dashboardsMenuItems.size() );
        assertEquals( showcaseEntryPoint.constants.Process_Dashboard(), dashboardsMenuItems.get( 0 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.Business_Dashboard(), dashboardsMenuItems.get( 1 ).getCaption() );
    }

    private void mockMenuHelper() {
        doReturn( mock( AbstractWorkbenchPerspectiveActivity.class ) ).when( menusHelper ).getDefaultPerspectiveActivity();
    }

    private void mockConstants() {
        showcaseEntryPoint.constants = mock( Constants.class, new ConstantsAnswerMock() );
    }

}
