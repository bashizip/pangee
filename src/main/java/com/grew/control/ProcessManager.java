package com.grew.control;

import java.util.List;
import com.grew.couchdb.CoushDB;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.dao.ProcessRepository;
import com.grew.model.ProcessDoc;

/**
 *
 *
 * @author bashizip
 */
public class ProcessManager extends ICrud<ProcessDoc> {

    public ProcessManager() {

    }

    @Override
    public List<ProcessDoc> findAll() {
        return ((ProcessRepository) getRepo()).findAll();
    }

    @Override
    public CouchDbRepositorySupport<ProcessDoc> getRepo() {

        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.proceduresDB());
            repo = new ProcessRepository(coushDB.getDB());

        }
        return repo;
    }

    public List<ProcessDoc> findAllByGroup(String[] groupId) {
        return ((ProcessRepository) getRepo()).findByGroup(groupId);
    }

    public ProcessDoc findByDefinitionKey(String key) {
        return ((ProcessRepository) getRepo()).findByDefinitionKey(key);
    }

    public ProcessDoc findByDefinitionKeyAndVersion(String pDefKey, int processVersion) {
              return ((ProcessRepository) getRepo()).findByDefinitionKeyAndVersion(pDefKey,processVersion);
     }
    
    
    public static void main(String[] args) {
        ProcessManager pm = new ProcessManager();
        ProcessDoc pdoc = pm.findByDefinitionKeyAndVersion("PTB", 1);
        System.out.println(pdoc.getVersion());
    }

}
