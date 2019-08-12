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

package org.jbpm.workbench.common.client.filters.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import org.dashbuilder.dataset.DataSetLookup;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.*;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.dataset.DataSetAwareSelect;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.list.DatePickerRange;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.common.client.util.DateRange;
import org.uberfire.client.views.pfly.widgets.DateRangePicker;
import org.uberfire.client.views.pfly.widgets.DateRangePickerOptions;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Moment;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.SanitizedNumberInput;
import org.uberfire.client.views.pfly.widgets.Select;

import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jboss.errai.common.client.dom.Window.getDocument;
import static org.jbpm.workbench.common.client.list.DatePickerRange.getDatePickerRangeFromLabel;
import static org.uberfire.client.views.pfly.widgets.Moment.Builder.moment;

@Dependent
@Templated(stylesheet = "/org/jbpm/workbench/common/client/resources/css/kie-manage.less")
public class BasicFiltersViewImpl implements BasicFiltersView,
                                             IsElement {

    private final Constants constants = Constants.INSTANCE;

    @Inject
    @DataField("content")
    Div content;

    @Inject
    @DataField("filter-list")
    Div filterList;

    @Inject
    @DataField("refine-section")
    Div refineSection;

    @Inject
    @DataField("refine")
    @Named("h5")
    Heading refine;

    @Inject
    @DataField("refine-options")
    Select refineSelect;

    @Inject
    @DataField("filters-input")
    Div filtersInput;

    @Inject
    @DataField("filters-input-help")
    Button filtersInputHelp;

    @Inject
    @DataField("refine-apply")
    Button refineApply;

    @Inject
    @DataField("form")
    Form form;

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Inject
    private ManagedInstance<Select> selectProvider;

    @Inject
    private ManagedInstance<DataSetAwareSelect> dataSetSelectProvider;

    @Inject
    private ManagedInstance<DateRangePicker> dateRangePickerProvider;

    @Inject
    private ManagedInstance<SanitizedNumberInput> sanitizedNumberInput;

    private Map<String, List<Input>> selectInputs = new HashMap<>();

    @PostConstruct
    public void init() {
        refine.setTextContent(constants.FilterBy());

        filtersInputHelp.setAttribute("data-content",
                                      getInputStringHelpHtml());

        jQueryPopover.wrap(filtersInputHelp).popover();

        refineSelect.getElement().addEventListener("change",
                                                   e -> setInputCurrentFilter(refineSelect.getValue()),
                                                   false);

        refineApply.setTextContent(constants.Apply());

    }

    private String getInputStringHelpHtml() {
        return "<p>" + constants.AllowedWildcardsForStrings() + "</p>\n" +
                " <ul>\n" +
                "   <li><code>_</code> - " + constants.ASubstituteForASingleCharacter() + "</li>\n" +
                "   <li><code>%</code> - " + constants.ASubstituteForZeroOrMoreCharacters() + "</li>\n" +
                " </ul>\n";
    }

    @Override
    public HTMLElement getElement() {
        return content;
    }

    @Override
    public void addTextFilter(final String label,
                              final String placeholder,
                              final Consumer<ActiveFilterItem<String>> callback) {

        createFilterOption(label);

        createTextInput(label,
                    placeholder,
                    refineSelect.getOptions().getLength() > 1,
                    input -> input.setType("text"),
                    v -> v,
                    callback);
    }

    @Override
    public void addNumericFilter(final String label,
                                 final String placeholder,
                                 final Consumer<ActiveFilterItem<Integer>> callback) {

        createFilterOption(label);

        createNumberInput(label,
                          placeholder,
                          refineSelect.getOptions().getLength() > 1,
                    input -> {
                        input.setType("number");
                        input.setAttribute("min",
                                           "0");
                    },
                    v -> Integer.valueOf(v),
                          callback);
    }

    @Override
    public void addDataSetSelectFilter(final String label,
                                       final DataSetLookup lookup,
                                       final String textColumnId,
                                       final String valueColumnId,
                                       final Consumer<ActiveFilterItem<String>> callback) {
        final DataSetAwareSelect select = dataSetSelectProvider.get();
        select.setDataSetLookup(lookup);
        select.setTextColumnId(textColumnId);
        select.setValueColumnId(valueColumnId);
        setupSelect(label,
                    select.getSelect(),
                    callback);
    }

    @Override
    public void addDateRangeFilter(final String label,
                                   final String placeholder,
                                   final Boolean useMaxDate,
                                   final Consumer<ActiveFilterItem<DateRange>> callback) {
        final DateRangePicker dateRangePicker = dateRangePickerProvider.get();
        dateRangePicker.getElement().setReadOnly(true);
        dateRangePicker.getElement().setAttribute("placeholder",
                                                  placeholder);
        addCSSClass(dateRangePicker.getElement(),
                    "form-control");
        addCSSClass(dateRangePicker.getElement(),
                    "bootstrap-datepicker");
        final DateRangePickerOptions options = getDateRangePickerOptions(useMaxDate);

        dateRangePicker.setup(options,
                              null);

        dateRangePicker.addApplyListener((e, p) -> {
            final Optional<DatePickerRange> datePickerRange = getDatePickerRangeFromLabel(p.getChosenLabel());
            onDateRangeValueChange(label,
                                   datePickerRange.isPresent() ? datePickerRange.get().getLabel() : constants.Custom(),
                                   datePickerRange.isPresent() ? datePickerRange.get().getStartDate() : p.getStartDate(),
                                   datePickerRange.isPresent() ? datePickerRange.get().getEndDate() : p.getEndDate(),
                                   callback);
        });

        appendHorizontalRule();
        appendSectionTitle(label);

        Div div = (Div) getDocument().createElement("div");
        addCSSClass(div,
                    "input-group");
        addCSSClass(div,
                    "date");

        Span spanGroup = (Span) getDocument().createElement("span");
        addCSSClass(spanGroup,
                    "input-group-addon");

        Span spanIcon = (Span) getDocument().createElement("span");
        addCSSClass(spanIcon,
                    "fa");
        addCSSClass(spanIcon,
                    "fa-calendar");
        spanGroup.appendChild(spanIcon);

        div.appendChild(dateRangePicker.getElement());
        div.appendChild(spanGroup);

        appendFormGroup(div);
    }

    protected DateRangePickerOptions getDateRangePickerOptions(final Boolean useMaxDate) {
        final DateRangePickerOptions options = DateRangePickerOptions.create();
        options.setAutoUpdateInput(false);
        options.setAutoApply(true);
        options.setTimePicker(true);
        options.setDrops("up");
        options.setTimePickerIncrement(30);
        if (useMaxDate) {
            options.setMaxDate(moment().endOf("day"));
        }
        for (DatePickerRange range : DatePickerRange.values()) {
            options.addRange(range.getLabel(),
                             range.getStartDate(),
                             range.getEndDate().endOf("day"));
        }
        return options;
    }

    protected void onDateRangeValueChange(final String label,
                                          final String selectedLabel,
                                          final Moment fromDate,
                                          final Moment toDate,
                                          final Consumer<ActiveFilterItem<DateRange>> callback) {

        final DateRange dateRange = new DateRange(fromDate.milliseconds(0).asDate(),
                                                  toDate.milliseconds(0).asDate());

        final String hint = constants.From() + ": " + fromDate.format("lll") + "<br>" + constants.To() + ": " + toDate.format("lll");
        addActiveFilter(label,
                        selectedLabel,
                        hint,
                        dateRange,
                        callback);
    }

    @Override
    public void addSelectFilter(final String label,
                                final Map<String, String> options,
                                final Consumer<ActiveFilterItem<String>> callback) {
        final Select select = selectProvider.get();
        options.forEach((k, v) -> select.addOption(v,
                                                   k));
        setupSelect(label,
                    select,
                    callback);
    }

    @Override
    public void clearAllSelectFilter() {
        selectInputs.values().forEach(values -> {
            values.forEach(i -> {
                if (i.getChecked()) {
                    i.setChecked(false);
                }
            });
        });
    }

    @Override
    public void checkSelectFilter(final String label,
                                  final String value) {
        selectInputs.computeIfPresent(label,
                                      (key, values) -> {
                                          values.forEach(i -> {
                                              if (i.getValue().equals(value) && i.getChecked() == false) {
                                                  i.setChecked(true);
                                              }
                                          });
                                          return values;
                                      });
    }

    @Override
    public void clearSelectFilter(final String label) {
        selectInputs.computeIfPresent(label,
                                      (key, values) -> {
                                          values.forEach(i -> {
                                              if (i.getChecked()) {
                                                  i.setChecked(false);
                                              }
                                          });
                                          return values;
                                      });
    }

    @Override
    public void addMultiSelectFilter(final String label,
                                     final Map<String, String> options,
                                     final Consumer<ActiveFilterItem<List<String>>> callback) {

        final HTMLElement hr = getDocument().createElement("hr");
        addCSSClass(hr,
                    "kie-dock__divider");
        addCSSClass(hr,
                    "kie-dock__divider_collapse");
        form.insertBefore(hr,
                          form.getFirstChild());

        final Div group = (Div) getDocument().createElement("div");
        addCSSClass(group,
                    "panel-group");
        addCSSClass(group,
                    "kie-dock__panel-group");
        form.insertBefore(group,
                          form.getFirstChild());

        final Div heading = (Div) getDocument().createElement("div");
        addCSSClass(heading,
                    "panel-heading");
        addCSSClass(heading,
                    "kie-dock__panel-heading");
        group.appendChild(heading);

        final Div title = (Div) getDocument().createElement("div");
        addCSSClass(title,
                    "panel-title");
        addCSSClass(title,
                    "kie-dock__heading--section");
        heading.appendChild(title);

        final Anchor anchorTitle = (Anchor) getDocument().createElement("a");
        anchorTitle.setAttribute("data-toggle",
                                 "collapse");
        final String divId = DOM.createUniqueId();
        anchorTitle.setAttribute("data-target",
                                 "#" + divId);
        anchorTitle.setTextContent(label);
        title.appendChild(anchorTitle);

        final Div content = (Div) getDocument().createElement("div");
        addCSSClass(content,
                    "panel-collapse");
        addCSSClass(content,
                    "collapse");
        addCSSClass(content,
                    "in");
        content.setId(divId);
        group.appendChild(content);

        final Div divPanel = (Div) getDocument().createElement("div");
        addCSSClass(divPanel,
                    "panel-body");
        addCSSClass(divPanel,
                    "kie-dock__panel-body");

        content.appendChild(divPanel);

        final Div div = (Div) getDocument().createElement("div");
        addCSSClass(div,
                    "form-group");
        for (Map.Entry<String, String> entry : options.entrySet()) {
            final Label labelElement = (Label) getDocument().createElement("label");
            final Input input = (Input) getDocument().createElement("input");
            input.setType("checkbox");
            input.setValue(entry.getKey());
            input.setAttribute("data-label",
                               entry.getValue());
            input.addEventListener("change",
                                   e -> {
                                       final List<String> values = new ArrayList<>();
                                       final List<String> labels = new ArrayList<>();

                                       selectInputs.get(label).stream().filter(i -> i.getChecked()).forEach(i -> {
                                           values.add(i.getValue());
                                           labels.add(i.getAttribute("data-label"));
                                       });

                                       addActiveFilter(label,
                                                       String.join(", ",
                                                                   labels),
                                                       null,
                                                       values,
                                                       callback);
                                   },
                                   false);
            selectInputs.computeIfAbsent(label,
                                         key -> new ArrayList<Input>());
            selectInputs.get(label).add(input);
            labelElement.appendChild(input);
            labelElement.appendChild(getDocument().createTextNode(entry.getValue()));
            final Div checkBoxDiv = (Div) getDocument().createElement("div");
            addCSSClass(checkBoxDiv,
                        "checkbox");
            checkBoxDiv.appendChild(labelElement);
            div.appendChild(checkBoxDiv);
        }

        divPanel.appendChild(div);
    }

    @Override
    public void hideFilterBySection() {
        addCSSClass(refineSection,
                    "hidden");
    }

    private void setupSelect(final String label,
                             final Select select,
                             final Consumer<ActiveFilterItem<String>> callback) {
        appendHorizontalRule();
        appendSectionTitle(label);
        select.setTitle(constants.Select());
        select.setWidth("100%");
        addCSSClass(select.getElement(),
                    "selectpicker");
        addCSSClass(select.getElement(),
                    "form-control");

        select.getElement().addEventListener("change",
                                             event -> {
                                                 if (select.getValue().isEmpty() == false) {
                                                     final OptionsCollection options = select.getOptions();
                                                     for (int i = 0; i < options.getLength(); i++) {
                                                         final Option item = (Option) options.item(i);
                                                         if (item.getSelected()) {
                                                             addActiveFilter(label,
                                                                             item.getText(),
                                                                             null,
                                                                             select.getValue(),
                                                                             callback);
                                                             select.setValue("");
                                                             break;
                                                         }
                                                     }
                                                 }
                                             },
                                             false);

        appendFormGroup(select.getElement());
        select.refresh();
    }

    private void appendFormGroup(final HTMLElement element) {
        Div div = (Div) getDocument().createElement("div");
        addCSSClass(div,
                    "form-group");
        div.appendChild(element);
        filterList.appendChild(div);
    }

    private void appendHorizontalRule() {
        final HTMLElement hr = getDocument().createElement("hr");
        addCSSClass(hr,
                    "kie-dock__divider");

        filterList.appendChild(hr);
    }

    private void appendSectionTitle(final String title) {
        final Heading heading = (Heading) getDocument().createElement("h5");
        heading.setTextContent(title);
        addCSSClass(heading,
                    "kie-dock__heading--section");
        filterList.appendChild(heading);
    }

    private <T extends Object> void createNumberInput(final String label,
                                                      final String placeholder,
                                                      final Boolean hidden,
                                                      final Consumer<Input> customizeCallback,
                                                      final Function<String, T> valueMapper,
                                                      final Consumer<ActiveFilterItem<T>> callback) {
        final SanitizedNumberInput numberInput = sanitizedNumberInput.get();
        numberInput.init();
        Input input = numberInput.getElement();

        createInput(label,
                    input,
                    placeholder,
                    hidden,
                    customizeCallback,
                    valueMapper,
                    callback);
    }

    private <T extends Object> void createInput(final String label,
                                                final Input input,
                                                final String placeholder,
                                                final Boolean hidden,
                                                final Consumer<Input> customizeCallback,
                                                final Function<String, T> valueMapper,
                                                final Consumer<ActiveFilterItem<T>> callback) {
        customizeCallback.accept(input);
        input.setAttribute("placeholder",
                           placeholder);
        input.setAttribute("data-filter",
                           label);
        addCSSClass(input,
                    "form-control");
        addCSSClass(input,
                    "filter-control");
        if (hidden) {
            addCSSClass(input,
                        "hidden");
        }
        input.setOnkeypress((KeyboardEvent e) -> {
            if ((e == null || e.getKeyCode() == KeyCodes.KEY_ENTER) && input.getValue().isEmpty() == false) {
                addActiveFilter(label,
                                input.getValue(),
                                null,
                                valueMapper.apply(input.getValue()),
                                callback);
                input.setValue("");
            }
        });
        filtersInput.insertBefore(input,
                                  filtersInput.getFirstChild());
    }

    private <T extends Object> void createTextInput(final String label,
                                                    final String placeholder,
                                                    final Boolean hidden,
                                                    final Consumer<Input> customizeCallback,
                                                    final Function<String, T> valueMapper,
                                                    final Consumer<ActiveFilterItem<T>> callback) {
        final Input input = (Input) getDocument().createElement("input");

        createInput(label,
                    input,
                    placeholder,
                    hidden,
                    customizeCallback,
                    valueMapper,
                    callback);
    }

    private void createFilterOption(final String label) {

        refineSelect.addOption(label);
        refineSelect.refresh();
    }

    private void setInputCurrentFilter(final String label) {
        for (Element child : elementIterable(filtersInput.getChildNodes())) {
            if (child.getTagName().equals("INPUT")) {
                if (label.equals(child.getAttribute("data-filter"))) {
                    removeCSSClass((HTMLElement) child,
                                   "hidden");
                } else {
                    addCSSClass((HTMLElement) child,
                                "hidden");
                }
            }
        }
    }

    @EventHandler("refine-apply")
    public void onApplyClick(@ForEvent("click") Event e) {
        for (Element child : elementIterable(filtersInput.getChildNodes())) {
            if (child.getTagName().equals("INPUT")) {
                Input input = (Input) child;
                if (input.getClassList().contains("hidden") == false) {
                    input.getOnkeypress().call(null);
                    break;
                }
            }
        }
    }

    protected <T extends Object> void addActiveFilter(final String labelKey,
                                                      final String labelValue,
                                                      final String hint,
                                                      final T value,
                                                      final Consumer<ActiveFilterItem<T>> callback) {
        if (callback != null) {
            callback.accept(new ActiveFilterItem(labelKey,
                                                 labelKey + ": " + labelValue,
                                                 hint,
                                                 value,
                                                 null));
        }
    }

    protected Map<String, List<Input>> getSelectInputs() {
        return selectInputs;
    }
}
