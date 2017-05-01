/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.model;

import java.io.Serializable;

/**
 *
 * @author amin
 */
public final class RowCell implements Serializable {
    private String key;
    private Object value;

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the name to set
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
