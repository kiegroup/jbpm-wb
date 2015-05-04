/*
 * Copyright 2014 JBoss Inc
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
package org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance;


import com.github.gwtbootstrap.client.ui.*;

import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ga.forms.display.GenericFormDisplayer;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.ga.forms.display.view.FormDisplayerView;
import org.jbpm.console.ng.pr.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.console.ng.pr.forms.display.process.api.ProcessDisplayerConfig;
import org.jbpm.console.ng.pr.forms.display.process.api.StartProcessFormDisplayProvider;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;


import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;

import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.forms.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;


@Dependent
public class QuickNewProcessInstancePopup extends BaseModal implements FormDisplayerView {

    interface Binder
            extends
            UiBinder<Widget, QuickNewProcessInstancePopup> {

    }

    @UiField
    public FlowPanel processForm;

    @UiField
    public FlowPanel basicForm;

    //@UiField
    //public Form filterForm;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

    @UiField
    public ControlGroup processDeploymentIdControlGroup = new ControlGroup();

    @UiField
    public ListBox processDeploymentIdListBox = new ListBox();

    @UiField
    public HelpBlock processDeploymentIdHelpLabel = new HelpBlock();

    @UiField
    public ControlGroup processDefinitionsControlGroup = new ControlGroup();

    @UiField
    public ListBox processDefinitionsListBox = new ListBox();

    @UiField
    public HelpBlock processDefinitionsHelpLabel = new HelpBlock();

    @UiField
    public FlowPanel body;

    @Inject
    User identity;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<ProcessDefinitionService> processDefinitionService;

    protected QueryFilter currentFilter;

    private static Binder uiBinder = GWT.create( Binder.class );


    private Long parentProcessInstanceId = -1L;


    @Inject
    private StartProcessFormDisplayProvider widgetPresenter;

    private Command onCloseCommand;

    private Command childCloseCommand;

    private FormContentResizeListener formContentResizeListener;

    private boolean initialized = false;


    private GenericFormDisplayer currentDisplayer;

    private int initialWidth = -1;

    private String deploymentId;

    private String processId;

    GenericModalFooter footer = new GenericModalFooter();

    @Inject
    protected StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    public QuickNewProcessInstancePopup() {
        setTitle( Constants.INSTANCE.Start_process_instance() );
        add( uiBinder.createAndBindUi( this ) );
        //createForm();
    }

    public void show( Long parentProcessInstanceId ) {
        show();
        this.parentProcessInstanceId = parentProcessInstanceId;

    }

    public void show() {

        init();
        loadFormValues();

        processForm.setVisible( false );
        basicForm.setVisible( true );
        super.show();
    }

    private void okButton() {
        if ( validateForm() ) {
            createNewProcessInstance();
        }
    }

    protected void loadFormValues() {
        final Map<String, List<String>> dropDowns = new HashMap<String, List<String>>();
        processDefinitionsListBox.clear();
        processDeploymentIdListBox.clear();
        currentFilter = new PortableQueryFilter( 0,
                10,
                false, "",
                "",
                true );
        processDefinitionService.call( new RemoteCallback<List<ProcessSummary>>() {
            @Override
            public void callback( List<ProcessSummary> processSummaries ) {

                for ( ProcessSummary sum : processSummaries ) {
                    if ( dropDowns.get( sum.getDeploymentId() ) == null ) {
                        dropDowns.put( sum.getDeploymentId(), new ArrayList<String>() );

                    }
                    dropDowns.get( sum.getDeploymentId() ).add( sum.getProcessDefId() );
                }
                processDeploymentIdListBox.clear();
                processDeploymentIdListBox.addItem( "--------" );
                processDeploymentIdListBox.setSelectedIndex( 0 );
                for ( String deploymentId : dropDowns.keySet() ) {
                    processDeploymentIdListBox.addItem( deploymentId );
                }


            }
        } ).getAll( currentFilter );

        processDeploymentIdListBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {

                processDefinitionsListBox.clear();
                processDefinitionsListBox.addItem( "-------" );
                processDefinitionsListBox.setSelectedIndex( 0 );
                int selected = processDeploymentIdListBox.getSelectedIndex();

                if ( dropDowns.get( processDeploymentIdListBox.getValue( selected ) ) != null ) {
                    for ( String processDef : dropDowns.get( processDeploymentIdListBox.getValue( selected ) ) ) {
                        processDefinitionsListBox.addItem( processDef );
                    }
                }


            }
        } );


    }

    private boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();

        return valid;
    }

    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    private void createNewProcessInstance() {

        if ( processDefinitionsListBox.getSelectedIndex() == 0 ) {

            errorMessages.setText( Constants.INSTANCE.Select_Process() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );

        } else {
            deploymentId = processDeploymentIdListBox.getValue();
            processId = processDefinitionsListBox.getValue();

            processForm.setVisible( true );
            basicForm.setVisible( false );

            ProcessDisplayerConfig config = new ProcessDisplayerConfig( new ProcessDefinitionKey( deploymentId, processId ), processId );
            startProcessDisplayProvider.setup( config, this );

        }

    }

    private void clearErrorMessages() {
        errorMessages.setText( "" );

    }


    protected void init() {

        remove( footer );
        footer = new GenericModalFooter();
        footer.addButton( Constants.INSTANCE.Start(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY );

        add( footer );

        onCloseCommand = new Command() {
            @Override
            public void execute() {
                closePopup();
            }
        };

        formContentResizeListener = new FormContentResizeListener() {
            @Override
            public void resize( int width, int height ) {
                if ( initialWidth == -1 && getOffsetWidth() > 0 ) initialWidth = getOffsetWidth();
                if ( width > getOffsetWidth() ) setWidth( width + 20 );
                else if ( initialWidth != -1 ) setWidth( initialWidth );
                centerVertically( getElement() );
            }
        };

        this.addHiddenHandler( new HiddenHandler() {
            @Override
            public void onHidden( HiddenEvent hiddenEvent ) {
                if ( initialized ) closePopup();
            }
        } );
    }

    @Override
    public void display( GenericFormDisplayer displayer ) {
        currentDisplayer = displayer;

        body.clear();
        body.add( displayer.getContainer() );


        remove( footer );
        footer = new GenericModalFooter();
        if ( displayer.getOpener() == null ) footer.add( displayer.getFooter() );
        add( footer );

        centerVertically( getElement() );
        initialized = true;

    }

    public void closePopup() {
        initialized = false;
        hide();
        super.hide();

    }

    private native void centerVertically( Element e ) /*-{
        $wnd.jQuery(e).css("margin-top", (-1 * $wnd.jQuery(e).outerHeight() / 2) + "px");
    }-*/;

    @Override
    public Command getOnCloseCommand() {
        return onCloseCommand;
    }

    @Override
    public void setOnCloseCommand( Command onCloseCommand ) {
        this.childCloseCommand = onCloseCommand;
    }

    @Override
    public FormContentResizeListener getResizeListener() {
        return formContentResizeListener;
    }

    @Override
    public void setResizeListener( FormContentResizeListener resizeListener ) {
        formContentResizeListener = resizeListener;
    }

    @Override
    public GenericFormDisplayer getCurrentDisplayer() {
        return currentDisplayer;
    }


}
