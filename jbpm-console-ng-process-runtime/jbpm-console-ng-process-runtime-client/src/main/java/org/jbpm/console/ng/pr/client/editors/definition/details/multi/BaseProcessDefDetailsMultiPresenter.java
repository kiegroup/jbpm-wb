package org.jbpm.console.ng.pr.client.editors.definition.details.multi;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.uberfire.workbench.model.Position;

import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsPresenter;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView.TabbedDetailsView;
import org.jbpm.console.ng.ht.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.console.ng.ht.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.console.ng.ht.forms.display.process.api.ProcessDisplayerConfig;
import org.jbpm.console.ng.pr.client.editors.diagram.ProcessDiagramUtil;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.events.ProcessDefSelectionEvent;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

public abstract class BaseProcessDefDetailsMultiPresenter extends
        AbstractTabbedDetailsPresenter {

    public interface BaseProcessDefDetailsMultiView extends
            TabbedDetailsView<BaseProcessDefDetailsMultiPresenter> {

        IsWidget getCloseButton();

        IsWidget getRefreshButton();

        IsWidget getNewInstanceButton();
    }

    @Inject
    protected StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    @Inject
    protected PopupFormDisplayerView formDisplayPopUp;

    @Inject
    private Event<ProcessDefSelectionEvent> processDefSelectionEvent;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private Constants constants = GWT.create(Constants.class);

    public BaseProcessDefDetailsMultiPresenter() {

    }

    protected Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        super.onStartup(place);
    }

    public void onProcessSelectionEvent(
            @Observes final ProcessDefSelectionEvent event) {
        deploymentId = event.getDeploymentId();
        processId = event.getProcessId();

        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(this.place,
                String.valueOf(deploymentId) + " - " + processId));

        setDefaultTab();
    }

    protected abstract void setDefaultTab();

    public void createNewProcessInstance() {
        ProcessDisplayerConfig config = new ProcessDisplayerConfig(
                new ProcessDefinitionKey(deploymentId, processId), processId);

        formDisplayPopUp.setTitle("");

        startProcessDisplayProvider.setup(config, formDisplayPopUp);
    }

    public void goToProcessDefModelPopup() {
        if (place != null && !deploymentId.equals("")) {
            placeManager.goTo(ProcessDiagramUtil
                    .buildPlaceRequest(new DefaultPlaceRequest("")
                            .addParameter("processId", processId).addParameter(
                                    "deploymentId", deploymentId)));
        }
    }

    public void viewProcessInstances() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(
                "Process Instances");
        placeRequestImpl.addParameter("processName", processId);
        placeManager.goTo(placeRequestImpl);
    }

    public void refresh() {
        processDefSelectionEvent.fire(new ProcessDefSelectionEvent(processId,
                deploymentId));
    }
}
