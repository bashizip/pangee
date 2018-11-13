package com.grew.control;


import java.util.List;
import com.grew.couchdb.CoushDB;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.dao.DictionaryRepository;
import com.grew.model.codification.DictionaryDoc;

/**
 *
 *
 * @author bashizip
 */
public class DictonnaryManager extends ICrud<DictionaryDoc> {

    public DictonnaryManager() {

    }

    @Override
    public CouchDbRepositorySupport<DictionaryDoc> getRepo() {

        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.schemaDB());
            repo = new DictionaryRepository(coushDB.getDB());
        }
        return repo;
    }

    @Override
    public List<DictionaryDoc> findAll() {
        return ((DictionaryRepository) getRepo()).findAll();
    }
    
   
    public List<DictionaryDoc> findByName(String name) {
        return ((DictionaryRepository) getRepo()).findByName(name);
    }


}
