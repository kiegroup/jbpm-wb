package org.jbpm.console.ng.gc.client.preferences.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface Constants extends Messages {

    Constants INSTANCE = GWT.create( Constants.class );

    String Advanced();

    String Basic();

    String View_Mode();
    
    String View_Mode_Selector();
}
