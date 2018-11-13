package com.grew.dao;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import com.grew.model.codification.DictionaryDoc;

/**
 *
 * @author bashizip
 */
public class DictionaryRepository extends CouchDbRepositorySupport<DictionaryDoc> {

    public DictionaryRepository(CouchDbConnector db) {
        super(DictionaryDoc.class, db);
       initStandardDesignDocument();
    }

    
    @GenerateView
    public List<DictionaryDoc> findByName(String type){
           List<DictionaryDoc> list= queryView("by_name",type);
           return list;
    }
    
   

    public List<DictionaryDoc> findAll() {
         List<DictionaryDoc> list= queryView("all_dictionaries");
         return list;
     }
  
}
