/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Amin
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    @Value("${app.solar.address}")
    private String SolrAddress;
    
    @Value("${app.groundtruth.path}")
    private String groundTruthPath;


    /**
     * @return the groundTruthPath
     */
    public String getGroundTruthPath() {
        return groundTruthPath;
    }

    /**
     * @param groundTruthPath the groundTruthPath to set
     */
    public void setGroundTruthPath(String groundTruthPath) {
        this.groundTruthPath = groundTruthPath;
    }

    /**
     * @return the SolrAddress
     */
    public String getSolrAddress() {
        return SolrAddress;
    }

    /**
     * @param SolrAddress the SolrAddress to set
     */
    public void setSolrAddress(String SolrAddress) {
        this.SolrAddress = SolrAddress;
    }

    }
