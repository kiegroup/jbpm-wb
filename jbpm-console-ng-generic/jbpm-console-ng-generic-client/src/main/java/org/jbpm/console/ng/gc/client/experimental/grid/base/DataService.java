package org.jbpm.console.ng.gc.client.experimental.grid.base;

import com.google.gwt.i18n.client.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jbpm.console.ng.ga.model.DataMockKey;
import org.jbpm.console.ng.ga.model.DataMockSummary;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ga.service.GenericServiceEntryPoint;
import org.uberfire.paging.PageResponse;

public class DataService implements GenericServiceEntryPoint<DataMockKey, DataMockSummary> {

    List<DataMockSummary> data = new ArrayList<DataMockSummary>();

    public void createData() {
        for (int i = 0; i < 1000; i++) {
            NumberFormat numberFormat = NumberFormat.getFormat("0000");
            String format = numberFormat.format(i);
            data.add(new DataMockSummary("ID:" + format, "Data 1:" + format, "Data Long Data here (XXXXXXXXXXXXXXXXXXXXXXX) 2:" + format,
                    "Data 3:" + format, "Data Long Data here (XXXXXXXXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxxxxxxx) 4:" + format));
        }
    }

    public List<DataMockSummary> getAllData() {
        return data;
    }

    public List<DataMockSummary> getPagedData(int start, int lenght) {
        return data.subList(start, lenght);
    }

    @Override
    public PageResponse<DataMockSummary> getData(final QueryFilter filter) {
        PageResponse<DataMockSummary> response = new PageResponse<DataMockSummary>();

        List<DataMockSummary> fiteredData = getAll(filter);

        response.setStartRowIndex(filter.getOffset());
        response.setTotalRowSize(fiteredData.size());
        response.setLastPage(false);
        response.setPageRowList(fiteredData.subList(filter.getOffset(), filter.getCount()));

        return response;
    }

    @Override
    public DataMockSummary getItem(DataMockKey key) {
        return null;
    }

    @Override
    public List<DataMockSummary> getAll(final QueryFilter filter) {
        if (filter.getOrderBy().equals("ID")) {
            Collections.sort(data, new Comparator<DataMockSummary>() {
                public int compare(DataMockSummary o1, DataMockSummary o2) {
                    if (o1 == o2) {
                        return 0;
                    }

                    // Compare the name columns.
                    int diff = -1;
                    if (o1 != null) {
                        diff = (o2 != null) ? o1.getId().compareTo(o2.getId()) : 1;
                    }
                    return filter.isAscending() ? diff : -diff;
                }
            });
        } else if (filter.getOrderBy().equals("Column1")) {
            Collections.sort(data, new Comparator<DataMockSummary>() {
                public int compare(DataMockSummary o1, DataMockSummary o2) {
                    if (o1 == o2) {
                        return 0;
                    }

                    // Compare the name columns.
                    int diff = -1;
                    if (o1 != null) {
                        diff = (o2 != null) ? o1.getColumn1().compareTo(o2.getColumn1()) : 1;
                    }
                    return filter.isAscending() ? diff : -diff;
                }
            });
        } else if (filter.getOrderBy().equals("Column2")) {
            Collections.sort(data, new Comparator<DataMockSummary>() {
                public int compare(DataMockSummary o1, DataMockSummary o2) {
                    if (o1 == o2) {
                        return 0;
                    }

                    // Compare the name columns.
                    int diff = -1;
                    if (o1 != null) {
                        diff = (o2 != null) ? o1.getColumn2().compareTo(o2.getColumn2()) : 1;
                    }
                    return filter.isAscending() ? diff : -diff;
                }
            });
        } else if (filter.getOrderBy().equals("Column3")) {
            Collections.sort(data, new Comparator<DataMockSummary>() {
                public int compare(DataMockSummary o1, DataMockSummary o2) {
                    if (o1 == o2) {
                        return 0;
                    }

                    // Compare the name columns.
                    int diff = -1;
                    if (o1 != null) {
                        diff = (o2 != null) ? o1.getColumn3().compareTo(o2.getColumn3()) : 1;
                    }
                    return filter.isAscending() ? diff : -diff;
                }
            });
        } else if (filter.getOrderBy().equals("Column4")) {
            Collections.sort(data, new Comparator<DataMockSummary>() {
                public int compare(DataMockSummary o1, DataMockSummary o2) {
                    if (o1 == o2) {
                        return 0;
                    }

                    // Compare the name columns.
                    int diff = -1;
                    if (o1 != null) {
                        diff = (o2 != null) ? o1.getColumn4().compareTo(o2.getColumn4()) : 1;
                    }
                    return filter.isAscending() ? diff : -diff;
                }
            });
        }
        return data;
    }

}
