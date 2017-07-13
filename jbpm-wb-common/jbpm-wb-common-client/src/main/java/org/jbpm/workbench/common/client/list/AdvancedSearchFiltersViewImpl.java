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

package org.jbpm.workbench.common.client.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.Composite;
import org.dashbuilder.dataset.DataSetLookup;
import org.jboss.errai.common.client.dom.*;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.*;
import org.jbpm.workbench.common.client.dataset.DataSetAwareSelect;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.common.client.util.DateRange;
import org.uberfire.client.views.pfly.widgets.DateRangePicker;
import org.uberfire.client.views.pfly.widgets.DateRangePickerOptions;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Moment;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.Select;

import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jboss.errai.common.client.dom.Window.getDocument;
import static org.jbpm.workbench.common.client.list.DatePickerRange.getDatePickerRangeFromLabel;
import static org.uberfire.client.views.pfly.widgets.Moment.Builder.moment;

@Dependent
@Templated
public class AdvancedSearchFiltersViewImpl extends Composite implements AdvancedSearchFiltersView {

    private final Constants constants = Constants.INSTANCE;

    @Inject
    @DataField("dropdown-filter-text")
    Span filterText;

    @Inject
    @DataField("date-dropdown-filter-text")
    Span dateFilterText;

    @Inject
    @DataField("active-filters-text")
    Span activeFiltersText;

    @Inject
    @DataField("filters-input")
    Div filtersInput;

    @Inject
    @DataField("filters-input-help")
    Anchor filtersInputHelp;

    @Inject
    @DataField("date-filters-input")
    Div dateFiltersInput;

    @Inject
    @DataField("remove-all-filters")
    Anchor removeAll;

    @Inject
    @DataField("filters")
    UnorderedList filters;

    @Inject
    @DataField("date-filters")
    UnorderedList dateFilters;

    @Inject
    @DataField("active-filters")
    @ListContainer("ul")
    @Bound
    private ListComponent<ActiveFilterItem, ActiveFilterItemView> activeFilters;

    @Inject
    @AutoBound
    private DataBinder<List<ActiveFilterItem>> activeFiltersList;

    @Inject
    @DataField("select-filters")
    private Div selectFilters;

    @Inject
    @DataField("date-caret")
    private Span dateCaret;

    @Inject
    @DataField("date-button")
    private Button dateButton;

    @Inject
    private ManagedInstance<Select> selectProvider;

    @Inject
    private ManagedInstance<DateRangePicker> dateRangePickerProvider;

    @Inject
    private ManagedInstance<DataSetAwareSelect> dataSetSelectProvider;

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @PostConstruct
    public void init() {
        removeAll.setTextContent(constants.ClearAll());
        activeFiltersText.setTextContent(constants.ActiveFilters());
        activeFiltersList.setModel(new ArrayList<>());
        activeFilters.addComponentCreationHandler(v -> removeCSSClass(removeAll,
                                                                      "hidden"));
        activeFilters.addComponentDestructionHandler(v -> {
            if (activeFiltersList.getModel().isEmpty()) {
                addCSSClass(removeAll,
                            "hidden");
            }
            v.getValue().getCallback().accept(v.getValue().getValue());
        });

        filtersInputHelp.setAttribute("data-content",
                                      getInputStringHelpHtml());

        jQueryPopover.wrap(filtersInputHelp).popover();
    }

    private String getInputStringHelpHtml() {
        return "<p>" + constants.AllowedWildcardsForStrings() + "</p>\n" +
                " <ul>\n" +
                "   <li><code>_</code> - " + constants.ASubstituteForASingleCharacter() + "</li>\n" +
                "   <li><code>%</code> - " + constants.ASubstituteForZeroOrMoreCharacters() + "</li>\n" +
                "   <li><code>[" + constants.CharList() + "]</code> - " + constants.SetOfCharactersToMatch() + "</li>\n" +
                "   <li><code>[^" + constants.CharList() + "]</code> - " + constants.MatchesOnlyACharacterNOTSpecifiedWithinTheBrackets() + "</li>\n" +
                " </ul>\n";
    }

    @Override
    public void addTextFilter(final String label,
                              final String placeholder,
                              final Consumer<String> addCallback,
                              final Consumer<String> removeCallback) {

        removeCSSClass(filtersInputHelp,
                       "hidden");
        createFilterOption(label,
                           filters,
                           e -> setInputCurrentFilter(label));
        createInput(label,
                    placeholder,
                    input -> input.setType("text"),
                    addCallback,
                    removeCallback);
    }

