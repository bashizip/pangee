package com.grew.control;

import com.itextpdf.text.pdf.PdfName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grew.couchdb.CoushDB;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.dao.SettingsRepository;
import com.grew.model.SettingsDoc;

/**
 *
 *
 * @author bashizip
 */
public class GlobalSettingsManager extends ICrud<SettingsDoc> {
    private static final String SETTINGS_ID = "settings";
      
    public GlobalSettingsManager() {

    }

    @Override
    public CouchDbRepositorySupport<SettingsDoc> getRepo() {

        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.schemaDB());
            repo = new SettingsRepository(coushDB.getDB());
        }
        return repo;
    }

    @Override
    public List<SettingsDoc> findAll() {
        return getRepo().getAll();
    }


    public  SettingsDoc findInstance() {
        SettingsDoc sdoc = find(SETTINGS_ID);
        if (sdoc == null) {
            sdoc = new SettingsDoc();
            sdoc.setId("settings");
            create(sdoc);
            return sdoc;
        }
        return sdoc;
    }

    public Map<Object, Object> getValues() {
        return findInstance().getValues();
    }

    public Object get(Object key) {
        return getValues().get(key);
    }

}
