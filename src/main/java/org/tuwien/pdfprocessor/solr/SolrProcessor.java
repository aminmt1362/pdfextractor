/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.solr;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tuwien.pdfprocessor.helper.AppProperties;
import org.tuwien.pdfprocessor.processor.DocumentProcessor;

/**
 *
 * @author amin
 */
@Service
public class SolrProcessor {

    private static final Logger LOGGER = Logger.getLogger(SolrProcessor.class.getName());

    HttpSolrClient solr = null;

    
    private final AppProperties properties;

//    @Autowired
//    private DocumentProcessor documentProcessor;
    
    @Autowired
    public SolrProcessor(AppProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        String solrPath = properties.getSolrAddress();
        solr = new HttpSolrClient.Builder(solrPath).build();

//        solr.setParser(new JsonMapResponseParser());
//        SolrInputDocument solrInputDocument = new SolrInputDocument();
//        solrInputDocument.
    }

    /**
     * It should push extracted PDF documents into solr for search
     */
    public void insertDocumentsIntoSolr() throws SolrServerException, IOException {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
//        for (JSONArray extractedFile : documentProcessor.getExtractedFiles()) {
//            solrInputDocument.addField("documentid", extractedFile.getString(0));
//            solrInputDocument.addField("content", extractedFile.getString(1));
//            try {
//                solr.add(solrInputDocument);
//            } catch (SolrServerException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            }
//        }
        solr.commit();
    }
}
