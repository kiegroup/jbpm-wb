/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.forms.modeler.client.editors.taskform.displayers;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.DOM;
import org.gwtbootstrap3.client.shared.event.HideEvent;
import org.gwtbootstrap3.client.shared.event.HideHandler;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.forms.modeler.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.console.ng.pr.forms.client.display.displayers.process.AbstractStartProcessFormDisplayer;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.api.events.ResizeFormcontainerEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;

/**
 * @author salaboy
 */
@Dependent
public class FormModellerStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer {

    private static final String ACTION_START_PROCESS = "startProcess";

    @Inject
    private FormRendererWidget formRenderer;

    @Inject
    private Caller<FormModelerProcessStarterEntryPoint> renderContextServices;

    protected String action;

    protected void initDisplayer() {
        formRenderer.loadContext( formContent );

        formRenderer.setVisible( true );

        final PanelGroup accordion = new PanelGroup();
        accordion.setId( DOM.createUniqueId() );

        accordion.add( new Panel() {{
            final PanelCollapse collapse = new PanelCollapse() {{
                setIn( false );
                addHideHandler( new HideHandler() {
                    @Override
                    public void onHide( final HideEvent hideEvent ) {
                        hideEvent.stopPropagation();
                    }
                } );
                add( new PanelBody() {{
                    add( correlationKey );
                }} );
            }};
            add( new PanelHeader() {{
                add( new Heading( HeadingSize.H4 ) {{
                    add( new Anchor() {{
                        setText( constants.Correlation_Key() );
                        setDataToggle( Toggle.COLLAPSE );
                        setDataParent( accordion.getId() );
                        setDataTargetWidget( collapse );
                    }} );
                }} );
            }} );
            add( collapse );
        }} );

        accordion.add( new Panel() {{
            final PanelCollapse collapse = new PanelCollapse() {{
                setIn( true );
                addHideHandler( new HideHandler() {
                    @Override
                    public void onHide( final HideEvent hideEvent ) {
                        hideEvent.stopPropagation();
                    }
                } );
                add( new PanelBody() {{
                    add( formRenderer.asWidget() );
                }} );
            }};
            add( new PanelHeader() {{
                add( new Heading( HeadingSize.H4 ) {{
                    add( new Anchor() {{
                        setText( constants.Form() );
                        setDataToggle( Toggle.COLLAPSE );
                        setDataParent( accordion.getId() );
                        setDataTargetWidget( collapse );
                    }} );
                }} );
            }} );
            add( collapse );
        }} );

        formContainer.add( accordion );
    }

    public void startProcessFromDisplayer() {
        submitForm( ACTION_START_PROCESS );
    }

    protected void submitForm( String action ) {
        this.action = action;
        formRenderer.submitFormAndPersist();
    }

    @Override
    public boolean supportsContent( String content ) {
        return formRenderer.isValidContextUID( content );
    }

    @Override
    public void close() {
        renderContextServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void response ) {
                formContent = null;
                FormModellerStartProcessDisplayerImpl.super.close();
            }
        } ).clearContext( formContent );
    }

    @Override
    public int getPriority() {
        return 1;
    }

    public void onFormSubmitted( @Observes FormSubmittedEvent event ) {
        if ( event.isMine( formContent ) ) {
            if ( event.getContext().getErrors() == 0 ) {
                if ( ACTION_START_PROCESS.equals( action ) ) {
                    renderContextServices.call( getStartProcessRemoteCallback(), getUnexpectedErrorCallback() )
                            .startProcessFromRenderContext( formContent, deploymentId, processDefId, getCorrelationKey(), parentProcessInstanceId );
                }
            }
        }
    }

    public void onFormResized( @Observes ResizeFormcontainerEvent event ) {
        if ( event.isMine( formContent ) ) {
            formRenderer.resize( event.getWidth(), event.getHeight() );
            if ( resizeListener != null ) {
                resizeListener.resize( event.getWidth(), event.getHeight() );
            }
        }
    }

}
