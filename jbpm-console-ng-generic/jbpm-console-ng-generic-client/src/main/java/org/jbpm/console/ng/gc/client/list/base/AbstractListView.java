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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

/**
 *
 * @author salaboy
 */
public abstract class AbstractListView<T, V extends AbstractListPresenter> extends Composite implements RequiresResize {

  @Inject
  @DataField
  public LayoutPanel listContainer;

  public ExtendedPagedTable<T> listGrid;

  @Inject
  protected Event<NotificationEvent> notification;

  protected V presenter;
  
  
  public interface ListView<T,V> extends UberView<V> {

    void showBusyIndicator(String message);

    void hideBusyIndicator();

    void displayNotification(String text);

    ExtendedPagedTable<T> getListGrid();

  }
  

  @Inject
  protected PlaceManager placeManager;

  public void init(V presenter){
    this.presenter = presenter;
    listContainer.clear();
    listGrid = new ExtendedPagedTable<T>(10);

    presenter.addDataDisplay(listGrid);
    listContainer.add(listGrid);

    initColumns();
    initGenericToolBar();

  }
  
  @Override
  public void onResize() {
    if ((getParent().getOffsetHeight() - 120) > 0) {
      listContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
    }
  }

  public void displayNotification(String text) {
    notification.fire(new NotificationEvent(text));
  }

  public void initGenericToolBar() {
    Button refreshButton = new Button();
    refreshButton.setIcon(IconType.REFRESH);
    refreshButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.refreshGrid();
      }
    });
    listGrid.getToolbar().add(refreshButton);
  }
  
  public abstract void initColumns();

}
