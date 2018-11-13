package com.grew.couchdb;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.MalformedURLException;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

/**
 *
 * @author bashizip
 */
public class CoushDB {

    private  CouchDbConnector db;
    private  DBConfig instanceCfg;


    public  CoushDB(DBConfig cfg) {
        this.instanceCfg = cfg;
        initDB();
    }

    private void initDB() {

        HttpClient httpClient = null;
        try {
            httpClient = new StdHttpClient.Builder()
                    .url(instanceCfg.getUrl())
                    .username(instanceCfg.getUsername())
                    .password(instanceCfg.getPassword())
                    .build();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        db = new StdCouchDbConnector(instanceCfg.getDbName(), dbInstance);
       // db.createDatabaseIfNotExists();

    }

    public CouchDbConnector getDB() {

        if (db == null) {
            initDB();
        }
        return db;
    }

}
