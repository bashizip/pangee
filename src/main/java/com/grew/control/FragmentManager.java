/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

import java.util.List;
import com.grew.couchdb.CoushDB;
import com.grew.dao.FragmentRepository;
import com.grew.model.FragmentDoc;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;

/**
 *
 * @author bashizip
 */
public class FragmentManager extends ICrud<FragmentDoc> {

    @Override
    public CouchDbRepositorySupport<FragmentDoc> getRepo() {
        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.entitiesDB());
            repo = new FragmentRepository(coushDB.getDB());
        }
        return repo;

    }

    public List<FragmentDoc> findByEntityId(String id) {
        List<FragmentDoc> dfs = null;
        FragmentRepository dfRepo = ((FragmentRepository) getRepo());

        dfs = dfRepo.findByEntityId(id);

        return dfs;
    }

    @Override
    public List<FragmentDoc> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<FragmentDoc> findByExecutionId(String processInstanceId) {
        List<FragmentDoc> dfs = null;
        FragmentRepository dfRepo = ((FragmentRepository) getRepo());

        dfs = dfRepo.findByExecutionId(processInstanceId);

        return dfs;
    }

}
