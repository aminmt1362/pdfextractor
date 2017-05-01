/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.solr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tuwien.pdfprocessor.helper.AppProperties;
import org.tuwien.pdfprocessor.model.Document;
import org.tuwien.pdfprocessor.processor.DocumentProcessor;
import org.tuwien.pdfprocessor.repository.DocumentRepository;

/**
 *
 * @author amin
 */
@Service
public class SolrProcessor {

    private static final Logger LOGGER = Logger.getLogger(SolrProcessor.class.getName());

    HttpSolrClient solr = null;

    private final AppProperties properties;

    @Autowired
    private DocumentProcessor documentProcessor;
    
    @Autowired
    private DocumentRepository repository;

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
        SolrInputDocument solrInputDocument;
        solr.deleteByQuery("*:*");
        solr.commit();
        for (JSONArray extractedFile : documentProcessor.getExtractedFiles()) {
            Iterator it = extractedFile.iterator();
            while (it.hasNext()) {
                JSONObject jsonObj = (JSONObject) it.next();
                solrInputDocument = new SolrInputDocument();
                solrInputDocument.addField("documentid", jsonObj.get("documentid"));
                solrInputDocument.addField("content", jsonObj.get("content"));
                try {
                    solr.add(solrInputDocument);
                    solr.commit();
                } catch (SolrServerException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }

        }

    }
    
    /**
     * It should push extracted PDF documents (P TAGS) FROM DB into solr for search
     */
    public void insertDocumentsIntoSolrFromDB() throws SolrServerException, IOException {
        SolrInputDocument solrInputDocument;
        solr.deleteByQuery("*:*");
        solr.commit();
        
        List<Document> lstDocs = repository.findAll();
        
        for (Document doc : lstDocs) {
            
            solrInputDocument = new SolrInputDocument();
                solrInputDocument.addField("documentid", doc.getDocumentId());
                solrInputDocument.addField("content", doc.getContent());
                try {
                    solr.add(solrInputDocument);
                    solr.commit();
                } catch (SolrServerException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                
        }
    }
}
