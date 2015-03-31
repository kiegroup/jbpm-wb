package org.jbpm.console.ng.gc.client.experimental.workbranch.configuration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;

@ApplicationScoped
public class WorkbranchConfigurationMenuBuilder implements MenuFactory.CustomMenuBuilder {

    private NavLink link = new NavLink();
    
    @Inject
    private WorkbranchConfigurationPopup popup ;

    public WorkbranchConfigurationMenuBuilder() {
    	link.setIcon(IconType.COG);
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                popup.show();
            }
        } );
    }

    @Override
    public void push( final MenuFactory.CustomMenuBuilder element ) {
        //Do nothing
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return link;
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }
}
