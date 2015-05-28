package org.jbpm.console.ng.gc.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface TableDisplayerConstants extends Messages {

    public static final TableDisplayerConstants INSTANCE = GWT.create(TableDisplayerConstants.class);

    String ok();

    String cancel();

    String table_displayer_editor_tab_data();

    String table_displayer_editor_tab_display();

    String Name();

    String Description();

    String Name_must_be_defined();

    String Description_must_be_defined();

    String Required_fields_must_be_defined();

}
