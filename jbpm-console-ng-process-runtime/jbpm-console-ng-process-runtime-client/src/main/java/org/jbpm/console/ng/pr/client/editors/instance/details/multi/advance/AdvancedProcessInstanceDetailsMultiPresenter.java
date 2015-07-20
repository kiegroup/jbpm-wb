package org.jbpm.console.ng.pr.client.editors.instance.details.multi.advance;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.pr.client.editors.instance.details.multi.BaseProcessInstanceDetailsMultiPresenter;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@WorkbenchScreen(identifier = "Advanced Process Instance Details Multi", preferredWidth = 500)
public class AdvancedProcessInstanceDetailsMultiPresenter extends BaseProcessInstanceDetailsMultiPresenter {

    public interface AdvancedProcessInstanceDetailsMultiView
            extends BaseProcessInstanceDetailsMultiPresenter.BaseProcessInstanceDetailsMultiView {

    }

    @Inject
    private AdvancedProcessInstanceDetailsMultiView view;

    @WorkbenchMenu
    public Menus buildSpecificMenu() {
        return buildMenu();
    }

    @Override
    protected BaseProcessInstanceDetailsMultiView getSpecificView() {
        return view;
    }

    @DefaultPosition
    public Position getPosition() {
        return super.getPosition();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instances();
    }

    @WorkbenchPartView
    public UberView<BaseProcessInstanceDetailsMultiPresenter> getView() {
        return view;
    }

    public void signalProcessInstance() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Signal Process Popup" );
        placeRequestImpl.addParameter( "processInstanceId", deploymentId );
        placeManager.goTo( placeRequestImpl );

    }
}
