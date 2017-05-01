/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.processor;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import org.json.JSONException;
import org.tuwien.pdfprocessor.helper.Utility;
import org.tuwien.pdfprocessor.model.TableModel;

/**
 *
 * @author amin
 */
public class PDFGenieProcessor {
    
    private final Logger LOGGER = Logger.getLogger(PDFGenieProcessor.class.getName());
    
    private List<JsonObject> groundTruthGObjects = new ArrayList<>();
    private List<TableModel> groundTruthTables = new ArrayList<>();
    
    private void process() throws IOException {
        // Read Groundtruth Files
        try (Stream<Path> paths = Files.walk(Paths.get(""))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        String jsonTxt = Utility.readFile(filePath.toString(), StandardCharsets.UTF_8);
                        System.out.println("Processing file " + filePath.toString());

                        ObjectMapper mapper = new ObjectMapper();
                        JsonFactory factory = mapper.getFactory(); // since 2.1 use mapper.getFactory() instead
                        com.fasterxml.jackson.core.JsonParser jp = factory.createParser(jsonTxt);
                        JsonNode actualObj = mapper.readTree(jp);

                        JsonParser parser = new JsonParser();

                        JsonElement jelement = parser.parse(jsonTxt);
                        JsonObject jsobj = jelement.getAsJsonObject();
                        groundTruthGObjects.add(jsobj);

                        TableModel table = new TableModel();
                        table.setHeader(jsobj.get("header").getAsString());
                        table.setFileID(jsobj.get("fileid").getAsString());
                        table.setTableCounter(Integer.valueOf(jsobj.get("tablecounter").toString()));
                        table.setRows(jsobj);
                        
                        groundTruthTables.add(table);

//                        JSONObject json = new JSONObject(jsonTxt);
//                        this.groundTruthObjects.add(json);
                        /*Table newTable = mapJsonToObject(json);
                        List<Table> tables = this.groundTruthTables.get(newTable.getFileID());

                        // GET LIST OF ALL TABLES, REPLACE IT WITH NEWLY ADDED TABLE INTO the list of tables of one file.
                        if (tables != null && tables.size() > 0) {
                            tables.add(newTable);
                            this.groundTruthTables.replace(newTable.getFileID(), tables);
                        } else {
                            tables = new ArrayList<>();
                            tables.add(newTable);
                            this.groundTruthTables.put(newTable.getFileID(), tables);
                        }
                        
                         */
                    } catch (FileNotFoundException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (JSONException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }

                }
            });
        }
//        printGroundTruhTables();
    }
}
