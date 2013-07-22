/*
 * Copyright 2010 JBoss Inc
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

    String Signal();

    String Abort();

    String Start_Date();

    String State();

    String Unknown();

    String Active();

    String Aborted();

    String Completed();

    String Pending();

    String Suspended();

    String Initiator();

    String Process_Instances_Refreshed();

    String No_Process_Instances_Found();

    String Aborting_Process_Instance_Not_Allowed();

    String Aborting_Process_Instance();

    String Signaling_Process_Instance();

    String Signaling_Process_Instance_Not_Allowed();

    String Bulk_Signal();

    String Related_To_Me();

    String Showing();

    String Filter();

    String Process_Instances();

    String Variable_History();

    String Edit_Variable();

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

    String Event();

    String Signal_Ref();

    String Process_Diagram();

    String Please_Wait();

    String Processes_Refreshed_From_The_Repo();

    String Bulk_Actions();

    String Process_Variable_History();

}
