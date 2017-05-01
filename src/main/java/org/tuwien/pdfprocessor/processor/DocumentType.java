/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.processor;

/**
 *
 * @author amin
 */
public class DocumentType {
    private String type;
    private Boolean importToDb;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the importToDb
     */
    public Boolean getImportToDb() {
        return importToDb;
    }

    /**
     * @param importToDb the importToDb to set
     */
    public void setImportToDb(Boolean importToDb) {
        this.importToDb = importToDb;
    }
}
