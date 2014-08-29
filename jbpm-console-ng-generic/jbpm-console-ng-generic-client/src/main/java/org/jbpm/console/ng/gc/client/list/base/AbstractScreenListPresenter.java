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
package org.jbpm.console.ng.gc.client.list.base;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;

/**
 * @param <T> data type for the AsyncDataProvider
 * @author salaboy
 */
public abstract class AbstractScreenListPresenter<T> extends AbstractListPresenter<T> {

    @Inject
    protected Identity identity;

    @Inject
    protected PlaceManager placeManager;

    protected PlaceRequest place;

    @Inject
    protected Event<BeforeClosePlaceEvent> beforeCloseEvent;

    @OnOpen
    public void onOpen() {
        refreshGrid();
    }

    @OnFocus
    public void onFocus() {
        refreshGrid();
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnClose
    public void onClose() {
        beforeCloseEvent.fire( new BeforeClosePlaceEvent( place ) );
    }

}
