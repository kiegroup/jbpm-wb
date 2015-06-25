/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.df.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface FiltersConstants extends Messages {

    public static final FiltersConstants INSTANCE = GWT.create(FiltersConstants.class);

    String ok();

    String cancel();

    String table_displayer_editor_tab_data();

    String table_displayer_editor_tab_display();

    String Name();

    String Description();

    String Name_must_be_defined();

    String Description_must_be_defined();

    String Required_fields_must_be_defined();

    String displayer_editor_dataset_notfound();

    String displayer_editor_datasetmetadata_fetcherror();

    String filter_editor_selectcolumn();

    String common_dropdown_select();

    String dataset_filters();

    String common_button_addnew();

    String timeframe_from();

    String timeframe_to();

    String timeframe_first_month_year();

    String json_displayersettings_dataset_lookup_notspecified() ;

    String json_columnsettings_null_columnid();

    String json_datasetlookup_unsupported_column_filter();

    String json_datasetlookup_columnfilter_null_columnid();

    String json_datasetlookup_columnfilter_null_functiontype();

    String json_datasetlookup_corefunction_null_params();

    String json_datasetlookup_logexpr_null_params();

    String json_datasetlookup_columnfilter_wrong_type();

    String json_datasetlookup_validation_error();

    String json_dataset_column_id_type_not_specified();

    String datasethandler_groupops_no_pivotcolumn();

    String datasethandler_groupops_no_groupintervals();

    String Basic_Properties();

    String Filter_parameters();





}
