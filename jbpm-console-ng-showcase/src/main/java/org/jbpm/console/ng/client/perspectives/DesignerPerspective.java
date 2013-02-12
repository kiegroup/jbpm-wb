package org.jbpm.console.ng.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
@WorkbenchPerspective(identifier = "Designer", isDefault = false)
public class DesignerPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName( "Designer" );

        final PanelDefinition west = new PanelDefinitionImpl();
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "FileExplorer" ) ) );

        p.getRoot().appendChild( Position.WEST, west );

        return p;
    }
}
