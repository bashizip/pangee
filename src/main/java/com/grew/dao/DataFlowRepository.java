package com.grew.dao;

import java.util.List;
import com.grew.model.DataFlowDoc;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

/**
 *
 * @author bashizip
 */
public class DataFlowRepository extends CouchDbRepositorySupport<DataFlowDoc> {

    public DataFlowRepository(CouchDbConnector db) {
        super(DataFlowDoc.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public List<DataFlowDoc> findByProcessInstanceId(String execId) {
        return queryView("by_processInstanceId", execId);
    }

    public List<DataFlowDoc> findByEntityId(String entityId) {
        return queryView("by_entityId", entityId);
    }

    public List<DataFlowDoc> findByRaisonSociale(String rs) {
        return queryView("by_raisonSociale", rs);
    }

}
