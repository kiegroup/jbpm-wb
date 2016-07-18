/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.pr.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-toolkit-doc-1-5&t=DevGuideInternationalization
 * (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the properties file can still be used on the server). To use
 * this, use <code>GWT.create(Constants.class)</code>.
 */
public interface Constants extends Messages {

    Constants INSTANCE = GWT.create(Constants.class);

    String No_Process_Definitions_Found();

    String Reload_Repository();

    String Name();

    String Version();

    String Details();

    String Actions();

    String Start();

    String End();

    String Status();

    String Signal();

    String Abort();

    String Start_Date();

    String State();

    String Unknown();

    String Active();

    String FilterActive();

    String Aborted();

    String FilterAborted();

    String Completed();

    String FilterCompleted();

    String Pending();

    String Suspended();

    String Initiator();

    String Process_Instances_Refreshed();

    String No_Process_Instances_Found();

    String Aborting_Process_Instance_Not_Allowed(Object id);

    String Aborting_Process_Instance(Object id);

    String Signaling_Process_Instance();

    String Signaling_Process_Instance_Not_Allowed(Object id);

    String Bulk_Signal();

    String Related_To_Me();

    String Showing();

    String Filter();

    String Process_Instances();

    String History();

    String Edit();

    String Last_Modification();

    String Value();

    String Type();

    String No_Variables_Available();

    String Refresh();

    String View_Process_Instances();

    String New_Process_Instance();

    String View_Process_Model();

    String Process_Definition_Id();

    String Process_Definition_Name();

    String Human_Tasks_Count();

    String Deployment_Name();

    String Human_Tasks();

    String User_And_Groups();

    String SubProcesses();

    String Process_Variables();

    String Process_Definition_Details();

    String Process_Definition_Details_Refreshed();

    String Previous_Value();

    String No_History_For_This_Variable();

    String Process_Definitions();

    String Process_Instance_Details();

    String Process_Instance_ID();

    String Process_Definition_Package();

    String Process_Definition_Version();

    String Process_Instance_State();

    String Current_Activities();

    String Process_Instance_Log();

    String Process_Instances_Details_Refreshed();

    String Clear();

    String Save();

    String Variables_Name();

    String Variable_Value();

    String Variables_History();

    String Signalling_Process_Instance();

    String Signal_Data();

    String Signal_Name();

    String Process_Diagram();

    String Please_Wait();

    String Processes_Refreshed_From_The_Repo();

    String Bulk_Actions();

    String Process_Variable_History();

    String Process_Definitions_Refreshed();

    String Options();

    String Definition_Details();

    String New_Instance();

    String Bulk_Abort();

    String Process_Model();

    String Views();

    String Process_Variables_Refreshed();

    String Services();

    String Id();

    String Logs();

    String Get_Instance_Data();

    String No_Documents_Available();

    String Documents();

    String download();

    String Last_Modified();

    String Size();

    String Technical_Log();

    String Business_Log();

    String Desc_Log_Order();

    String Asc_Log_Order();

    String Process_Instance_Name();

    String Process_Instance_Description();

    String Select();

    String Active_Tasks();

    String Owner();

    String Project();

    String Loading();

    String VariableValueUpdated(String variableName);

    String Ok();

    String FilterManagement();

    String Close();

    String Correlation_Key();
    
    String Process_DeploymentId();
    
    String Basic();
    
    String Advanced();
    
    String Form();

    String Parent_Process_Instance();

    String ParentProcessInstanceId();

    String No_Parent_Process_Instance();

    String New_Process_InstanceList();

    String Abort_Process_Instance();

    String Abort_Process_Instances();

    String UnexpectedError(String message);

    String ErrorRetrievingProcessDefinitions(String message);

    String ErrorRetrievingProcessVariables(String message);

    String Outcome();

    String Duration();

    String Identity();

    String ExternalId();

    String NoSubprocessesRequiredByThisProcess();

    String NoProcessVariablesDefinedForThisProcess();

    String NoUserOrGroupUsedInThisProcess();

    String NoUserTasksDefinedInThisProcess();

    String NoServicesRequiredForThisProcess();

    String ErrorRetrievingProcessDocuments(String message);

    String AccessDocument();

    String ProcessInstanceNeedsToBeActiveInOrderToBeAborted();

    String SelectServerTemplate();
}
