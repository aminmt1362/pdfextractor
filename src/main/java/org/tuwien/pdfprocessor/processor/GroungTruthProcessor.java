/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.tuwien.pdfprocessor.helper.*;

/**
 *
 * @author amin
 */
@Service
public class GroungTruthProcessor {

    private static final String FILENAME = "/home/amin/Documents/amin/pdfgenie/CLEF2013wn-CHiC-HallEt2013.html";

    public void process() throws FileNotFoundException, IOException {

        String htmldata = Utility.readFile(FILENAME, StandardCharsets.UTF_8);

        JSONArray jSONArray = processTablesToJson(htmldata);
    }

    /**
     * Extract tables from HTML and returns an array of json objects
     *
     * @param htmlData
     * @return
     */
    private JSONArray processTablesToJson(String htmlData) {
        Document document = Jsoup.parse(htmlData);

        JSONArray returnTables = new JSONArray();
        JSONObject tableObject;
        JSONArray jsonArr = null;
        for (Element newTable : document.select("table")) {

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
                int counter = 0;
                for (Element tableTd : tableTr.select("td")) {
                    tableDataObject.put(tableHeaders.get(counter), tableTd.text());
                    counter++;
                }

                if (tableDataObject.length() > 0) {
                    jsonArr.put(tableDataObject);
                }

            }
            tableObject.put("rows", jsonArr);
            System.out.println();
            System.out.println(tableObject.toString(4));
            returnTables.put(tableObject);
        }

        return returnTables;
    }
}
