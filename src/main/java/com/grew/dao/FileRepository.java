package com.grew.dao;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import com.grew.model.FileDoc;
import com.grew.model.DataFlowDoc;
import com.grew.model.codification.CodeDoc;


/**
 *
 * @author bashizip
 */
public class FileRepository extends CouchDbRepositorySupport<FileDoc> {

    public FileRepository(CouchDbConnector db) {
        super(FileDoc.class, db);
       initStandardDesignDocument();
    }

    public List<FileDoc> findByEntityId(String entityId) {
          return queryView("by_entityId", entityId);
    }

}
