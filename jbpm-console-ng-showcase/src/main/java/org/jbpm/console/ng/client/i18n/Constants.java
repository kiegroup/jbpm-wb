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

package org.jbpm.console.ng.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-
 * toolkit-doc-1-5&t=DevGuideInternationalization (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the
 * properties file can still be used on the server). To use this, use
 * <code>GWT.create(Constants.class)</code>.
 */
public interface Constants
        extends
        Messages {

    Constants INSTANCE = GWT.create( Constants.class );

    String SignOut();

    String WelcomeUser();
    

    There_is_no_variable_information_to_show();
    Variable();
    Value();
    Last_Time_Changed();
    View_History();
    Variable_History_Perspective();
        
    Show_me_my_pending_Tasks();
    I_want_to_start_a_new_Process();
    I_want_to_design_a_new_Process_Model();
    I_want_to_design_a_new_Form();
    I_want_to_create_a_Task();
    Show_me_all_the_pending_tasks_in_my_Group();
    Show_me_my_Inbox();
    
    Hooray_you_don_t_have_any_pending_Task__();
    Id();
    Task();
    Status();
    Due_On();
    Details();
    Request_Details_Perspective_Errai();
    
    No_KBases_Available();
	Please_Select_at_least_one_Task_to_Execute_a_Quick_Action();
	Priority();
	Status();
	No_Parent();
	Parent();
	Edit();
	Task_Edit_Perspective();
	Work();
	Form_Perspective();
	
	No_Process_Definitions_Available();
	Name();
	Package();
	Type();
	Version();
	Start_Process();
	Actions();
	Process_Definition_Details_Perspective();

	No_Process_Instances_Available();
	Deleting_Process_Instance();
	Terminating_Process_Instance();
	Signaling_Process_Instance();
	Process_Id();
	Process_Name();
	State();
	Process_Instance_Details_Perspective();
	
	Hooray_you_don_t_have_any_Group_Task_to_Claim__();
	Task_Id();
	Task_Name();
	Priority();
	Actual_Owner();
	Description();

	Hooray_you_don_t_have_any_pending_Task__();
	Please_Select_at_least_one_Task_to_Execute_a_Quick_Action();

	Completed();
	Pending();
	Personal_Task_Statistics();
}