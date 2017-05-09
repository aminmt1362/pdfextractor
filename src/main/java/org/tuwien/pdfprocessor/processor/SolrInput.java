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
public class SolrInput {
    private String core;
    private String processortype;

    /**
     * @return the core
     */
    public String getCore() {
        return core;
    }

    /**
     * @param core the core to set
     */
    public void setCore(String core) {
        this.core = core;
    }

    /**
     * @return the processortype
     */
    public String getProcessortype() {
        return processortype;
    }

    /**
     * @param processortype the processortype to set
     */
    public void setProcessortype(String processortype) {
        this.processortype = processortype;
    }
}
