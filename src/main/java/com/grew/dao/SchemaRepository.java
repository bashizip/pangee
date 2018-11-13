package com.grew.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import com.grew.couchdb.CoushDB;
import com.grew.couchdb.DBRefs;
import com.grew.model.JsonSchemaDoc;
import com.grew.utils.ExecutionUtils;

/**
 *
 * @author bashizip
 */
public class SchemaRepository extends CouchDbRepositorySupport<JsonSchemaDoc> {

    public SchemaRepository(CouchDbConnector db) {
        super(JsonSchemaDoc.class, db);
    }

    public List<JsonSchemaDoc> findByFormKey(String key) {

        return queryView("by_formKey", key);

    }
}
