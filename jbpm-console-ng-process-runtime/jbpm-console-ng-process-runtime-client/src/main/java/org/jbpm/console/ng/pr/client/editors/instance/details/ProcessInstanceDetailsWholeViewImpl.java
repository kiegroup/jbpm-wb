package org.jbpm.console.ng.pr.client.editors.instance.details;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.pr.client.i18n.Constants;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class ProcessInstanceDetailsWholeViewImpl extends Composite implements ProcessInstanceDetailsPresenter.ProcessInstanceDetailsWholeView {

    interface ProcessInstanceDetailsWholeViewImplBinder extends UiBinder<Widget, ProcessInstanceDetailsWholeViewImpl> {

    }

    private static ProcessInstanceDetailsWholeViewImplBinder uiBinder = GWT.create( ProcessInstanceDetailsWholeViewImplBinder.class );

    @Inject
    private ProcessInstanceDetailsPresenter.ProcessInstanceNodeHistoryView nodeHistoryView;

    @Inject
    private ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView detailsView;

    @UiField
    public HTMLPanel wholeView;

    @UiField
    public HTMLPanel parentsDetailsView;

    @UiField
    public HTMLPanel parentsNodeHistoryView;

    @UiField
    public ControlLabel gridAccordionLabel;

    @UiField
    public ControlLabel detailsAccordionLabel;
    
    private Constants constants = GWT.create( Constants.class );

    @Override
    public ProcessInstanceDetailsPresenter.ProcessInstanceNodeHistoryView getNodeHistoryView() {
        return nodeHistoryView;
    }

    @Override
    public ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView getProcessInstanceDetailsView() {
        return detailsView;
    }

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setUiBinder() {
        parentsNodeHistoryView.add( nodeHistoryView );
        parentsDetailsView.add( detailsView );
        detailsAccordionLabel.add( new HTMLPanel( constants.Process_Instance_Details() ) );
        gridAccordionLabel.add( new HTMLPanel( constants.Instance_Node_Logs() ) );
    }
}
