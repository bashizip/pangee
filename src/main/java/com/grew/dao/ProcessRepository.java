package com.grew.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import com.grew.model.ProcessDoc;

/**
 *
 * @author bashizip
 */
public class ProcessRepository extends CouchDbRepositorySupport<ProcessDoc> {

    public ProcessRepository(CouchDbConnector db) {
        super(ProcessDoc.class, db);
        initStandardDesignDocument();
    }

    public List<ProcessDoc> findAll() {
        return queryView("all_processes");
    }

    @GenerateView
    public List<ProcessDoc> findByGroup(Object[] groupId) {
       ComplexKey ck = ComplexKey.of(groupId);
        List<ProcessDoc> pdoc = new ArrayList<>();
  
            List<ProcessDoc> p = queryView("by_group",ck);
           // pdoc.addAll(p);
        
        return p;
    }

    @GenerateView
    public ProcessDoc findByDefinitionKey(String key) {
        List<ProcessDoc> list = queryView("by_definitionKey", key);
        if (list != null) {
            if (!list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    public ProcessDoc findByDefinitionKeyAndVersion(String pDefKey, int processVersion) {
        List<ProcessDoc> list = queryView("by_definitionKey_version", ComplexKey.of(pDefKey, processVersion));
        if (list != null) {
            if (!list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

}
