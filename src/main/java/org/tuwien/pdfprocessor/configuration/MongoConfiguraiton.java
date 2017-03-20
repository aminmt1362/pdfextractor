/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuwien.pdfprocessor.configuration;

import com.mongodb.Mongo;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 *
 * @author amin
 */
//@Configuration
//@EnableMongoRepositories
public class MongoConfiguraiton extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "pdfextractor";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new Mongo("localhost:27017");
    }
    
}
