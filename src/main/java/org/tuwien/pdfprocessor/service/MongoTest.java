/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.service;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tuwien.pdfprocessor.model.Document;
import org.tuwien.pdfprocessor.repository.DocumentRepository;

/**
 *
 * @author amin
 */
@Service
public class MongoTest {

    @Autowired
    private DocumentRepository repository;

    @PostConstruct
    public void test() {

        Document d = new Document();
        d.setDocumentId("chic_1");
        d.setDocumentName("chic");
        repository.insert(d);

        List<Document> listOfDocuments = repository.findAll();

        for (Document listOfDocument : listOfDocuments) {
            System.out.print(listOfDocument.getContent());
        }
    }
}
