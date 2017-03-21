/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.tuwien.pdfprocessor.helper.*;

/**
 *
 * @author amin
 */
@Service
public class DocumentProcessor {

    private static final String FILENAME = "/home/amin/Documents/amin/pdfgenie/CLEF2013wn-CHiC-HallEt2013.html";

    public void process() throws FileNotFoundException, IOException {

        String htmldata = Utility.readFile(FILENAME, StandardCharsets.UTF_8);

        JSONArray jSONArrayPTags = processDocument(htmldata, FILENAME);
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
}
