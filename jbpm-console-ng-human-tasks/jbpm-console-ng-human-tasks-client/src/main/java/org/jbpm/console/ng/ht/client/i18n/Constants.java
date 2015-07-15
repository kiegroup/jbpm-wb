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

package org.jbpm.console.ng.ht.client.i18n;

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

    String Tasks_List();

    String Grid();

    String New_Task();

    String Personal();

    String Group();

    String Active();

    String All();

    String No_Tasks_Found();

    String Priority();

    String Task();

    String Id();

    String Status();

    String Due_On();

    String Parent();

    String Complete();

    String Release();

    String Claim();

    String Work();

    String Start();

    String Details();

    String Actions();

    String No_Parent();

    String User();

    String Process_Instance_Id();

    String Process_Definition_Id();

    String Process_Instance_Details();

    String No_Comments_For_This_Task();

    String Comment();

    String At();

    String Added_By();

    String Add_Comment();

    String Task_Must_Have_A_Name();

    String Create();

    String Task_Name();

    String Quick_Task();

    String Description();

    String Comments();

    String Filters();

    String Process_Context();

    String Update();

    String Form();

    String Advanced();

    String Basic();

    String Refresh();

    String AutoRefresh();

    String RestoreDefaultFilters();

    String Disable();

    String Tasks_Refreshed();

    String Add_User();

    String Add_Group();

    String Remove_User();

    String Remove_Group();

    String Assignments();

    String Auto_Assign_To_Me();

    String Created_On();

    String Text_Require();

    String UserOrGroup();

    String Forward();

    String Delegate();

    String Potential_Owners();

    String No_Potential_Owners();

    String Add_TypeRole();

    String Type_Role();

    String Parent_Group();

    String Save();

    String Delete();

    String Calendar();

    String Logs();

    String Task_Log();

    String Provide_User_Or_Group();

    String Show_Details();

    String Hide_Details();

    String Task_Admin();

    String Delegate_User();

    String Reminder();

    String Actual_Owner();

    String Reminder_Details();

    String No_Actual_Owner();

    String Loading();

    String DelegationUserInputRequired();

    String DelegationUnable();

    String DelegationSuccessfully();

    String TaskCreatedWithId(String id);

    String High();

    String Medium();

    String Low();

    String Close();

    String FilterManagement();
    String Created();
    String Ready();
    String Reserved();
    String InProgress();
    String Suspended();
    String Failed();
    String Error();
    String Exited();
    String Obsolete();
    String Completed();

    String TaskRole();
    String Initiator();
    String Stakeholder();
    String Potential_Owner();
    String Administrator();

    String Task_Form();
    String Task_Form_Name();
    String Task_Form_DeploymentId();

    String New_TaskList();

    String Administrators();
}
