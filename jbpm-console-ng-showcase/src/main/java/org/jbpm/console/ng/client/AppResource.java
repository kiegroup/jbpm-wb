package org.jbpm.console.ng.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.jbpm.console.ng.client.images.AppImages;


public interface AppResource
        extends
        ClientBundle {

    AppResource INSTANCE = GWT.create( AppResource.class );

    AppImages images();

}
