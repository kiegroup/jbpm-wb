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

package org.jbpm.console.ng.es.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;
import org.uberfire.workbench.model.menu.MenuItem;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web- toolkit-doc-1-5&t=DevGuideInternationalization
 * (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the properties file can still be used on the server). To use
 * this, use <code>GWT.create(Constants.class)</code>.
 */
public interface Constants extends Messages {

    Constants INSTANCE = GWT.create( Constants.class );

    String Queued();

    String All();

    String Running();

    String Retrying();

    String Error();

    String Completed();

    String Showing();

    String Cancelled();

    String Refresh();

    String Settings();

    String New_Job();

    String No_Pending_Jobs();

    String Id();

    String Due_On();

    String Actions();

    String Started();

    String Stopped();

    String No_Parameters_added_yet();

    String Add_Parameter();

    String Create();

    String Name();

    String Type();

    String Retries();

    String Start_StopService();

    String Status();

    String Number_of_Threads();

    String Frequency();

    String JobName();

    String RequestsListTitle();

    String No_Jobs_Found();

    String The_Job_Must_Have_A_Name();

    String The_Job_Must_Have_A_Due_Date_In_The_Future();

    String The_Job_Must_Have_A_Type();

    String The_Job_Must_Have_A_Positive_Number_Of_Reties();

    String Please_Provide_A_Valid_Frequency();

    String Please_Provide_The_Number_Of_Executors();

    String Please_Provide_A_Valid_Number_Of_Executors();

    String Stop();

    String Start();

    String Loading();

    String Advanced();

    String Basic();

    String The_Job_Must_Have_A_Valid_Type();

    String ServiceStarted();

    String ServiceStopped();

    String Job_Service_Settings();

    String Number_Of_Attempted_Retries();
    String Execution_Parameters();
    String Exceptions_Occurred();
    String Ok();
    String Job_Request_Details();

    String ManageFilters();


}
