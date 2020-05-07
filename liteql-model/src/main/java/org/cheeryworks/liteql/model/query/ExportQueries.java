package org.cheeryworks.liteql.model.query;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ExportQueries implements Serializable {

    private List<ExportQuery> exportQueries = new LinkedList<ExportQuery>();

    public List<ExportQuery> getExportQueries() {
        return exportQueries;
    }

    public void setExportQueries(List<ExportQuery> exportQueries) {
        this.exportQueries = exportQueries;
    }

}
