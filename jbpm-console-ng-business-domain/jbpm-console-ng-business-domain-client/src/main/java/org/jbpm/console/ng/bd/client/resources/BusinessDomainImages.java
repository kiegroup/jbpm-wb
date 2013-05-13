/*
 * Copyright 2010 JBoss Inc
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

package org.jbpm.console.ng.bd.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface BusinessDomainImages extends ClientBundle {

    BusinessDomainImages INSTANCE = GWT.create(BusinessDomainImages.class);

    @Source("org/jbpm/console/ng/bd/public/images/icons/details-grid-icon.png")
    public ImageResource detailsGridIcon();

    @Source("org/jbpm/console/ng/bd/public/images/icons/undeploy-grid-icon.png")
    public ImageResource undeployGridIcon();
 

}
