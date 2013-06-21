/*
 * Copyright 2013 JBoss Inc
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
    
    String List_Human_Event();
    
    String No_Human_Events();
    
    String Human_Event();
    
    String Id_Event();

    String Grid();

    String Personal();

    String Group();

    String Id();

    String Details();

    String Actions();

    String User();

    String Comment();

    String At();

    String Added_By();

    String Add_Comment();

    String Create();

    String Task_Name();

    String Description();

    String Comments();

    String Filters();

    String Process_Context();

    String Update();

    String Form();

    String Today();

    String Refresh();
    
    String Events_Refreshed();
    
    String Type_Event();
    
    String Time();
    
    String All();
    

}
