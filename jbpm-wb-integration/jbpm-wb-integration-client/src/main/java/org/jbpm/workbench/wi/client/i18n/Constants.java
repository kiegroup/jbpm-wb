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

package org.jbpm.workbench.wi.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web- toolkit-doc-1-5&t=DevGuideInternationalization
 * (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the properties file can still be used on the server). To use
 * this, use <code>GWT.create(Constants.class)</code>.
 */
public interface Constants extends Messages {

    Constants INSTANCE = GWT.create(Constants.class);

    String No_Deployment_Units_Available();

    String Deployment_Units();

    String Undeploy();

    String Details();

    String Deploy_Unit();

    String Deploy_A_New_Unit();

    String GroupID();

    String Artifact();

    String Version();

    String KieBaseName();

    String KieSessionName();

    String Actions();

    String Refresh();

    String Deployed_Units_Refreshed();

    String Unit();

    String Please_Wait();

    String KIE_Configurations();

    String New_Deployment_Unit();

    String Strategy();

    String Deployment();

    String Undeploy_Question();

    String DeploymentDescriptorEditor();

    String PersistenceUnit();

    String AuditPersistenceUnit();

    String PersistenceMode();

    String AuditMode();

    String RuntimeStrategy();

    String MarshalStrategy();

    String Add();

    String Remove();

    String Resolver();

    String Value();

    String Identifier();

    String Name();

    String PromptForRemoval();

    String NoDataDefined();

    String DeploymentDescriptorParameters();

    String NoParametersDefined();

    String DDParametersPopupTitle();

    String EventListeners();

    String Globals();

    String WorkItemHandlers();

    String TaskEventListeners();

    String EnvironmentEntries();

    String Configuration();

    String RequiredRoles();

    String RemoteableClasses();

    String LimitSerializationClasses();

    String NamedParams();

    String NoParamResolver();

    String Type();

    String MergeMode();

    String EnterValue();

    String EnterResolverType();

    String CaseProject();

    String ConfigureProjectSuccess(String projectName);

    String ConfigureProjectFailure(String projectName);

    String CaseAppProvisioningStarted();

    String CaseAppProvisioningCompleted();

    String CaseAppProvisioningFailed();

    String CaseAppName();

    String CMMNFileTypeDescription();

    String CMMNFileTypeShortName();
    
    String ServiceTaskList();
    
    String NoServiceTasksFound();
    
    String BaseSettings();
    
    String MavenInstallHelp();
    
    String InstallPomDepsHelp();
    
    String UseVersionRangeHelp();
    
    String InstallServiceTask();
    
    String UninstallServiceTask();
}