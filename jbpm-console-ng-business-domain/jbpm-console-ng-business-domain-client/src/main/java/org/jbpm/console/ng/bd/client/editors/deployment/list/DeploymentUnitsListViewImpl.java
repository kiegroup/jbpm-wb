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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.client.resources.BusinessDomainImages;
import org.jbpm.console.ng.bd.client.util.ResizableHeader;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.model.events.DeployedUnitChangedEvent;
import org.jbpm.console.ng.gc.client.list.base.BaseViewImpl;
import org.jbpm.console.ng.gc.client.util.DataGridUtils;
import org.uberfire.client.common.BusyPopup;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.SelectionChangeEvent;

@Dependent
@Templated(value = "DeploymentUnitsListViewImpl.html")
public class DeploymentUnitsListViewImpl extends BaseViewImpl<KModuleDeploymentUnitSummary, DeploymentUnitsListPresenter>
        implements DeploymentUnitsListPresenter.DeploymentUnitsListView {

    private static final String DEPLOYMENT_CONFIRM = "Are you sure that you want to undeploy the deployment unit?";

    private static final String ALL_DEPLOYMENT_CONFIRM = "Are you sure that you want to undeploy all the deployments selected?";

    private Constants constants = GWT.create(Constants.class);

    private BusinessDomainImages images = GWT.create(BusinessDomainImages.class);

    @Override
    public void init(final DeploymentUnitsListPresenter presenter) {
        super.DELETE_ACTION_IMAGE = images.undeployGridIcon();
        super.MSJ_NO_ITEMS_FOUND = constants.No_Deployment_Units_Available();
        super.initializeComponents(presenter, presenter.getDataProvider(), GridSelectionModel.MULTI);
    }

    @Override
    public void initGridColumns() {
        this.idColumn();
        this.groupIdColumn();
        this.artifactIdColumn();
        this.versionColumn();
        this.kbaseColumn();
        this.ksessionColumn();
        this.strategyColumn();
        this.actionsColumn();
    }

    private void idColumn() {
        Column<KModuleDeploymentUnitSummary, String> unitIdColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, KModuleDeploymentUnitSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getId();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(KModuleDeploymentUnitSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getId());
            }
        };
        unitIdColumn.setSortable(true);
        sortHandler.setComparator(unitIdColumn, new Comparator<KModuleDeploymentUnitSummary>() {
            @Override
            public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        listGrid.addColumn(unitIdColumn, new ResizableHeader(constants.Deployment(), 100, listGrid, unitIdColumn));
        listGrid.setColumnWidth(unitIdColumn, "300px");
    }

    private void groupIdColumn() {
        Column<KModuleDeploymentUnitSummary, String> groupIdColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, KModuleDeploymentUnitSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getGroupId();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(KModuleDeploymentUnitSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getGroupId());
            }
        };
        groupIdColumn.setSortable(true);
        sortHandler.setComparator(groupIdColumn, new Comparator<KModuleDeploymentUnitSummary>() {
            @Override
            public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
                return o1.getGroupId().compareTo(o2.getGroupId());
            }
        });
        listGrid.addColumn(groupIdColumn, new ResizableHeader(constants.GroupID(), 100, listGrid, groupIdColumn));
    }

    private void artifactIdColumn() {
        Column<KModuleDeploymentUnitSummary, String> artifactIdColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, KModuleDeploymentUnitSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getArtifactId();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(KModuleDeploymentUnitSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getArtifactId());
            }
        };
        artifactIdColumn.setSortable(true);
        sortHandler.setComparator(artifactIdColumn, new Comparator<KModuleDeploymentUnitSummary>() {
            @Override
            public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
                return o1.getArtifactId().compareTo(o2.getArtifactId());
            }
        });
        listGrid.addColumn(artifactIdColumn, new ResizableHeader(constants.Artifact(), 100, listGrid, artifactIdColumn));
    }

    private void versionColumn() {
        Column<KModuleDeploymentUnitSummary, String> versionColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, KModuleDeploymentUnitSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getVersion();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(KModuleDeploymentUnitSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getVersion());
            }
        };
        versionColumn.setSortable(true);
        sortHandler.setComparator(versionColumn, new Comparator<KModuleDeploymentUnitSummary>() {
            @Override
            public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
                return o1.getVersion().compareTo(o2.getVersion());
            }
        });
        listGrid.addColumn(versionColumn, new ResizableHeader(constants.Version(), 50, listGrid, versionColumn));
        listGrid.setColumnWidth(versionColumn, "75px");
    }

    private void kbaseColumn() {
        Column<KModuleDeploymentUnitSummary, String> kbaseColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell()) {

            @Override
            public void render(Cell.Context context, KModuleDeploymentUnitSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getKbaseName();
                sb.append(DataGridUtils.createDivStart(title, "DEFAULT"));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(KModuleDeploymentUnitSummary unit) {
                String kbaseName = unit.getKbaseName();
                if (kbaseName.equals("")) {
                    kbaseName = "DEFAULT";
                }
                return DataGridUtils.trimToColumnWidth(listGrid, this, kbaseName);
            }
        };
        kbaseColumn.setSortable(true);
        sortHandler.setComparator(kbaseColumn, new Comparator<KModuleDeploymentUnitSummary>() {
            @Override
            public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
                return o1.getKbaseName().compareTo(o2.getKbaseName());
            }
        });
        listGrid.addColumn(kbaseColumn, new ResizableHeader(constants.KieBaseName(), 75, listGrid, kbaseColumn));
    }

    private void ksessionColumn() {
        Column<KModuleDeploymentUnitSummary, String> ksessionColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell()) {

            public void render(Cell.Context context, KModuleDeploymentUnitSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getKsessionName();
                sb.append(DataGridUtils.createDivStart(title, "DEFAULT"));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(KModuleDeploymentUnitSummary unit) {
                String ksessionName = unit.getKsessionName();
                if (ksessionName.equals("")) {
                    ksessionName = "DEFAULT";
                }
                return DataGridUtils.trimToColumnWidth(listGrid, this, ksessionName);
            }
        };
        ksessionColumn.setSortable(true);
        sortHandler.setComparator(ksessionColumn, new Comparator<KModuleDeploymentUnitSummary>() {
            @Override
            public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
                return o1.getKsessionName().compareTo(o2.getKsessionName());
            }
        });
        listGrid.addColumn(ksessionColumn, new ResizableHeader(constants.KieSessionName(), 75, listGrid, ksessionColumn));
    }

    private void strategyColumn() {
        Column<KModuleDeploymentUnitSummary, String> strategyColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell()) {

            public void render(Cell.Context context, KModuleDeploymentUnitSummary unit, SafeHtmlBuilder sb) {
                String title = unit.getStrategy();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, unit, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue(KModuleDeploymentUnitSummary unit) {
                return DataGridUtils.trimToColumnWidth(listGrid, this, unit.getStrategy());
            }
        };
        strategyColumn.setSortable(true);
        sortHandler.setComparator(strategyColumn, new Comparator<KModuleDeploymentUnitSummary>() {
            @Override
            public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
                return o1.getStrategy().compareTo(o2.getStrategy());
            }
        });
        listGrid.addColumn(strategyColumn, new ResizableHeader(constants.Strategy(), 75, listGrid, strategyColumn));
    }

    private void actionsColumn() {
        List<HasCell<KModuleDeploymentUnitSummary, ?>> cells = new LinkedList<HasCell<KModuleDeploymentUnitSummary, ?>>();

        cells.add(new DeleteActionHasCell(constants.Undeploy(), new Delegate<KModuleDeploymentUnitSummary>() {
            @Override
            public void execute(KModuleDeploymentUnitSummary unit) {
                if (itemsSelected != null && itemsSelected.size() > 1) {
                    if (Window.confirm(ALL_DEPLOYMENT_CONFIRM)) {
                        for (KModuleDeploymentUnitSummary item : itemsSelected) {
                            // TODO it should call a new method with a List
                            // param
                            presenter.undeployUnit(item.getId(), item.getGroupId(), item.getArtifactId(), item.getVersion(),
                                    item.getKbaseName(), item.getKsessionName());
                        }
                        setMultiSelectionModel();
                    }
                } else {
                    if (Window.confirm(DEPLOYMENT_CONFIRM)) {
                        presenter.undeployUnit(unit.getId(), unit.getGroupId(), unit.getArtifactId(), unit.getVersion(),
                                unit.getKbaseName(), unit.getKsessionName());
                    }
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
        listGrid.addColumn(actionsColumn, new ResizableHeader(constants.Actions(), listGrid, actionsColumn));
        listGrid.setColumnWidth(actionsColumn, "70px");
    }

    public void refreshOnChangedUnit(@Observes DeployedUnitChangedEvent event) {
        refreshItems();
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void refreshItems() {
        presenter.refreshItems();
    }

    @Override
    public void multiSelectionModelChange(SelectionChangeEvent event, Set<KModuleDeploymentUnitSummary> selectedKieSession) {
        for (KModuleDeploymentUnitSummary unit : selectedKieSession) {
            //
        }
    }

    @Override
    public void simpleSelectionModelChange(SelectionChangeEvent event, KModuleDeploymentUnitSummary selectedItemSelectionModel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setGridEvents() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initializeLeftButtons() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initializeRightButtons() {
        // TODO Auto-generated method stub
    }

    @Override
    public void addHandlerPager() {
        // TODO Auto-generated method stub
    }
}