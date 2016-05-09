package org.jbpm.console.ng.ga.model.dataset;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ConsoleDataSetLookup extends DataSetLookup {

    private String serverTemplateId;

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    public static DataSetLookup fromInstance(DataSetLookup orig, String serverTemplateId) {
        ConsoleDataSetLookup clone = new ConsoleDataSetLookup();
        clone.setDataSetUUID(orig.getDataSetUUID());
        clone.setRowOffset(orig.getRowOffset());
        clone.setNumberOfRows(orig.getNumberOfRows());
        for (DataSetOp dataSetOp : orig.getOperationList()) {
            clone.getOperationList().add(dataSetOp.cloneInstance());
        }
        clone.setServerTemplateId(serverTemplateId);
        return clone;
    }

    @Override
    public DataSetLookup cloneInstance() {
        return fromInstance(super.cloneInstance(), getServerTemplateId());
    }
}
