/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.dao;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.model.SettingsDoc;

/**
 *
 * @author bashizip
 */
public class SettingsRepository extends CouchDbRepositorySupport<SettingsDoc> {

    public SettingsRepository(CouchDbConnector db) {
        super(SettingsDoc.class, db);
    }

 
}
