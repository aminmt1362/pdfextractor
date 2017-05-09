/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.processor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tuwien.pdfprocessor.helper.MongoDBHelper;
import org.tuwien.pdfprocessor.repository.DocumentRepository;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author amin
 */
@Service
public class Pdf2tableProcessor {

//    @Autowired
//    private Pdf2tableGtRepository gtRepository;
    @Autowired
    private DocumentRepository repository;

    @Autowired
    private MongoDBHelper mongoHelper;

    private static final Logger LOGGER = Logger.getLogger(Pdf2tableProcessor.class.getName());

    private static final String MAINPATH = "/home/amin/Documents/amin/classification/pdf2tableresults/all/";
    private static final String GROUNDTRUTHPATH = "/home/amin/Documents/amin/classification/pdf2tableresults/gt/";

    private static final String PDFTABLECONST = "pdftable";
    private static final String PDFTABLEGTCONST = "pdftablegt";

    public enum PROCESSTYPE {
        DEFAULT,
        GROUNDTRUTH
    }

    public void process(PROCESSTYPE processType, Boolean insertIntoDB, String sourcePath) throws IOException {

        String finalPath = "";

        if (processType == PROCESSTYPE.GROUNDTRUTH) {
//            finalPath = GROUNDTRUTHPATH;
            mongoHelper.deleteAllDocuments(PDFTABLEGTCONST);
        } else {
//            finalPath = MAINPATH;
            mongoHelper.deleteAllDocuments(PDFTABLECONST);
        }

        finalPath = sourcePath;
        List<org.tuwien.pdfprocessor.model.Document> docList = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(finalPath))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {

                    try {

                        File fXmlFile = new File(filePath.toString());
                        if (fXmlFile.toString().endsWith("output.xml")) {
                            System.out.println("processing " + filePath.toString());

                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                            Document doc = dBuilder.parse(fXmlFile);
                            doc.getDocumentElement().normalize();

                            JSONArray tables = processDocument(doc, filePath.getParent().toString());

                            // Insert into MongoDB
                            if (insertIntoDB) {
                                if (processType == PROCESSTYPE.GROUNDTRUTH) {

                                    for (Object o : tables) {
                                        if (o instanceof JSONObject) {
                                            org.tuwien.pdfprocessor.model.Document docModel = new org.tuwien.pdfprocessor.model.Document();
                                            JSONObject objModel = ((JSONObject) o);
                                            docModel.setContent(objModel.toString());
                                            docModel.setDocumentId(objModel.getString("fileid") + "_" + objModel.getInt("tablecounter"));
                                            docModel.setDocumentName(objModel.getString("fileid"));
                                            docModel.setType(PDFTABLEGTCONST);
//                                            repository.insert(docModel);
                                            docList.add(docModel);
                                        }
                                    }
                                } else {

                                    org.tuwien.pdfprocessor.model.Document docModel = null;
                                    for (Object o : tables) {
                                        if (o instanceof JSONObject) {
                                            docModel = new org.tuwien.pdfprocessor.model.Document();
                                            JSONObject objModel = ((JSONObject) o);
//                                            if (!objModel.toString().equals("{}")) {
                                            docModel.setContent(objModel.toString());
                                            docModel.setDocumentId(objModel.getString("fileid") + "_" + objModel.getInt("tablecounter"));
                                            docModel.setDocumentName(objModel.getString("fileid"));
                                            docModel.setType(PDFTABLECONST);
                                            docList.add(docModel);
//                                            repository.insert(docModel);
//                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (SAXException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (ParserConfigurationException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }

                }
            });
            
        }
        
        docList.forEach((table) -> {
            repository.insert(table);
        });
    }

    /**
     * Reads xml document and convert it into the JSON format we require
     */
    private JSONArray processDocument(Document doc, String path) {

        JSONArray returnTables = new JSONArray();
        JSONObject tableObject = null;
        JSONArray jsonArr = null;

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        int tableCounter = 0;

        NodeList nodeList = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
//            tableObject = new JSONObject();
            //We have encountered an <table> tag.
            Node node = nodeList.item(i);
            if ("table".equals(node.getNodeName())) {
                if (node instanceof Element) {
                    tableCounter++;
                    Map<Integer, String> headerList = new HashMap<>();

                    // Get title
                    Node titleNode = ((Element) node).getElementsByTagName("title").item(0);

                    String title = titleNode.getTextContent();

                    // GET HEADER ELEMENT
                    NodeList header = ((Element) node).getElementsByTagName("header");

                    // Get Only First Header
                    Node firstHeaderNode = header.item(0);

                    // GET First Header_Line
                    Node headerLineNode = ((Element) firstHeaderNode).getElementsByTagName("header_line").item(0);

                    // GET HEADER ELEMENT LISTS
                    NodeList headerElements = headerLineNode.getChildNodes();

                    // ITERATE ALL HEADE_ELEMENTS
                    int headerelementcounter = 0;
                    for (int c = 0; c < headerElements.getLength(); c++) {
                        Node headerElementNode = headerElements.item(c);
                        if (headerElementNode.getNodeName().equals("header_element")) {
                            headerelementcounter++;
                            String pathToNode = MessageFormat.format("/tables/table[{0}]/header[1]/header_line[1]/header_element[{1}]/@sh", tableCounter, headerelementcounter);
//                            try {
                            //String shVal = (String) xpath.evaluate(pathToNode, doc, XPathConstants.STRING);
                            String shVal = headerElementNode.getAttributes().getNamedItem("sh").getNodeValue();
                            headerList.put(Integer.valueOf(shVal.trim()), headerElementNode.getTextContent().replace("\n", "").replace("\r", ""));

//                            } catch (XPathExpressionException ex) {
//                                LOGGER.log(Level.SEVERE, null, ex);
//                            }
                        }
                    }

                    // For each table create JSON
                    // Count data_rows
                    String datarowcounter = MessageFormat.format("/tables/table[{0}]/tbody[1]/data_row", tableCounter);
                    XPathExpression expr;
                    try {
                        expr = xpath.compile(datarowcounter);

                        NodeList datarows = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                        Integer rowCounts = datarows.getLength();

                        tableObject = new JSONObject();
                        jsonArr = new JSONArray();

                        for (int rowCounter = 0; rowCounter < rowCounts; rowCounter++) {

                            // For each Row Get Row Values
                            JSONObject tableDataObject = new JSONObject();

                            int headerCounter = 0;
                            for (Map.Entry<Integer, String> entry : headerList.entrySet()) {
                                headerCounter++;
                                Integer shVal = entry.getKey();
                                String headerText = entry.getValue();

                                // Find Cell
                                for (int dbc = 0; dbc < rowCounts; dbc++) {
                                    Node dr = datarows.item(dbc);

                                    for (int cellcnt = 0; cellcnt < dr.getChildNodes().getLength(); cellcnt++) {
                                        Node cll = dr.getChildNodes().item(cellcnt);
                                        if (cll.getAttributes() != null) {
                                            if (Integer.valueOf(cll.getAttributes().getNamedItem("sh").getNodeValue()) == shVal) {

//                                            Node cellItem = cll.getTextContent();
                                                String cellContent = cll.getTextContent().replace("\n", "").replace("\r", "");

                                                if (headerText.equals("")) {
                                                    headerText = "header " + headerCounter;
                                                }
                                                tableDataObject.put(headerText, cellContent);
                                            }
                                        }
                                    }
                                }
//                                String propCell = MessageFormat.format("/tables/table[{0}]/tbody[1]/data_row[{1}]/cell[@sh={2}]", tableCounter, rowCounter + 1, shVal);
//                                expr = xpath.compile(propCell);
//                                NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
//                                if (nl.getLength() > 0) {
//                                    Node cellItem = nl.item(0);
//                                    String cellContent = cellItem.getTextContent().replace("\n", "").replace("\r", "");
//
//                                    if (headerText.equals("")) {
//                                        headerText = "header " + headerCounter;
//                                    }
//                                    tableDataObject.put(headerText, cellContent);
//                                } else {
//                                    tableDataObject.put(headerText, "no data");
//                                }
                                // Add this cellText with the headername into JSON

//                                for (int cellcounter = 0; cellcounter < nl.getLength(); cellcounter++) {
//                                    
//                                    System.out.println(cellContent);
//                                }
                            }
                            // Add Row into array
                            jsonArr.put(tableDataObject);

                        }

                        // Add rows into tableObject
                        tableObject.put("rows", jsonArr);

                        String fileName = path.substring(path.lastIndexOf("/") + 1).replace(".pdf", "");

                        tableObject.put("fileid", fileName);
                        tableObject.put("tablecounter", tableCounter);
                        tableObject.put("header", title);
                        tableObject.toString();

                        String fileNameJson = fileName + "_" + tableCounter + ".json";
                        try (FileWriter file = new FileWriter(path + "/" + fileNameJson)) {
                            file.write(tableObject.toString());
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }

                    } catch (XPathExpressionException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }

            }

            if (tableObject != null) {
                returnTables.put(tableObject);

                // Save JSON INTO FILE
            }
        }

        return returnTables;
    }

    /**
     * It will read the PDf2tables from database and sends them into TCF scoring
     * system
     */
    public String calculateScoring() {

        org.tuwien.pdfprocessor.model.Document exampleDocument = new org.tuwien.pdfprocessor.model.Document();
        exampleDocument.setType("pdf2tablegt");
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

}
