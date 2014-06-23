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

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ga.model.events.SearchEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;

/**
 *
 * @author salaboy
 */
public abstract class AbstractListPresenter<T> {

  protected AsyncDataProvider<T> dataProvider;

  protected QueryFilter currentFilter;
  
  @Inject
  protected PlaceManager placeManager;
  
  public void addDataDisplay(final HasData<T> display) {
    dataProvider.addDataDisplay(display);
  }

  public void refreshGrid() {
    HasData<T> next = dataProvider.getDataDisplays().iterator().next();
    next.setVisibleRangeAndClearData(next.getVisibleRange(), true);
  }

  protected void onSearchEvent(@Observes SearchEvent searchEvent) {
    String filterString = searchEvent.getFilter();
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("textSearch", filterString.toLowerCase());
    currentFilter.setParams(params);
    HasData<T> next = dataProvider.getDataDisplays().iterator().next();
    if(filterString.equals("")){
      next.setVisibleRangeAndClearData(next.getVisibleRange(), true);
    }else{
      next.setVisibleRangeAndClearData(new Range(0, next.getVisibleRange().getLength()), true);
    }

  }

  @OnOpen
  public void onOpen() {
    refreshGrid();
  }

  @OnFocus
  public void onFocus() {
    refreshGrid();
  }
}
