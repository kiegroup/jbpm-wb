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

package org.jbpm.console.ng.he.client.i8n;

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

    String Day();

    String Week();

    String Month();

    String Grid();

    String New_Task();

    String Personal();

    String Group();

    String Active();

    String All();

    String No_Pending_Tasks_Enjoy();

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

    String Today();

    String Advanced();

    String Refresh();
    
    String Tasks_Refreshed();
    

}
