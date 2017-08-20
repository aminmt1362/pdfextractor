/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.solr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.tuwien.pdfprocessor.helper.AppProperties;
import org.tuwien.pdfprocessor.helper.MongoDBHelper;
import org.tuwien.pdfprocessor.model.Document;
import org.tuwien.pdfprocessor.processor.PdfGenieProcessor;
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
    private PdfGenieProcessor documentProcessor;

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private MongoDBHelper mongoHelper;

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
    @Deprecated
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
     * It should push extracted PDF documents (P TAGS) FROM DB into solr for
     * search
     */
    @Deprecated
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

    /**
     * Imports the extracted html files from pdf to Solr for indexing
     *
     * @param path
     * @throws SolrServerException
     * @throws IOException
     */
    public void importHtmlToSolr(String path, String indexName) throws SolrServerException, IOException {

        SolrInputDocument solrInputDocument;
        String solrPath = properties.getSolrAddress();

        // Add core Name to uri
        solr = new HttpSolrClient.Builder(solrPath + indexName).build();

        solr.deleteByQuery("*:*");
        solr.commit();

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        String content = new String(Files.readAllBytes(filePath));

                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }

    /**
     * It should push extracted PDF documents (P TAGS) FROM DB into solr for
     * search
     *
     * @param indexName
     * @param type
     * @throws org.apache.solr.client.solrj.SolrServerException
     * @throws java.io.IOException
     */
    public void solrImportTables(String indexName, String type) throws SolrServerException, IOException {
        SolrInputDocument solrInputDocument;
        String solrPath = properties.getSolrAddress();

        // Add core Name to uri
        solr = new HttpSolrClient.Builder(solrPath + indexName).build();

        solr.deleteByQuery("*:*");
        solr.commit();

        org.tuwien.pdfprocessor.model.Document exampleDocument = new org.tuwien.pdfprocessor.model.Document();
        exampleDocument.setType(type);
        Example<org.tuwien.pdfprocessor.model.Document> example = Example.of(exampleDocument);

        List<Document> lstDocs = repository.findAll(example);
        int counter = 0;
        for (Document doc : lstDocs) {

            solrInputDocument = new SolrInputDocument();

            if (doc.getContent().length() >= 32765) {
                doc.setContent(doc.getContent().substring(0, 30000));
            }

            solrInputDocument.addField("documentid", doc.getDocumentId());
            solrInputDocument.addField("content", doc.getContent());
            try {
                solr.add(solrInputDocument);
                if (counter++ % 100 == 0) {
                    solr.commit();
                }

            } catch (SolrServerException | IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        solr.commit();
    }
}
