/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.bd.client.editors.deployment.list;

import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.client.resources.BusinessDomainImages;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.model.events.DeployedUnitChangedEvent;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "DeploymentUnitsListViewImpl.html")
public class DeploymentUnitsListViewImpl extends AbstractListView<KModuleDeploymentUnitSummary, DeploymentUnitsListPresenter>
        implements DeploymentUnitsListPresenter.DeploymentUnitsListView {

  private Constants constants = GWT.create(Constants.class);

  private BusinessDomainImages images = GWT.create(BusinessDomainImages.class);

  @Override
  public void init(final DeploymentUnitsListPresenter presenter) {
    
    super.init(presenter);
    
    Button newUnitButton = new Button();
    newUnitButton.setIcon(IconType.PLUS);
    newUnitButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        placeManager.goTo(new DefaultPlaceRequest("New Deployment"));
      }
    });
    listGrid.getToolbar().add(newUnitButton);

  }

  @Override
  public void initColumns() {
    idColumn();
    groupIdColumn();
    artifactIdColumn();
    versionColumn();
    kbaseColumn();
    ksessionColumn();
    strategyColumn();
    actionsColumn();
  }

  private void idColumn() {
    Column<KModuleDeploymentUnitSummary, String> unitIdColumn = new Column<KModuleDeploymentUnitSummary, String>(
            new TextCell()) {

              @Override
              public String getValue(KModuleDeploymentUnitSummary unit) {
                return unit.getId();
              }
            };
    unitIdColumn.setSortable(true);
    listGrid.addColumn(unitIdColumn, constants.Deployment());

  }

  private void groupIdColumn() {
    Column<KModuleDeploymentUnitSummary, String> groupIdColumn = new Column<KModuleDeploymentUnitSummary, String>(
            new TextCell()) {

              @Override
              public String getValue(KModuleDeploymentUnitSummary unit) {
                return unit.getGroupId();
              }
            };
    groupIdColumn.setSortable(true);

    listGrid.addColumn(groupIdColumn, constants.GroupID());
  }

  private void artifactIdColumn() {
    Column<KModuleDeploymentUnitSummary, String> artifactIdColumn = new Column<KModuleDeploymentUnitSummary, String>(
            new TextCell()) {

              @Override
              public String getValue(KModuleDeploymentUnitSummary unit) {
                return unit.getArtifactId();
              }
            };
    artifactIdColumn.setSortable(true);

    listGrid.addColumn(artifactIdColumn, constants.Artifact());
  }

  private void versionColumn() {
    Column<KModuleDeploymentUnitSummary, String> versionColumn = new Column<KModuleDeploymentUnitSummary, String>(
            new TextCell()) {

              @Override
              public String getValue(KModuleDeploymentUnitSummary unit) {
                return unit.getVersion();
              }
            };
    versionColumn.setSortable(true);
    listGrid.addColumn(versionColumn, constants.Version());

  }

  private void kbaseColumn() {
    Column<KModuleDeploymentUnitSummary, String> kbaseColumn = new Column<KModuleDeploymentUnitSummary, String>(
            new TextCell()) {

              @Override
              public String getValue(KModuleDeploymentUnitSummary unit) {
                String kbaseName = unit.getKbaseName();
                if (kbaseName.equals("")) {
                  kbaseName = "DEFAULT";
                }
                return kbaseName;
              }
            };
    kbaseColumn.setSortable(true);

    listGrid.addColumn(kbaseColumn, constants.KieBaseName());
  }

  private void ksessionColumn() {
    Column<KModuleDeploymentUnitSummary, String> ksessionColumn = new Column<KModuleDeploymentUnitSummary, String>(
            new TextCell()) {

              @Override
              public String getValue(KModuleDeploymentUnitSummary unit) {
                String ksessionName = unit.getKsessionName();
                if (ksessionName.equals("")) {
                  ksessionName = "DEFAULT";
                }
                return ksessionName;
              }
            };
    ksessionColumn.setSortable(true);

    listGrid.addColumn(ksessionColumn, constants.KieSessionName());
  }

  private void strategyColumn() {
    Column<KModuleDeploymentUnitSummary, String> strategyColumn = new Column<KModuleDeploymentUnitSummary, String>(
            new TextCell()) {

              @Override
              public String getValue(KModuleDeploymentUnitSummary unit) {
                return unit.getStrategy();
              }
            };
    strategyColumn.setSortable(true);

    listGrid.addColumn(strategyColumn, constants.Strategy());
  }

  private void actionsColumn() {
    List<HasCell<KModuleDeploymentUnitSummary, ?>> cells = new LinkedList<HasCell<KModuleDeploymentUnitSummary, ?>>();

    cells.add(new DeleteActionHasCell(constants.Undeploy(), new Delegate<KModuleDeploymentUnitSummary>() {
      @Override
      public void execute(KModuleDeploymentUnitSummary unit) {

        if (Window.confirm(constants.Undeploy_Question())) {
          presenter.undeployUnit(unit.getId(), unit.getGroupId(), unit.getArtifactId(), unit.getVersion(),
                  unit.getKbaseName(), unit.getKsessionName());
        }

      }
    }));

    CompositeCell<KModuleDeploymentUnitSummary> cell = new CompositeCell<KModuleDeploymentUnitSummary>(cells);
    Column<KModuleDeploymentUnitSummary, KModuleDeploymentUnitSummary> actionsColumn = new Column<KModuleDeploymentUnitSummary, KModuleDeploymentUnitSummary>(
            cell) {
              @Override
              public KModuleDeploymentUnitSummary getValue(KModuleDeploymentUnitSummary object) {
                return object;
              }
            };
    listGrid.addColumn(actionsColumn, constants.Actions());

  }

  public void refreshOnChangedUnit(@Observes DeployedUnitChangedEvent event) {
    presenter.refreshGrid();
  }


  /* Is this generic enough ?*/
  protected class DeleteActionHasCell implements HasCell<KModuleDeploymentUnitSummary, KModuleDeploymentUnitSummary> {

    private ActionCell<KModuleDeploymentUnitSummary> cell;

    public DeleteActionHasCell(final String text, ActionCell.Delegate<KModuleDeploymentUnitSummary> delegate) {
      cell = new ActionCell<KModuleDeploymentUnitSummary>(text, delegate) {
        @Override
        public void render(Cell.Context context, KModuleDeploymentUnitSummary value, SafeHtmlBuilder sb) {
          String title = constants.Undeploy();
          AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.undeployGridIcon());
          SafeHtmlBuilder mysb = new SafeHtmlBuilder();
          mysb.appendHtmlConstant("<span title='" + title + "' style='margin-right:5px;'>");
          mysb.append(imageProto.getSafeHtml());
          mysb.appendHtmlConstant("</span>");
          sb.append(mysb.toSafeHtml());
        }
      };

    }

    @Override
    public Cell<KModuleDeploymentUnitSummary> getCell() {
      return cell;
    }

    @Override
    public FieldUpdater<KModuleDeploymentUnitSummary, KModuleDeploymentUnitSummary> getFieldUpdater() {
      return null;
    }

    @Override
    public KModuleDeploymentUnitSummary getValue(KModuleDeploymentUnitSummary object) {
      return object;
    }
  }

}
