package com.grew.dao;

import com.grew.model.UserDoc;
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

/**
 *
 * @author bashizip
 */
public class UserRepository extends CouchDbRepositorySupport<UserDoc> {

    public UserRepository(CouchDbConnector db) {
        super(UserDoc.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public List<UserDoc> findByUsername(String username) {
        return queryView("by_username", username);
    }

 
    public List<UserDoc> findAll() {
        return queryView("all_users");
    }

 
}
