/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tuwien.pdfprocessor.helper.*;
import org.tuwien.pdfprocessor.repository.DocumentRepository;

/**
 *
 * @author amin
 */
@Service
public class DocumentProcessor {

    private static final String FILENAME = "/home/amin/Documents/amin/pdfgenie/CLEF2013wn-CHiC-HallEt2013.html";
    private static final String GTPATH = "/home/amin/Documents/amin/classification/finalHtmlFiles/";

    private static final Logger LOGGER = Logger.getLogger(DocumentProcessor.class.getName());
    private final List<JSONArray> extractedFiles = new ArrayList<>();

    @Autowired
    private DocumentRepository repository;

    public void processDBImport() throws FileNotFoundException, IOException {

        repository. deleteAll();
        try (Stream<Path> paths = Files.walk(Paths.get(GTPATH))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        System.out.println(filePath);
                        String htmldata = Utility.readFile(filePath.toString(), StandardCharsets.UTF_8);
                        List<org.tuwien.pdfprocessor.model.Document> tables = processDocumentMongoDB(htmldata, filePath.getFileName().toString());
                        //this.extractedFiles.add(jSONArray);
                        //  repository.insert(this)
                        // Import it into DB OR CALL TCF IMPORT GT comparison
                        tables.forEach((table) -> {
                            repository.insert(table);
                        });
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }

                }
            });
        }
//
//        for (JSONArray extractedFile : this.extractedFiles) {
//            System.out.println(extractedFile.toString());
//        }
    }

    public void process() throws FileNotFoundException, IOException {

        try (Stream<Path> paths = Files.walk(Paths.get(GTPATH))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        System.out.println(filePath);
                        String htmldata = Utility.readFile(filePath.toString(), StandardCharsets.UTF_8);
                        JSONArray jSONArray = processDocument(htmldata, filePath.getFileName().toString());
                        this.extractedFiles.add(jSONArray);
                        // Import it into DB OR CALL TCF IMPORT GT comparison
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }

                }
            });
        }

        for (JSONArray extractedFile : this.extractedFiles) {
            System.out.println(extractedFile.toString());
        }
    }

    /**
     * Process HTML data by extracting paragraphs and creating an id of that and
     * import into mongodb for further use.
     *
     * INFO: A document ID is built from the document file name and a counter
     * for paragraph
     *
     * @param htmlData
     * @return
     */
    private JSONArray processDocument(String htmlData, String fileName) {
        Document document = Jsoup.parse(htmlData);
        JSONArray returnTables = new JSONArray();
        JSONObject tableObject;

        String fileId = fileName;
        Integer documentContentCounter = 0;

        for (Element paragraph : document.select("p")) {

            // Check whether it contains a table - Then process table
            Elements tables = paragraph.select("table");
            if (tables != null && tables.size() > 0) {
                for (Element table : tables) {
                    String tableContent = table.text();
                    String id = fileId + "_" + documentContentCounter++;

                    // Check whether table is inside P to get table header
                    Element pHeaderTag = table.previousElementSibling();
                    if (pHeaderTag != null && "p".equals(pHeaderTag.tagName())) {
                        tableContent = pHeaderTag.text() + tableContent;
                    }

                    tableObject = new JSONObject();
                    tableObject.put("documentid", id);
                    tableObject.put("content", tableContent);
                    returnTables.put(tableObject);
                }
            } else {
                String tableContent = paragraph.text();
                String id = fileId + "_" + documentContentCounter++;

                tableObject = new JSONObject();
                tableObject.put("documentid", id);
                tableObject.put("content", tableContent);
                returnTables.put(tableObject);
            }

        }

        return returnTables;
    }

    /**
     * Process HTML data by extracting paragraphs and creating an id of that and
     * import into mongodb for further use.
     *
     * INFO: A document ID is built from the document file name and a counter
     * for paragraph
     *
     * @param htmlData
     * @return
     */
    private List<org.tuwien.pdfprocessor.model.Document> processDocumentMongoDB(String htmlData, String fileName) {
        Document document = Jsoup.parse(htmlData);
        JSONArray returnTables = new JSONArray();
        JSONObject tableObject;

        List<org.tuwien.pdfprocessor.model.Document> mongoDocs = new ArrayList<org.tuwien.pdfprocessor.model.Document>();

        String fileId = fileName;
        Integer documentContentCounter = 0;

        for (Element paragraph : document.select("p")) {

            // Check whether it contains a table - Then process table
            Elements tables = paragraph.select("table");
            if (tables != null && tables.size() > 0) {
                for (Element table : tables) {
                    String tableContent = table.text();
                    String id = fileId + "_" + documentContentCounter++;

                    // Check whether table is inside P to get table header
                    Element pHeaderTag = table.previousElementSibling();
                    if (pHeaderTag != null && "p".equals(pHeaderTag.tagName())) {
                        tableContent = pHeaderTag.text() + tableContent;
                    }

                    org.tuwien.pdfprocessor.model.Document newMongoDoc = new org.tuwien.pdfprocessor.model.Document();
                    // tableObject = new JSONObject();
                    newMongoDoc.setDocumentName(fileName);

                    newMongoDoc.setDocumentId(id);
                    newMongoDoc.setContent(tableContent);
                    //tableObject.put("documentid", id);

                    mongoDocs.add(newMongoDoc);
                    //tableObject.put("content", tableContent);
                    //returnTables.put(tableObject);
                }
            } else {
                String tableContent = paragraph.text();
                String id = fileId + "_" + documentContentCounter++;

//                tableObject = new JSONObject();
//                tableObject.put("documentid", id);
//                tableObject.put("content", tableContent);
//                returnTables.put(tableObject);
                org.tuwien.pdfprocessor.model.Document newMongoDoc = new org.tuwien.pdfprocessor.model.Document();
                // tableObject = new JSONObject();
                newMongoDoc.setDocumentName(fileName);

                newMongoDoc.setDocumentId(id);
                newMongoDoc.setContent(tableContent);
                //tableObject.put("documentid", id);

                mongoDocs.add(newMongoDoc);
            }

        }

        return mongoDocs;
    }

    /**
     * @return the extractedFiles
     */
    public List<JSONArray> getExtractedFiles() {
        return extractedFiles;
    }
}
