/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.model;

import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author amin
 */
public final class TableModel implements Serializable {
    private String FileID = "";
    private Integer TableCounter = 0;
    private String Header = "";
    private JsonObject rows;
    private List<RowData> rowDatas = new ArrayList<>();
    
    /**
     * Add RowData into the list
     * @param rowData 
     */
    public void addRowData(RowData rowData) {
        this.rowDatas.add(rowData);
    }

    /**
     * @return the Header
     */
    public String getHeader() {
        return Header;
    }

    /**
     * @param Header the Header to set
     */
    public void setHeader(String Header) {
        this.Header = Header;
    }

    /**
     * @return the rowDatas
     */
    public List<RowData> getRowDatas() {
        return rowDatas;
    }

    /**
     * @param rowDatas the rowDatas to set
     */
    public void setRowDatas(List<RowData> rowDatas) {
        this.rowDatas = rowDatas;
    }

    /**
     * @return the FileID
     */
    public String getFileID() {
        return FileID;
    }

    /**
     * @param FileID the FileID to set
     */
    public void setFileID(String FileID) {
        this.FileID = FileID;
    }

    /**
     * @return the TableCounter
     */
    public Integer getTableCounter() {
        return TableCounter;
    }

    /**
     * @param TableCounter the TableCounter to set
     */
    public void setTableCounter(Integer TableCounter) {
        this.TableCounter = TableCounter;
    }

    /**
     * @return the rows
     */
    public JsonObject getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(JsonObject rows) {
        this.rows = rows;
    }
    
    
}
