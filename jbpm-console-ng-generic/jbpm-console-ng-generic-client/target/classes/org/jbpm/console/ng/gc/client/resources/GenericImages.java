/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.console.ng.gc.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface GenericImages extends ClientBundle {

    GenericImages INSTANCE = GWT.create(GenericImages.class);

    @Source("org/jbpm/console/ng/ht/public/images/icons/abort-grid-icon.png")
    public ImageResource abortGridIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/complete-grid-icon.png")
    public ImageResource completeGridIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/start-grid-icon.png")
    public ImageResource startGridIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/complete-cal-icon.png")
    public ImageResource completeCalIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/start-cal-icon.png")
    public ImageResource startCalIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/claim-grid-icon.png")
    public ImageResource claimGridIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/release-grid-icon.png")
    public ImageResource releaseGridIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/claim-cal-icon.png")
    public ImageResource claimCalIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/release-cal-icon.png")
    public ImageResource releaseCalIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/popup-grid-icon.png")
    public ImageResource popupIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/details-grid-icon.png")
    public ImageResource detailsIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/grouptask-cal-icon.png")
    public ImageResource groupTaskCalIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/personaltask-cal-icon.png")
    public ImageResource personalTaskCalIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/suboptions-cal-icon.png")
    public ImageResource subOptionsCalIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/suboptionsactive-cal-icon.png")
    public ImageResource subOptionsActiveCalIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/delete.png")
    public ImageResource deleteGridIcon();

    @Source("org/jbpm/console/ng/ht/public/images/icons/edit-grid-icon.png")
    public ImageResource editGridIcon();

}
