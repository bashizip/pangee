/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.formserver;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.ektorp.ViewQuery;
import com.grew.couchdb.CoushDB;
import com.grew.couchdb.DBRefs;
import com.grew.utils.StreamUtil;

/**
 *
 * @author bash
 */
public class SchemaDataSource {

    private List<LinkedTreeMap<String, Object>> forms;
    private List<LinkedTreeMap<String, Object>> codes;
    private List<LinkedTreeMap<String, Object>> dictionaries;
    private LinkedTreeMap<String, Object> parameters;

    public String getForm() throws IOException {
        CoushDB coushDB = new CoushDB(DBRefs.schemaDB());
        List<LinkedTreeMap<String, Object>> res = null;
        ViewQuery query = new ViewQuery()
                .designDocId("_design/JsonSchemaDoc")
                .viewName("by_formKey").key("saisie_FK").includeDocs(true);

        InputStream data = coushDB.getDB().queryForStream(query);
        
        String dataS = IOUtils.toString(data);
        
       // System.out.println(dataS);

      

        return dataS;
    }

    public static void main(String[] args) throws IOException {
        String size = new SchemaDataSource().getForm();
        System.out.println(size);
    }
}
