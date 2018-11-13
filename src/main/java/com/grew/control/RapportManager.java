/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

import java.util.List;
import com.grew.couchdb.CoushDB;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.dao.RapportsRepository;
import com.grew.model.ReportDoc;

/**
 *
 * @author bashizip
 */
public class RapportManager extends ICrud<ReportDoc> {

    @Override
    public CouchDbRepositorySupport<ReportDoc> getRepo() {
        if (repo == null) {
            if (repo == null) {
                CoushDB coushDB = new CoushDB(DBRefs.rapportsDB());
                repo = new RapportsRepository(coushDB.getDB());

            }
        }
        return repo;

    }

    public ReportDoc findByReportKey(String key) {
        ReportDoc dfs = null;
        RapportsRepository dfRepo = ((RapportsRepository) getRepo());
        dfs = dfRepo.findByReportKey(key);
        return dfs;
    }

    @Override
    public List<ReportDoc> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
