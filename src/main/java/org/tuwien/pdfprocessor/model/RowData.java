/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author amin
 */
public final class RowData implements Serializable {
    private List<RowCell> rowColumn = new ArrayList<>();
    
    public void addRowColumn(RowCell rc) {
        this.rowColumn.add(rc);
    }

    /**
     * @return the rowColumn
     */
    public List<RowCell> getRowColumn() {
        return rowColumn;
    }

    /**
     * @param rowColumn the rowColumn to set
     */
    public void setRowColumn(List<RowCell> rowColumn) {
        this.rowColumn = rowColumn;
    }
}
