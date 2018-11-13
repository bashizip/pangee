/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

import java.util.List;
import org.ektorp.ViewQuery;
import com.grew.couchdb.CoushDB;
import com.grew.dao.DataFlowRepository;
import com.grew.model.DataFlowDoc;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;

/**
 *
 * @author bashizip
 */
public class DataFlowManager extends ICrud<DataFlowDoc> {

    public CoushDB getCdb() {
        if (cdb == null) {
            getRepo();
        }
        return cdb;
    }

    @Override
    public CouchDbRepositorySupport<DataFlowDoc> getRepo() {
        if (repo == null) {

            if (repo == null) {
                cdb = new CoushDB(DBRefs.localDB());
                repo = new DataFlowRepository(cdb.getDB());

            }
        }
        return repo;

    }

    public DataFlowDoc findByProcessInstanceId(String execId) {
        List<DataFlowDoc> dfs = null;
        DataFlowRepository dfRepo = ((DataFlowRepository) getRepo());

        dfs = dfRepo.findByProcessInstanceId(execId);

        DataFlowDoc u = null;
        if (dfs != null && !dfs.isEmpty()) {
            u = dfs.get(0);

        }
        return u;
    }

    @Override
    public List<DataFlowDoc> findAll() {
        ViewQuery q = new ViewQuery().allDocs().includeDocs(false);
        if (cdb == null) {
            cdb = new CoushDB(DBRefs.localDB());
        }

        List<Object> bulkLoaded = cdb.getDB().queryView(q, Object.class);
        for (Object df : bulkLoaded) {
            System.out.println(df);
        }
        return null;

    }

    public static void main(String[] args) {
        DataFlowManager m = new DataFlowManager();

        for (DataFlowDoc df : m.findAll()) {
            System.out.println(df.getDefinitionId());
        }
    }

    public DataFlowDoc findByEntityId(String entityId) {

        List<DataFlowDoc> dfs = null;
        DataFlowRepository dfRepo = ((DataFlowRepository) getRepo());

        dfs = dfRepo.findByEntityId(entityId);

        DataFlowDoc u = null;
        if (dfs != null && !dfs.isEmpty()) {
            u = dfs.get(0);

        }
        return u;
    }

    public List<DataFlowDoc> findByRaisonSociale(String rs) {

        List<DataFlowDoc> dfs = null;
        DataFlowRepository dfRepo = ((DataFlowRepository) getRepo());

        dfs = dfRepo.findByRaisonSociale(rs);

        return dfs;

    }
}
