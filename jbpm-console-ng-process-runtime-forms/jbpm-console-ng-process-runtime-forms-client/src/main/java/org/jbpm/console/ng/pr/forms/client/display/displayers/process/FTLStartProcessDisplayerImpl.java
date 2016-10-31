/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.pr.forms.client.display.displayers.process;

import java.util.Map;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;
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

@Dependent
public class FTLStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer {

    @Override
    public boolean supportsContent( String content ) {
        return true;
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    protected void initDisplayer() {
        publish( this );
        jsniHelper.publishGetFormValues();
        formContainer.clear();

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
                    add( new HTMLPanel( formContent ) );
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

        jsniHelper.injectFormValidationsScripts( formContent );

        formContainer.add( accordion );
    }

    @Override
    public native void startProcessFromDisplayer() /*-{
        try {
            if ($wnd.eval("taskFormValidator()")) $wnd.startProcess($wnd.getFormValues($doc.getElementById("form-data")));
        } catch (err) {
            alert("Unexpected error: " + err);
        }
    }-*/;

    public void startProcess( JavaScriptObject values ) {
        final Map<String, Object> params = jsniHelper.getParameters( values );
        processService.call( getStartProcessRemoteCallback(), getUnexpectedErrorCallback() )
                .startProcess( serverTemplateId, deploymentId, processDefId, getCorrelationKey(), params );
    }

    protected native void publish( FTLStartProcessDisplayerImpl ftl )/*-{
        $wnd.startProcess = function (form) {
            ftl.@org.jbpm.console.ng.pr.forms.client.display.displayers.process.FTLStartProcessDisplayerImpl::startProcess(Lcom/google/gwt/core/client/JavaScriptObject;)(form);
        }
    }-*/;
}
