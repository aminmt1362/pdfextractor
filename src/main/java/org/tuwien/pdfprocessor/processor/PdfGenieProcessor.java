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
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tuwien.pdfprocessor.helper.*;
import org.tuwien.pdfprocessor.repository.DocumentRepository;

/**
 *
 * @author amin
 */
@Service
public class PdfGenieProcessor {

    private static final String FILENAME = "/home/amin/Documents/amin/pdfgenie/CLEF2013wn-CHiC-HallEt2013.html";
    private static final String GTPATH = "/home/amin/Documents/amin/classification/finalHtmlFiles_pdfgenie/";

    private static final Logger LOGGER = Logger.getLogger(PdfGenieProcessor.class.getName());
    private final List<JSONArray> extractedFiles = new ArrayList<>();
    
    @Autowired
    private MongoDBHelper mongoHelper;

    @Autowired
    private DocumentRepository repository;

    public void processDBImport(String sourcePath) throws FileNotFoundException, IOException {

        mongoHelper.deleteAllDocuments("pdfgenie");
        try (Stream<Path> paths = Files.walk(Paths.get(sourcePath))) {
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
                    newMongoDoc.setType("pdfgenie");
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
                newMongoDoc.setType("pdfgenie");
                //tableObject.put("documentid", id);

                mongoDocs.add(newMongoDoc);
            }

        }

        return mongoDocs;
    }

    /**
     * Process the extracted htmls of tables and import them into db
     *
     * @param sourcePath
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void processGt(String sourcePath) throws FileNotFoundException, IOException {

        List<JSONArray> extractedFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(sourcePath))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        System.out.println(filePath);
                        String htmldata = Utility.readFile(filePath.toString(), StandardCharsets.UTF_8);
                        JSONArray jSONArray = processTablesToJson(htmldata, filePath.getFileName().toString());
                        extractedFiles.add(jSONArray);
                        // Import it into DB OR CALL TCF IMPORT GT comparison
                    } catch (IOException ex) {
                        Logger.getLogger(GroundTruthProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });
        }

        // Remove all already processed pdfgenie groundtruth 
        mongoHelper.deleteAllDocuments("pdfgeniegt");
        for (JSONArray extractedFile : extractedFiles) {
            //System.out.println(extractedFile.toString());
            for (Object o : extractedFile) {
                if (o instanceof JSONObject) {
                    org.tuwien.pdfprocessor.model.Document docModel = new org.tuwien.pdfprocessor.model.Document();
                    JSONObject objModel = ((JSONObject) o);
                    docModel.setContent(objModel.toString());
                    docModel.setDocumentId(objModel.getString("fileid") + "_" + objModel.getInt("tablecounter"));
                    docModel.setDocumentName(objModel.getString("fileid"));
                    docModel.setType("pdfgeniegt");
                    repository.insert(docModel);
                }
            }
        }

    }

    /**
     * Extract tables from HTML and returns an array of json objects
     *
     * @param htmlData
     * @return
     */
    private JSONArray processTablesToJson(String htmlData, String fileName) {
        Document document = Jsoup.parse(htmlData);

        JSONArray returnTables = new JSONArray();
        JSONObject tableObject;
        JSONArray jsonArr = null;
        int tableCounter = 0;
        for (Element newTable : document.select("table")) {
            tableCounter++;
            tableObject = new JSONObject();
            jsonArr = new JSONArray();

            Element pTag = newTable.previousElementSibling();
            if (pTag != null && "p".equals(pTag.tagName())) {
                tableObject.put("header", pTag.text());
            }

            List<String> tableHeaders = new ArrayList<>();
            for (Element tableTr : newTable.select("tr")) {

                for (Element tableTh : tableTr.select("th")) {
                    tableHeaders.add(tableTh.text());
                }

                JSONObject tableDataObject = new JSONObject();
                Integer counter = 0;
                for (Element tableTd : tableTr.select("td")) {

                    String headerVal = tableHeaders.isEmpty() || counter >= tableHeaders.size() ? counter.toString() : tableHeaders.get(counter);
                    tableDataObject.put(headerVal, tableTd.text());
                    counter++;
                }

                if (tableDataObject.length() > 0) {
                    jsonArr.put(tableDataObject);
                }

            }
            tableObject.put("tablecounter", tableCounter);
            tableObject.put("fileid", fileName.replaceAll(".html", ""));
            tableObject.put("rows", jsonArr);
//            System.out.println();
//            System.out.println(tableObject.toString(4));
            returnTables.put(tableObject);
        }

        return returnTables;
    }
    
    
    /**
     * It will read the PDf2tables from database and sends them into TCF scoring
     * system
     * @return 
     */
    public String calculateScoring() {

        org.tuwien.pdfprocessor.model.Document exampleDocument = new org.tuwien.pdfprocessor.model.Document();
        exampleDocument.setType("pdfgeniegt");
        Example<org.tuwien.pdfprocessor.model.Document> example = Example.of(exampleDocument);

        List<org.tuwien.pdfprocessor.model.Document> docs = repository.findAll(example);

        RestTemplate restTemplate = new RestTemplate();
        // Clear Tables
        restTemplate.delete("http://localhost:8090/deleteUserTable/");

        for (org.tuwien.pdfprocessor.model.Document doc : docs) {
            restTemplate.postForEntity("http://localhost:8090/importtable/", doc.getContent(), org.tuwien.pdfprocessor.model.Document.class);
            
        }
        
        // now that all tables are importes, now we start calculation
        String entity = restTemplate.getForObject("http://localhost:8090/calculateScore/", String.class);
        System.out.print(entity);
        return entity;
    }

    /**
     * @return the extractedFiles
     */
    public List<JSONArray> getExtractedFiles() {
        return extractedFiles;
    }
}
