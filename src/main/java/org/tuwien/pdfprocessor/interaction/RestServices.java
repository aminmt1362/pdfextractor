/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.interaction;

import org.tuwien.pdfprocessor.processor.DocumentProcessor;
import org.tuwien.pdfprocessor.processor.GroundTruthProcessor;
import org.tuwien.pdfprocessor.helper.HttpContentResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tuwien.pdfprocessor.processor.DocumentType;
import org.tuwien.pdfprocessor.processor.Pdf2tableProcessor;
import org.tuwien.pdfprocessor.solr.SolrProcessor;

/**
 *
 * @author amin
 */
@RestController
public class RestServices {

    private final Logger LOGGER = Logger.getLogger(RestServices.class.getName());

    @Autowired
    private GroundTruthProcessor groungTruthProcessor;

    @Autowired
    private DocumentProcessor documentProcessor;

    @Autowired
    private Pdf2tableProcessor pdf2tableProcessor;

    @Autowired
    private SolrProcessor solrProcessor;

    @PostMapping("/processPdf2Table")
    public HttpEntity<?> processPdf2Table(@RequestBody DocumentType documentType) {
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
//        
        try {
            if (documentType.getType().toLowerCase().equals("default")) {
                pdf2tableProcessor.process(Pdf2tableProcessor.PROCESSTYPE.DEFAULT, documentType.getImportToDb());
            } else if(documentType.getType().toLowerCase().equals("groundtruth")) {
                pdf2tableProcessor.process(Pdf2tableProcessor.PROCESSTYPE.GROUNDTRUTH, documentType.getImportToDb());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
        }
//        
        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }

    /**
     * Import Processed documents into SOLR.
     *
     * @return
     */
    @PostMapping("/importtosolr")
    public HttpEntity<?> importDocumentToSolr() {
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
//        
        try {
            solrProcessor.insertDocumentsIntoSolr();
        } catch (SolrServerException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
        }
//        
        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }

    /**
     * Import Processed documents into SOLR.
     *
     * @return
     */
    @PostMapping("/importtosolrfromDB")
    public HttpEntity<?> importDocumentToSolrFromDB() {
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
//        
        try {
            solrProcessor.insertDocumentsIntoSolrFromDB();
        } catch (SolrServerException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
        }
//        
        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }

    /**
     * Start processing the HTML document extracted by PDFGENIE (Check Other
     * extractor) and import into MongoDB
     *
     * @return
     */
    @PostMapping("/documentprocesswithdb")
    public HttpEntity<?> documentProcessAndDBInsert() {
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);

        try {
            documentProcessor.processDBImport();
        } catch (IOException ex) {
            Logger.getLogger(RestServices.class.getName()).log(Level.SEVERE, null, ex);
            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }

    /**
     * Start processing the HTML document extracted by PDFGENIE (Check Other
     * extractor)
     *
     * @return
     */
    @PostMapping("/documentprocess")
    public HttpEntity<?> documentProcess() {
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);

        try {
            documentProcessor.process();
        } catch (IOException ex) {
            Logger.getLogger(RestServices.class.getName()).log(Level.SEVERE, null, ex);
            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }

    /**
     * Start processing the groundtruth by extracting tables from HTLM files
     * extracted by PDFGENIE
     *
     * @return
     */
    @PostMapping("/gtprocess")
    public HttpEntity<?> groundTruthProcess() {

        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);

        try {
            groungTruthProcessor.process();
        } catch (IOException ex) {
            Logger.getLogger(RestServices.class.getName()).log(Level.SEVERE, null, ex);
            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }

}
