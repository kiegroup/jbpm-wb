package org.jbpm.console.ng.bd.client.editors.deployment.descriptor.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.dd.type.DDResourceTypeDefinition;

import org.uberfire.client.resources.UberfireResources;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class DDResourceType extends DDResourceTypeDefinition implements ClientResourceType {

    private static final Image IMAGE = new Image( UberfireResources.INSTANCE.images().typeGenericFile() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = Constants.INSTANCE.Deployment();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }

}
