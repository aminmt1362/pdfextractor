/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.interaction;

import org.tuwien.pdfprocessor.processor.DocumentProcessor;
import org.tuwien.pdfprocessor.processor.GroungTruthProcessor;
import org.tuwien.pdfprocessor.helper.HttpContentResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author amin
 */

@RestController
public class RestServices {
    
    @Autowired
    private GroungTruthProcessor groungTruthProcessor;
    
    @Autowired
    private DocumentProcessor documentProcessor;
    
    
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
