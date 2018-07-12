/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.es.client.editors.quicknewjob;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.MouseEvent;

import org.jboss.errai.common.client.dom.NumberInput;
import org.jboss.errai.common.client.dom.RadioInput;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.DateUtils;

import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestParameterSummary;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.DateRangePicker;
import org.uberfire.client.views.pfly.widgets.DateRangePickerOptions;
import org.uberfire.client.views.pfly.widgets.FormGroup;
import org.uberfire.client.views.pfly.widgets.FormLabel;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.client.views.pfly.widgets.ValidationState;
import org.uberfire.ext.widgets.common.client.tables.ResizableHeader;
import org.uberfire.ext.widgets.table.client.DataGrid;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.jboss.errai.common.client.dom.Window.getDocument;
import static org.uberfire.client.views.pfly.widgets.Moment.Builder.moment;

@Dependent
@Templated
public class NewJobViewImpl implements NewJobPresenter.NewJobView,
                                       UberElement<NewJobPresenter> {

    private final Constants constants = Constants.INSTANCE;

    @Inject
    @DataField("basic-tab")
    ListItem basicTab;

    @Inject
    @DataField("basic-pane")
    private Div basicPane;

    @Inject
    @DataField("advanced-tab")
    ListItem advancedTab;

    @Inject
    @DataField("advanced-pane")
    private Div advancedPane;

    @Inject
    @DataField("advanced-content")
    private FlowPanel advancedContent;

    @Inject
    @DataField("job-name-label")
    private FormLabel jobNameLabel;

    @Inject
    @DataField("job-name-input")
    private TextInput jobNameInput;

    @Inject
    @DataField("job-name-group")
    private FormGroup jobNameGroup;

    @Inject
    @DataField("job-name-help")
    private Span jobNameHelp;

    @Inject
    @DataField("date-filters-input")
    Div dateFiltersInput;

    @Inject
    @DataField("run-now")
    private RadioInput jobRunNowRadio;

    @Inject
    @DataField("job-type-label")
    private FormLabel jobTypeLabel;

    @Inject
    @DataField("job-type-input")
    private TextInput jobTypeInput;

    @Inject
    @DataField("job-type-group")
    private FormGroup jobTypeGroup;

    @Inject
    @DataField("job-type-help")
    private Span jobTypeHelp;

    @Inject
    @DataField("job-retries-label")
    private FormLabel jobRetriesLabel;

    @Inject
    @DataField("job-retries-input")
    private NumberInput jobRetriesInput;

    @Inject
    @DataField("job-retries-group")
    private FormGroup jobRetriesGroup;

    @Inject
    @DataField("job-retries-help")
    private Span jobRetriesHelp;

    @Inject
    @DataField("modal")
    private Modal modal;

    @Inject
    @DataField("alert")
    private InlineNotification inlineNotification;

    @Inject
    private ManagedInstance<DateRangePicker> dateRangePickerProvider;

    private DateRangePicker dateRangePicker;

    private Date selectedDate;

    protected NewJobPresenter presenter;

    public void init(NewJobPresenter presenter) {
        this.presenter = presenter;
    }

    private ListDataProvider<RequestParameterSummary> dataProvider = new ListDataProvider<RequestParameterSummary>();

    public DataGrid<RequestParameterSummary> myParametersGrid = new DataGrid<RequestParameterSummary>();

    private boolean redrawParametersGrid;

    @PostConstruct
    public void init() {
        jobNameLabel.addRequiredIndicator();
        jobTypeLabel.addRequiredIndicator();
        jobRetriesLabel.addRequiredIndicator();

        jobRetriesInput.setType("number");
        jobRetriesInput.setAttribute("min",
                                     "0");
        jobRetriesInput.setDefaultValue("0");
        jobRetriesInput.addEventListener("keypress",
                                         getNumericInputListener(),
                                         false);

        myParametersGrid.setHeight("200px");
        myParametersGrid.setEmptyTableWidget(new Label(constants.No_Parameters_added_yet()));

        initGridColumns();
        initDateTimePicker();

        Button button = GWT.create(Button.class);
        button.setText(Constants.INSTANCE.Add_Parameter());
        button.addClickHandler(e -> addNewParameter());
        advancedContent.add(myParametersGrid);
        advancedContent.add(button);
        inlineNotification.setType(InlineNotification.InlineNotificationType.DANGER);
    }

    protected EventListener<KeyboardEvent> getNumericInputListener() {
        return (KeyboardEvent e) -> {
            int keyCode = e.getKeyCode();
            if (keyCode <= 0) { //getKeyCode() returns 0 for numbers on Firefox 53
                keyCode = e.getWhich();
            }
            if (!((keyCode >= KeyCodes.KEY_NUM_ZERO && keyCode <= KeyCodes.KEY_NUM_NINE) ||
                    (keyCode >= KeyCodes.KEY_ZERO && keyCode <= KeyCodes.KEY_NINE) ||
                    (keyCode == KeyCodes.KEY_BACKSPACE || keyCode == KeyCodes.KEY_LEFT || keyCode == KeyCodes.KEY_RIGHT))) {
                e.preventDefault();
            }
        };
    }

    public void show() {
        cleanForm();
        modal.show();
        redrawParametersGrid = true;
    }

    protected void initDateTimePicker() {
        this.dateRangePicker = dateRangePickerProvider.get();
        dateRangePicker.getElement().setReadOnly(true);
        dateRangePicker.getElement().setAttribute("placeholder",
                                                  Constants.INSTANCE.Due_On());
        dateRangePicker.getElement().getClassList().add("form-control");

        final Div div = (Div) getDocument().createElement("div");
        div.setAttribute("data-filter",
                         "Date");
        div.getClassList().add("input-group");
        div.getClassList().add("filter-control");
        div.appendChild(dateRangePicker.getElement());
        dateFiltersInput.appendChild(div);
        dateFiltersInput.setHidden(true);
    }

    protected void resetDateTimePicker() {
        final DateRangePickerOptions options = getDateRangePickerOptions();
        dateRangePicker.setup(options,
                              null);
        dateRangePicker.addApplyListener((e, p) -> {
            selectedDate = p.getStartDate().milliseconds(0).asDate();
            dateRangePicker.getElement().setAttribute("placeholder",
                                                      DateUtils.getDateTimeStr(selectedDate));
        });
    }

    protected DateRangePickerOptions getDateRangePickerOptions() {
        final DateRangePickerOptions options = DateRangePickerOptions.create();
        options.setAutoUpdateInput(false);
        options.setAutoApply(true);
        options.setTimePicker(true);
        options.setTimePickerIncrement(1);
        options.setSingleDatePicker(true);
        options.setMinDate(moment());
        options.setParentEl("[data-field='modal']");

        return options;
    }

    public void cleanForm() {
        showBasicPane();

        selectedDate = new Date();
        jobRunNowRadio.setChecked(true);
        dateFiltersInput.setHidden(true);
        jobNameInput.setValue("");
        jobTypeInput.setValue("");
        jobRetriesInput.setValue("0");

        dataProvider.getList().clear();
        cleanErrorMessages();
    }

    @Override
    public void showBasicPane() {
        addCSSClass(basicTab,
                    "active");
        addCSSClass(basicPane,
                    "active");
        basicPane.setHidden(false);

        removeCSSClass(advancedTab,
                       "active");
        removeCSSClass(advancedPane,
                       "active");
        advancedPane.setHidden(true);
    }

    public void cleanErrorMessages() {
        jobNameGroup.clearValidationState();
        jobNameHelp.setTextContent("");
        jobTypeGroup.clearValidationState();
        jobTypeHelp.setTextContent("");
        jobRetriesGroup.clearValidationState();
        jobRetriesHelp.setTextContent("");
        addCSSClass(inlineNotification.getElement(),
                    "hidden");
    }

    public void showInlineNotification(final String messages) {
        if (messages.isEmpty()) {
            return;
        }

        inlineNotification.setMessage(messages);
        removeCSSClass(inlineNotification.getElement(),
                       "hidden");
    }

    @Override
    public void showEmptyNameErrorMessage() {
        jobNameGroup.setValidationState(ValidationState.ERROR);
        jobNameHelp.setTextContent(Constants.INSTANCE.The_Job_Must_Have_A_BusinessKey());
    }

    @Override
    public void showInvalidTypeErrorMessage(){
        jobTypeGroup.setValidationState(ValidationState.ERROR);
        jobTypeHelp.setTextContent(Constants.INSTANCE.The_Job_Must_Have_A_Valid_Type());
    }

    @Override
    public void showEmptyTypeErrorMessage() {
        jobTypeGroup.setValidationState(ValidationState.ERROR);
        jobTypeHelp.setTextContent(Constants.INSTANCE.The_Job_Must_Have_A_Type());
    }

    @Override
    public void showEmptyRetriesErrorMessage() {
        jobRetriesGroup.setValidationState(ValidationState.ERROR);
        jobRetriesHelp.setTextContent(Constants.INSTANCE.The_Job_Must_Have_A_Positive_Number_Of_Reties());
    }

    public void removeRow(int index) {
        dataProvider.getList().remove(index);
    }

    public void addRow(RequestParameterSummary parameter) {
        dataProvider.getList().add(parameter);
    }

    private void initGridColumns() {
        Column<RequestParameterSummary, String> paramKeyColumn = new Column<RequestParameterSummary, String>(new EditTextCell()) {
            @Override
            public String getValue(RequestParameterSummary rowObject) {
                return rowObject.getKey();
            }
        };
        paramKeyColumn.setFieldUpdater(new FieldUpdater<RequestParameterSummary, String>() {
            @Override
            public void update(int index,
                               RequestParameterSummary object,
                               String value) {
                object.setKey(value);
                dataProvider.getList().set(index,
                                           object);
            }
        });
        myParametersGrid.addColumn(paramKeyColumn,
                                   new ResizableHeader<RequestParameterSummary>(constants.Key(),
                                                                                myParametersGrid,
                                                                                paramKeyColumn));

        Column<RequestParameterSummary, String> paramValueColumn = new Column<RequestParameterSummary, String>(new EditTextCell()) {
            @Override
            public String getValue(RequestParameterSummary rowObject) {
                return rowObject.getValue();
            }
        };
        paramValueColumn.setFieldUpdater(new FieldUpdater<RequestParameterSummary, String>() {
            @Override
            public void update(int index,
                               RequestParameterSummary object,
                               String value) {
                object.setValue(value);
                dataProvider.getList().set(index,
                                           object);
            }
        });
        myParametersGrid.addColumn(paramValueColumn,
                                   new ResizableHeader<RequestParameterSummary>(constants.Value(),
                                                                                myParametersGrid,
                                                                                paramValueColumn));

        // actions (icons)
        final ButtonCell buttonCell = new ButtonCell(ButtonType.DANGER,
                                                     IconType.TRASH);
        final Column<RequestParameterSummary, String> actionsColumn = new Column<RequestParameterSummary, String>(buttonCell) {
            @Override
            public String getValue(final RequestParameterSummary object) {
                return Constants.INSTANCE.Remove();
            }
        };
        actionsColumn.setFieldUpdater(new FieldUpdater<RequestParameterSummary, String>() {
            @Override
            public void update(int index,
                               RequestParameterSummary object,
                               String value) {
                removeRow(index);
            }
        });
        actionsColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        myParametersGrid.addColumn(actionsColumn,
                                   constants.Actions());
        myParametersGrid.setColumnWidth(actionsColumn,
                                        105,
                                        Style.Unit.PX);
        dataProvider.addDataDisplay(myParametersGrid);
    }

    public void addNewParameter() {
        addRow(new RequestParameterSummary(constants.ClickToEdit(),
                                           constants.ClickToEdit()));
    }

    public void hide() {
        modal.hide();
    }

    public HTMLElement getElement() {
        return modal.getElement();
    }

    @EventHandler("run-now")
    public void onRunNow(@ForEvent("change") final Event event) {
        selectedDate = new Date();
        dateFiltersInput.setHidden(true);
    }

    @EventHandler("run-later")
    public void onRunLater(@ForEvent("change") final Event event) {
        dateFiltersInput.setHidden(false);
        dateRangePicker.getElement().setAttribute("placeholder",
                                                  DateUtils.getDateTimeStr(selectedDate));
        resetDateTimePicker();
    }

    @EventHandler("start")
    public void onCreateClick(final @ForEvent("click") MouseEvent event) {
        presenter.createJob(jobNameInput.getValue(),
                            selectedDate,
                            jobTypeInput.getValue(),
                            jobRetriesInput.getValue(),
                            dataProvider.getList());
    }

    @EventHandler("cancel")
    public void onCancelClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }

    @EventHandler("close")
    public void onCloseClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }

    // JBPM-6785: when the modal is shown force redraw to show empty table widget
    @EventHandler("advanced-tab")
    public void onAdvancedTabMouseUp(final @ForEvent("mouseup") MouseEvent event) {
        if (redrawParametersGrid) {
            myParametersGrid.redraw();
            redrawParametersGrid = false;
        }
    }
}
