/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.service;

import org.tuwien.pdfprocessor.processor.PdfGenieProcessor;
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
import org.tuwien.pdfprocessor.processor.SolrInput;
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
    private PdfGenieProcessor pdfGenieProcessor;

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
                pdf2tableProcessor.process(Pdf2tableProcessor.PROCESSTYPE.DEFAULT, documentType.getImportToDb(), documentType.getSourcePath());
            } else if (documentType.getType().toLowerCase().equals("groundtruth")) {
                pdf2tableProcessor.process(Pdf2tableProcessor.PROCESSTYPE.GROUNDTRUTH, documentType.getImportToDb(), documentType.getSourcePath());
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
     * It reads only from DB, and sends the extracted selected tables for ground
     * Truth into TCF scoring system
     *
     * @return
     */
    @PostMapping("/calcPdf2TableScore")
    public HttpEntity<?> calculatePdf2TableScore() {
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
//        
        try {
            String result = pdf2tableProcessor.calculateScoring();
            hcr = new HttpContentResponse(result);
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
     * @param solrInput
     * @return
     */
    @PostMapping("/importtosolr")
    public HttpEntity<?> importDocumentToSolr(@RequestBody SolrInput solrInput) {
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
//        
        try {
            solrProcessor.solrImportTables(solrInput.getCore(), solrInput.getProcessortype());
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
     * @param solrInput
     * @return
     */
    @PostMapping("/importhtmltosolr")
    public HttpEntity<?> importHtmlToSolr(@RequestBody SolrInput solrInput) {
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
//        
        try {
            solrProcessor.solrImportTables(solrInput.getCore(), solrInput.getProcessortype());
        } catch (SolrServerException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
        }
//        
        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }
//
//    /**
//     * Import Processed documents into SOLR.
//     *
//     * @return
//     */
//    @PostMapping("/importtosolrfromDB")
//    public HttpEntity<?> importDocumentToSolrFromDB() {
//        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
////        
//        try {
//            solrProcessor.insertDocumentsIntoSolrFromDB();
//        } catch (SolrServerException | IOException ex) {
//            LOGGER.log(Level.SEVERE, null, ex);
//            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
//            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
//        }
////        
//        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
//    }

//    /**
//     * Start processing the HTML document extracted by PDFGENIE (Check Other
//     * extractor) and import into MongoDB
//     *
//     * @param documentType
//     * @return
//     */
//    @PostMapping("/documentprocesswithdb")
//    public HttpEntity<?> documentProcessAndDBInsert(@RequestBody DocumentType documentType) {
//        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
//
//        try {
//            pdfGenieProcessor.processDBImport(documentType.getSourcePath());
//        } catch (IOException ex) {
//            Logger.getLogger(RestServices.class.getName()).log(Level.SEVERE, null, ex);
//            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
//            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
//        }
//
//        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
//    }
//
//    /**
//     * Start processing the HTML document extracted by PDFGENIE (Check Other
//     * extractor)
//     *
//     * @return
//     */
//    @PostMapping("/documentprocess")
//    public HttpEntity<?> documentProcess() {
//        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
//
//        try {
//            pdfGenieProcessor.process();
//        } catch (IOException ex) {
//            Logger.getLogger(RestServices.class.getName()).log(Level.SEVERE, null, ex);
//            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
//            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
//        }
//
//        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
//    }
    /**
     * Start processing the groundtruth by extracting tables from HTLM files
     * extracted by PDFGENIE
     *
     * @param documentType
     * @return
     */
    @PostMapping("/pdfgenieprocess")
    public HttpEntity<?> pdfGenieGtProcess(@RequestBody DocumentType documentType) {

        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);

        try {
            if (documentType.getType().toLowerCase().equals("groundtruth")) {
                pdfGenieProcessor.processPdfGenie(documentType.getSourcePath(), "pdfgeniegt");
            } else if (documentType.getType().toLowerCase().equals("default")) {
                pdfGenieProcessor.processPdfGenie(documentType.getSourcePath(), "pdfgenie");
            } else if (documentType.getType().toLowerCase().equals("ptag")) {
                pdfGenieProcessor.processDBImport(documentType.getSourcePath(), "pdfgenieptag");
            }
        } catch (IOException ex) {
            Logger.getLogger(RestServices.class.getName()).log(Level.SEVERE, null, ex);
            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }

    @PostMapping("/calcPdfGenieScore")
    public HttpEntity<?> calculatePdfGenieScore() {
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.STARTED);
//        
        try {
            String result = pdfGenieProcessor.calculateScoring();
            hcr = new HttpContentResponse(result);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            hcr = new HttpContentResponse(HttpContentResponse.ERROR);
            return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
        }
//        
        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }

}
