package com.grew.control;

import java.util.List;
import com.grew.couchdb.CoushDB;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.dao.SchemaRepository;
import com.grew.model.JsonSchemaDoc;

/**
 *
 * @author bashizip
 */
public class SchemasManager extends ICrud<JsonSchemaDoc> {

    public SchemasManager() {

    }

    @Override
    public CouchDbRepositorySupport<JsonSchemaDoc> getRepo() {

        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.schemaDB());
            repo = new SchemaRepository(coushDB.getDB());
        }
        return repo;

    }

    public JsonSchemaDoc findByFormKey(String formKey) {

        List<JsonSchemaDoc> schemas = null;
        
        SchemaRepository sRepo = ((SchemaRepository) getRepo());

        schemas = sRepo.findByFormKey(formKey);

        JsonSchemaDoc u = null;
        
        if (schemas != null && !schemas.isEmpty()) {
            u = schemas.get(0);
        }
        return u;

    }

    @Override
    public List<JsonSchemaDoc> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public static void main(String[] args) {
        System.out.println(new SchemasManager().findByFormKey("saisie_FK").getSchema());
    }
}
