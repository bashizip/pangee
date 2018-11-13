package com.grew.control;

import java.util.List;
import com.grew.couchdb.CoushDB;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.dao.FieldRepository;
import com.grew.model.codification.FieldDoc;

/**
 *
 *
 * @author bashizip
 */
public class FieldManager extends ICrud<FieldDoc> {

    public FieldManager() {

    }

    @Override
    public CouchDbRepositorySupport<FieldDoc> getRepo() {

        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.schemaDB());
            repo = new FieldRepository(coushDB.getDB());
        }
        return repo;
    }

    @Override
    public List<FieldDoc> findAll() {
        return ((FieldRepository) getRepo()).findAll();
    }

    private List<FieldDoc> findByType(String field) {
        return ((FieldRepository) getRepo()).byType(field);

    }

    public static void main(String[] args) {
        
        FieldManager fm = new FieldManager();
        List<FieldDoc> fdocs = fm.findAll();
        
        for (FieldDoc fd : fdocs) {
          
            fd.setType(FieldDoc.class.getSimpleName());
            fm.edit(fd);
        }
    }

}
