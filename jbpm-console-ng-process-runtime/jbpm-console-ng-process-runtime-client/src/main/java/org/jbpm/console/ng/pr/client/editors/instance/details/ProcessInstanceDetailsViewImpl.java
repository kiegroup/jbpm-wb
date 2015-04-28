package org.jbpm.console.ng.pr.client.editors.instance.details;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class ProcessInstanceDetailsViewImpl extends Composite implements ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView {

    interface ProcessInstanceDetailsViewImplBinder extends UiBinder<Widget, ProcessInstanceDetailsViewImpl> {

    }

    private static ProcessInstanceDetailsViewImplBinder uiBinder = GWT.create( ProcessInstanceDetailsViewImplBinder.class );

    @UiField
    public HTML processDefinitionIdText;

    @UiField
    public HTML processDeploymentText;

    @UiField
    public HTML processVersionText;

    @UiField
    public HTML correlationKeyText;

    @UiField
    public HTML stateText;

    @UiField
    public Label processDefinitionIdLabel;

    @UiField
    public Label processDeploymentLabel;

    @UiField
    public Label processVersionLabel;

    @UiField
    public Label correlationKeyLabel;

    @UiField
    public Label stateLabel;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );
    private Path processAssetPath;
    private String encodedProcessSource;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ));
    }

    public void initLables() {
        processDefinitionIdLabel.setText( constants.Process_Definition_Id() );
        processDeploymentLabel.setText( constants.Deployment_Name() );
        processVersionLabel.setText( constants.Process_Definition_Version() );
        correlationKeyLabel.setText( constants.Correlation_Key() );
        stateLabel.setText( constants.Process_Instance_State() );
    }

    
    @Override
    public HTML getProcessDefinitionIdText() {
        return processDefinitionIdText;
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public HTML getStateText() {
        return this.stateText;
    }

    @Override
    public HTML getProcessDeploymentText() {
        return processDeploymentText;
    }

    @Override
    public HTML getCorrelationKeyText() {
        return correlationKeyText;
    }

    @Override
    public HTML getProcessVersionText() {
        return processVersionText;
    }

    @Override
    public void setProcessAssetPath( Path processAssetPath ) {
        this.processAssetPath = processAssetPath;
    }

    @Override
    public void setEncodedProcessSource( String encodedProcessSource ) {
        this.encodedProcessSource = encodedProcessSource;
    }

    public Path getProcessAssetPath() {
        return processAssetPath;
    }

    public String getEncodedProcessSource() {
        return encodedProcessSource;
    }

}
