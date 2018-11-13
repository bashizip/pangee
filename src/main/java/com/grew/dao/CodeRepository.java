package com.grew.dao;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import com.grew.model.codification.CodeDoc;


/**
 *
 * @author bashizip
 */
public class CodeRepository extends CouchDbRepositorySupport<CodeDoc> {

    public CodeRepository(CouchDbConnector db) {
        super(CodeDoc.class, db);
       initStandardDesignDocument();
    }

    @GenerateView
    public List<CodeDoc> findByType(String type){
           List<CodeDoc> list= queryView("by_type");
           return list;
    }
    
   

    public List<CodeDoc> findAll() {
         List<CodeDoc> list= queryView("all");
         return list;
     }
  
}
