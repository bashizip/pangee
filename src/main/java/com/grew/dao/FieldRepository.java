package com.grew.dao;

import java.util.List;
import com.grew.model.codification.FieldDoc;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

/**
 *
 * @author bashizip
 */
public class FieldRepository extends CouchDbRepositorySupport<FieldDoc> {

    public FieldRepository(CouchDbConnector db) {
        super(FieldDoc.class, db);
       initStandardDesignDocument();
    }

    @GenerateView
    public List<FieldDoc> byType(String type){
           List<FieldDoc> list= queryView("by_type");
           return list;
    }
     @GenerateView
    public List<FieldDoc> byName(String type){
           List<FieldDoc> list= queryView("by_name");
           return list;
    }
    

    public List<FieldDoc> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 
  
}
