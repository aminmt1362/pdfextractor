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
    private String type; // Options: groundtruth , default for marking in db 
    private Boolean importToDb;
    private String sourcePath; // source path of xml files

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

    /**
     * @return the sourcePath
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * @param sourcePath the sourcePath to set
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

}
