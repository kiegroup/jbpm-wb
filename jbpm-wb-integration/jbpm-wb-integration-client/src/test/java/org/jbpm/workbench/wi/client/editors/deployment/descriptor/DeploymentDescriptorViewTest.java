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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.model.Parameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.mockito.Spy;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class DeploymentDescriptorViewTest {

    @Spy
    private DeploymentDescriptorViewImpl view;

    @GwtMock
    private TextBox textBox;

    @GwtMock
    private ListBox listBox;

    @GwtMock
    private CheckBox checkBox;

    private DeploymentDescriptorModel deploymentDescriptorModel;

    private int textBoxUsages = 0;
    private int listBoxUsages = 0;
    private int checkBoxUsages = 0;

    @Before
    public void setup() {
        // test data
        deploymentDescriptorModel = new DeploymentDescriptorModel();

        deploymentDescriptorModel.setAuditPersistenceUnitName("audit-peristence"); ++textBoxUsages;
        deploymentDescriptorModel.setPersistenceUnitName("save-thingy"); ++textBoxUsages;

        deploymentDescriptorModel.setPersistenceMode(PersistenceMode.JPA.toString()); ++listBoxUsages;
        deploymentDescriptorModel.setAuditMode(AuditMode.JMS.toString()); ++listBoxUsages;
        deploymentDescriptorModel.setRuntimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE.toString()); ++listBoxUsages;

        deploymentDescriptorModel.setLimitSerializationClasses(true); ++checkBoxUsages;

        deploymentDescriptorModel.setRemotableClasses(Collections.singletonList("class1"));

        deploymentDescriptorModel.setConfiguration(
                Collections.singletonList(new ItemObjectModel("config", "value", "resolver", getParameters())));
        deploymentDescriptorModel.setEnvironmentEntries(
                Collections.singletonList(new ItemObjectModel("config", "value", "resolver", getParameters())));
        deploymentDescriptorModel.setEventListeners(
                Collections.singletonList(new ItemObjectModel("config", "value", "resolver", getParameters())));
        deploymentDescriptorModel.setGlobals(
                Collections.singletonList(new ItemObjectModel("config", "value", "resolver", getParameters())));
        deploymentDescriptorModel.setMarshallingStrategies(
                Collections.singletonList(new ItemObjectModel("config", "value", "resolver", getParameters())));
        deploymentDescriptorModel.setRequiredRoles(
                Collections.singletonList("roles"));
        deploymentDescriptorModel.setTaskEventListeners(
                Collections.singletonList(new ItemObjectModel("config", "value", "resolver", getParameters())));
        deploymentDescriptorModel.setWorkItemHandlers(
                Collections.singletonList(new ItemObjectModel("config", "value", "resolver", getParameters())));
    }

    private static Random random = new Random();

    private static List<Parameter> getParameters() {
        return Collections.singletonList(new Parameter(UUID.randomUUID().toString(), Integer.toString(random.nextInt(100000))));
    }

    @Test
    public void setContent() {
        view.setContent(deploymentDescriptorModel);

        verify( textBox, times( textBoxUsages ) ).setText( any( String.class ) );
        verify( listBox, times( listBoxUsages ) ).getItemCount();
        verify( checkBox, times( checkBoxUsages ) ).setValue(anyBoolean());

        String [] listNames = {
                "RemotableClasses",
                "Configuration",
                "EnvironmentEntries",
                "EventListeners",
                "Globals",
                "MarshallingStrategies",
                "RequiredRoles",
                "TaskEventListeners",
                "WorkItemHandlers"
        };
        List [][] listListArr = new List [][] {
                { deploymentDescriptorModel.getConfiguration(), view.configurationDataProvider.getList() },
                { deploymentDescriptorModel.getEnvironmentEntries(), view.environmentEntriesDataProvider.getList() },
                { deploymentDescriptorModel.getEventListeners(), view.eventListenersDataProvider.getList() },
                { deploymentDescriptorModel.getGlobals(), view.globalsDataProvider.getList() },
                { deploymentDescriptorModel.getMarshallingStrategies(), view.marshalStrategyDataProvider.getList() },
                { deploymentDescriptorModel.getRemotableClasses(), view.remoteableClassesDataProvider.getList() },
                { deploymentDescriptorModel.getRequiredRoles(), view.requiredRolesDataProvider.getList() },
                { deploymentDescriptorModel.getTaskEventListeners(), view.taskEventListenersDataProvider.getList() },
                { deploymentDescriptorModel.getWorkItemHandlers(), view.workItemHandlersDataProvider.getList() }
        };
        for( int i = 0; i < listListArr.length; ++i ) {
            assertEquals( listNames[i] + " size", listListArr[i][0].size(), listListArr[i][1].size() );
            assertEquals( listNames[i] + " value", listListArr[i][0].get(0), listListArr[i][1].get(0) );
        }
    }

    @Test
    public void updateContent() {
        view.configurationDataProvider.setList(deploymentDescriptorModel.getConfiguration());
        view.environmentEntriesDataProvider.setList(deploymentDescriptorModel.getEnvironmentEntries());
        view.eventListenersDataProvider.setList(deploymentDescriptorModel.getEventListeners());
        view.globalsDataProvider.setList(deploymentDescriptorModel.getGlobals());
        view.marshalStrategyDataProvider.setList(deploymentDescriptorModel.getMarshallingStrategies());
        view.remoteableClassesDataProvider.setList(deploymentDescriptorModel.getRemotableClasses());
        view.requiredRolesDataProvider.setList(deploymentDescriptorModel.getRequiredRoles());
        view.taskEventListenersDataProvider.setList(deploymentDescriptorModel.getTaskEventListeners());
        view.workItemHandlersDataProvider.setList(deploymentDescriptorModel.getWorkItemHandlers());

        DeploymentDescriptorModel newModel = new DeploymentDescriptorModel();
        view.updateContent(newModel);

        verify( textBox, times( textBoxUsages ) ).getText();
        verify( listBox, times( listBoxUsages ) ).getSelectedIndex();
        verify( checkBox, times( checkBoxUsages ) ).getValue();

        assertFalse( newModel.getConfiguration().isEmpty() );
        assertFalse( newModel.getEnvironmentEntries().isEmpty());
        assertFalse( newModel.getEventListeners().isEmpty());
        assertFalse( newModel.getGlobals().isEmpty());
        assertFalse( newModel.getMarshallingStrategies().isEmpty());
        assertFalse( newModel.getRemotableClasses().isEmpty());
        assertFalse( newModel.getRequiredRoles().isEmpty());
        assertFalse( newModel.getTaskEventListeners().isEmpty());
        assertFalse( newModel.getWorkItemHandlers().isEmpty());

    }

    private static final String jarLocRegexStr = "([\\d\\.]{3})\\S*";
    private static final Pattern jarLocRegex = Pattern.compile(jarLocRegexStr);

    @Test
    public void changeDefaultLimitSerializationClassesValueToTrueIn7x() throws Exception {
        Properties props = new Properties();
        String testPropsFileName = "test.properties";
        InputStream testPropsStream = this.getClass().getResourceAsStream("/" + testPropsFileName);
        assertNotNull("Unable to find or open " + testPropsFileName, testPropsFileName);
        props.load(testPropsStream);
        String projectVersionStr = (String) props.get("project.version");

        Matcher matcher = jarLocRegex.matcher(projectVersionStr);
        assertTrue( "Fix regular expression: " + jarLocRegexStr, matcher.matches() );
        double jarVersion = Double.parseDouble(matcher.group(1));

        DeploymentDescriptorViewImpl viewImpl = new DeploymentDescriptorViewImpl();

        deploymentDescriptorModel.setLimitSerializationClasses(null);

        viewImpl.setContent(deploymentDescriptorModel);

        boolean limitSerializationClasses = DeploymentDescriptorViewImpl.getLimitSerializationClassesCheckBoxValue(deploymentDescriptorModel);

        assertTrue( "The default value of 'limitSerializationClasses is FALSE in 6.x and TRUE in 7.x",
                ( jarVersion < 7.0d && ! limitSerializationClasses )
                || (jarVersion >= 7.0d && limitSerializationClasses ) );
    }

    @Test
    public void runtimeStrategiesSetting() throws Exception {
        String itemName = "item1_name";
        String itemValue = "item1_value";

        view.addPersistenceMode(itemName,itemValue);
        view.addAuditMode(itemName,itemValue);
        view.addRuntimeStrategy(itemName,itemValue);

        verify(listBox,times(3)).addItem(itemName,itemValue);
    }

}
