/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tuwien.pdfprocessor.model.Document;

/**
 *
 * @author amin
 */
@Repository
public interface Pdf2tableRepository extends MongoRepository<Document, String> {
    
}
