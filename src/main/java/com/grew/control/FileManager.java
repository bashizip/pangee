/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

import java.util.List;
import com.grew.couchdb.CoushDB;
import com.grew.model.DataFlowDoc;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.dao.FileRepository;
import com.grew.model.FileDoc;

/**
 *
 * @author bashizip
 */
public class FileManager extends ICrud<FileDoc> {

    public FileManager() {
 
    }
   
    public CoushDB getCdb() {
        if (cdb == null) {
            getRepo();
        }
        return cdb;
    }

    @Override
    public CouchDbRepositorySupport<FileDoc> getRepo() {
        if (repo == null) {

            if (repo == null) {
                cdb = new CoushDB(DBRefs.filesDB());
                repo = new FileRepository(cdb.getDB());

            }
        }
        return repo;

    }

    public List<FileDoc> findByEntityId(String entityId) {

        List<FileDoc> dfs = null;
        FileRepository dfRepo = ((FileRepository) getRepo());

        dfs = dfRepo.findByEntityId(entityId);

        return dfs;
    }

    @Override
    public List<FileDoc> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
