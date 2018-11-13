package com.grew.control;

import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.List;
import com.grew.couchdb.CoushDB;
import com.grew.dao.UserRepository;
import com.grew.model.UserDoc;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.model.codification.FieldDoc;

/**
 *
 *
 * @author bashizip
 */
public class UserManager extends ICrud<UserDoc> {
    
    public UserManager() {
        
    }
    
    public UserDoc login(String username, String password) {
        
         List<UserDoc> list  = findByUsername(username);
         UserDoc u=null;
      
         if(list.size()==1){
            u=list.get(0);
        }
      
        if (u!=null &&u.getPassword().equals(password)) {
            return u;
            
        }
        return null;
        
    }
    
    public List<UserDoc> findByUsername(String username) {
        List<UserDoc> founds= 
        ((UserRepository) getRepo()).findByUsername(username);
        
             return founds;
    }
    
    @Override
    public List<UserDoc> findAll() {
        return ((UserRepository) getRepo()).findAll();
    }
    
    @Override
    public CouchDbRepositorySupport<UserDoc> getRepo() {
        
        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.localDB());
            repo = new UserRepository(coushDB.getDB());
            
        }
        return repo;
    }
    
    public static void main(String[] args) {
        
        UserManager fm = new UserManager();
        List<UserDoc> fdocs = fm.findAll();
        
        for (UserDoc fd : fdocs) {
            fd.setType(UserDoc.class.getSimpleName());
            fd.setSettings(new HashMap<>());
            fm.edit(fd);
        }
    }
    
}
