/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.forms.modeler.backend.server.finder;

import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;

import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.rendering.FormFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class  RuntimeFormFinder implements FormFinder {
    private Logger log = LoggerFactory.getLogger( RuntimeFormFinder.class );

    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Override
    public Form getForm( String ctxUID ) {
        FormRenderContext renderContext = formRenderContextManager.getRootContext( ctxUID );
        if ( renderContext != null ) return renderContext.getForm();
        return null;
    }

    @Override
    public Form getFormByPath( String ctxUID, String formPath ) {
        FormRenderContext renderContext = formRenderContextManager.getRootContext( ctxUID );

        if ( renderContext != null ) {
            try {
                Object form = renderContext.getContextForms().get( formPath );

                if ( form != null ) {
                    if ( form instanceof Form ) {
                        return (Form) form;
                    } else if ( form instanceof String ) {
                        Form result = formSerializationManager.loadFormFromXML( (String) form );
                        renderContext.getContextForms().put( formPath, result );
                        return result;
                    }
                }
            } catch ( Exception e ) {
                log.warn( "Error getting form {} from context {}: {}", formPath, ctxUID, e );
            }
        }
        return null;
    }

    @Override
    public Form getFormById( String ctxUID, long formId ) {
        FormRenderContext renderContext = formRenderContextManager.getRootContext( ctxUID );
        if ( renderContext != null ) {
            try {
                if ( renderContext.getForm().getId().equals( new Long( formId ) ) ) {
                    return renderContext.getForm();
                }

                Map forms = renderContext.getContextForms();

                String header = formSerializationManager.generateHeaderFormFormId( formId );
                for ( Iterator it = forms.keySet().iterator(); it.hasNext(); ) {
                    String key = ( String ) it.next();
                    Object form = forms.get( key );
                    if ( form instanceof Form ) {
                        if ( ( ( Form ) form ).getId().equals( formId ) ) {
                            return ( Form ) form;
                        }
                    } else if ( form instanceof String && form.toString().trim().startsWith( header ) ) {
                        Form result = formSerializationManager.loadFormFromXML( ( String ) form );
                        renderContext.getContextForms().put( key, result );
                        return result;
                    }

                }
            } catch ( Exception e ) {
                log.warn( "Error getting form {} from context {}: {}", formId, ctxUID, e );
            }
        }
        return null;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
