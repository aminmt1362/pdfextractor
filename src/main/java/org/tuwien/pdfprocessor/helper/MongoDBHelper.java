/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.helper;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.tuwien.pdfprocessor.model.Document;
import org.tuwien.pdfprocessor.repository.DocumentRepository;

/**
 *
 * @author amin
 */
@Service
public class MongoDBHelper {

    @Autowired
    private DocumentRepository repository;

    public void deleteAllDocuments(String type) {

        org.tuwien.pdfprocessor.model.Document exampleDocument = new org.tuwien.pdfprocessor.model.Document();
        exampleDocument.setType(type);
        Example<org.tuwien.pdfprocessor.model.Document> example = Example.of(exampleDocument);

        List<org.tuwien.pdfprocessor.model.Document> docs = repository.findAll(example);
        
        for (Document doc : docs) {
            repository.delete(doc);
        }
    }
}