    @Override
    public void addNumericFilter(final String label,
                                 final String placeholder,
                                 final Consumer<String> addCallback,
                                 final Consumer<String> removeCallback) {
        createFilterOption(label,
                           filters,
                           e -> setInputCurrentFilter(label));
        createInput(label,
                    placeholder,
                    input -> {
                        input.setType("number");
                        input.setAttribute("min",
                                           "0");
                        input.addEventListener("keypress",
                                               getNumericInputListener(),
                                               false);
                    },
                    addCallback,
                    removeCallback);
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

    @Override
    public void addDataSetSelectFilter(final String label,
                                       final String tableKey,
                                       final DataSetLookup lookup,
                                       final String textColumnId,
                                       final String valueColumnId,
                                       final Consumer<String> addCallback,
                                       final Consumer<String> removeCallback) {
        final DataSetAwareSelect select = dataSetSelectProvider.get();
        select.setDataSetLookup(lookup);
        select.setTextColumnId(textColumnId);
        select.setValueColumnId(valueColumnId);
        select.setTableKey(tableKey);
        setupSelect(label,
                    false,
                    select.getSelect(),
                    addCallback,
                    removeCallback);
    }

    @Override
    public void addDateRangeFilter(final String label,
                                   final String placeholder,
                                   final Consumer<DateRange> addCallback,
                                   final Consumer<DateRange> removeCallback) {
        final DateRangePicker dateRangePicker = dateRangePickerProvider.get();
        dateRangePicker.getElement().setReadOnly(true);
        dateRangePicker.getElement().setAttribute("placeholder",
                                                  placeholder);
        dateRangePicker.getElement().getClassList().add("form-control");
        final DateRangePickerOptions options = getDateRangePickerOptions();

        dateRangePicker.setup(options,
                              null);

        dateRangePicker.addApplyListener((e, p) -> {
            final Optional<DatePickerRange> datePickerRange = getDatePickerRangeFromLabel(p.getChosenLabel());
            onDateRangeValueChange(label,
                                   datePickerRange.isPresent() ? datePickerRange.get().getLabel() : constants.Custom(),
                                   datePickerRange.isPresent() ? datePickerRange.get().getStartDate() : p.getStartDate(),
                                   datePickerRange.isPresent() ? datePickerRange.get().getEndDate() : p.getEndDate(),
                                   addCallback,
                                   removeCallback);
        });

        final Div div = (Div) getDocument().createElement("div");
        div.setAttribute("data-filter",
                         label);
        div.getClassList().add("input-group");
        div.getClassList().add("filter-control");
        div.getClassList().add("hidden");
        div.appendChild(dateRangePicker.getElement());
        dateFiltersInput.appendChild(div);
        createFilterOption(label,
                           dateFilters,
                           e -> setDateCurrentFilter(label));
        if (dateFilterText.getTextContent().isEmpty()) {
            setDateCurrentFilter(label);
        } else {
            removeCSSClass(dateCaret,
                           "hidden");
            dateButton.setAttribute("data-toggle",
                                    "dropdown");
        }
    }

    protected DateRangePickerOptions getDateRangePickerOptions() {
        final DateRangePickerOptions options = DateRangePickerOptions.create();
        options.setAutoUpdateInput(false);
        options.setAutoApply(true);
        options.setTimePicker(true);
        options.setTimePickerIncrement(30);
        options.setMaxDate(moment().endOf("day"));
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
                                          final Consumer<DateRange> addCallback,
                                          final Consumer<DateRange> removeCallback) {

        final DateRange dateRange = new DateRange(fromDate.milliseconds(0).asDate(),
                                                  toDate.milliseconds(0).asDate());

        final String hint = constants.From() + ": " + fromDate.format("lll") + "<br>" + constants.To() + ": " + toDate.format("lll");
        addActiveFilter(label,
                        selectedLabel,
                        hint,
                        dateRange,
                        removeCallback);
        addCallback.accept(dateRange);
    }

    @Override
    public void addSelectFilter(final String label,
                                final Map<String, String> options,
                                final Boolean liveSearch,
                                final Consumer<String> addCallback,
                                final Consumer<String> removeCallback) {
        final Select select = selectProvider.get();
        options.forEach((k, v) -> select.addOption(v,
                                                   k));
        setupSelect(label,
                    liveSearch,
                    select,
                    addCallback,
                    removeCallback);
    }

    private void setupSelect(final String label,
                             final Boolean liveSearch,
                             final Select select,
                             final Consumer<String> addCallback,
                             final Consumer<String> removeCallback) {
        select.setTitle(label);
        select.setLiveSearch(liveSearch);
        select.setWidth("auto");
        select.getElement().getClassList().add("selectpicker");
        select.getElement().getClassList().add("form-control");

        selectFilters.appendChild(select.getElement());
        select.refresh();
        select.getElement().addEventListener("change",
                                             event -> {
                                                 if (select.getValue().isEmpty() == false) {
                                                     final OptionsCollection options = select.getOptions();
                                                     for (int i = 0; i < options.getLength(); i++) {
                                                         final Option item = (Option) options.item(i);
                                                         if (item.getSelected()) {
                                                             addActiveFilter(label,
                                                                             item.getText(),
                                                                             select.getValue(),
                                                                             removeCallback);
                                                             addCallback.accept(select.getValue());
                                                             select.setValue("");
                                                             break;
                                                         }
                                                     }
                                                 }
                                             },
                                             false);
    }

    private void createInput(final String label,
                             final String placeholder,
                             final Consumer<Input> customizeCallback,
                             final Consumer<String> addCallback,
                             final Consumer<String> removeCallback) {
        final Input input = (Input) getDocument().createElement("input");
        customizeCallback.accept(input);
        input.setAttribute("placeholder",
                           placeholder);
        input.setAttribute("data-filter",
                           label);
        input.getClassList().add("form-control");
        input.getClassList().add("filter-control");
        input.getClassList().add("hidden");
        input.addEventListener("keypress",
                               (KeyboardEvent e) -> {
                                   if (e.getKeyCode() == KeyCodes.KEY_ENTER && input.getValue().isEmpty() == false) {
                                       addActiveFilter(label,
                                                       input.getValue(),
                                                       input.getValue(),
                                                       removeCallback);
                                       addCallback.accept(input.getValue());
                                       input.setValue("");
                                   }
                               },
                               false);
        filtersInput.appendChild(input);
        if (filterText.getTextContent().isEmpty()) {
            setInputCurrentFilter(label);
        }
    }

    private void createFilterOption(final String label,
                                    final HTMLElement element,
                                    final EventListener listener) {
        final Anchor a = (Anchor) getDocument().createElement("a");
        a.setTextContent(label);
        a.addEventListener("click",
                           listener,
                           false);
        final ListItem li = (ListItem) getDocument().createElement("li");
        li.setAttribute("data-filter",
                        label);
        li.appendChild(a);
        element.appendChild(li);
    }

    public void setInputCurrentFilter(final String label) {
        setCurrentFilter(label,
                         filterText,
                         filters,
                         filtersInput);
    }

    public void setDateCurrentFilter(final String label) {
        setCurrentFilter(label,
                         dateFilterText,
                         dateFilters,
                         dateFiltersInput);
    }

    private void setCurrentFilter(final String label,
                                  final HTMLElement text,
                                  final HTMLElement optionsText,
                                  final HTMLElement options) {
        text.setTextContent(label);
        for (Element child : elementIterable(optionsText.getChildNodes())) {
            if (label.equals(child.getAttribute("data-filter"))) {
                addCSSClass((HTMLElement) child,
                            "hidden");
            } else {
                removeCSSClass((HTMLElement) child,
                               "hidden");
            }
        }
        for (Element child : elementIterable(options.getChildNodes())) {
            if (label.equals(child.getAttribute("data-filter"))) {
                removeCSSClass((HTMLElement) child,
                               "hidden");
            } else {
                addCSSClass((HTMLElement) child,
                            "hidden");
            }
        }
    }

    @Override
    public <T extends Object> void addActiveFilter(final String labelKey,
                                                   final String labelValue,
                                                   final T value,
                                                   final Consumer<T> removeCallback) {
        addActiveFilter(labelKey,
                        labelValue,
                        null,
                        value,
                        removeCallback);
    }

    protected <T extends Object> void addActiveFilter(final String labelKey,
                                                      final String labelValue,
                                                      final String hint,
                                                      final T value,
                                                      final Consumer<T> removeCallback) {
        activeFiltersList.getModel().removeIf(f -> f.getLabelKey().equals(labelKey));
        activeFiltersList.getModel().add(new ActiveFilterItem(labelKey,
                                                              labelValue,
                                                              hint,
                                                              value,
                                                              removeCallback));
    }

    public void onRemoveActiveFilter(@Observes final ActiveFilterItemRemoved event) {
        activeFiltersList.getModel().remove(event.getActiveFilterItem());
    }

    @EventHandler("remove-all-filters")
    public void onRemoveAll(@ForEvent("click") Event e) {
        removeAllActiveFilters();
    }

    @Override
    public void removeAllActiveFilters() {
        activeFiltersList.getModel().clear();
    }

}