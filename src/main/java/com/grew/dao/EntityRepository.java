/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.dao;

import io.jsonwebtoken.lang.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

import org.ektorp.ComplexKey;
import com.grew.model.EntityDoc;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import com.grew.control.SearchResult;
import com.grew.utils.ExecutionUtils;
import com.grew.utils.StringUtilities;

/**
 *
 * @author bashizip
 */
public class EntityRepository extends CouchDbRepositorySupport<EntityDoc> {

    public EntityRepository(CouchDbConnector db) {
        super(EntityDoc.class, db);
        initStandardDesignDocument();

    }

    /**
     *
     * @param ckey
     * @return
     */
    public List<EntityDoc> searchByNames(String[] ckey) {

        List<EntityDoc> list = null;
        ViewQuery query = createQuery("by_noms")
                .dbPath(db.path())
                .designDocId(stdDesignDocumentId)
                .keys(Arrays.asList(ckey));
        list = db.queryView(query, EntityDoc.class);
        return list;
    }

    public SearchResult searchByEntityID(String id) {

        ViewQuery query = createQuery("by_id")
                .dbPath(db.path())
                .designDocId(stdDesignDocumentId)
                .key(id);

        ViewResult vr = db.queryView(query);
        List<ViewResult.Row> rows = vr.getRows();
        
        if (rows.isEmpty()) {
            return null;
        }
        
        return new SearchResult(rows.get(0).getKey(), ExecutionUtils.jsonStringToMapValues(rows.get(0).getValue())).parse();

    }

    /**
     *
     * @param entityType
     * @param values
     * @param complexKey
     * @return
     * @throws com.grew.dao.DatabaseViewNotFound
     */
    public List<SearchResult> searchByX(String entityType, String[] complexKey, String[] values) throws DatabaseViewNotFound {

        ViewQuery query;
        List<SearchResult> searchResLit = null;

        try {

            String fullQuery = "by_entityType_" + StringUtilities.parseMethodNameParams(complexKey);

            List valuesFinal = new ArrayList();
            valuesFinal.add(0, entityType.toLowerCase());

            for (String v : values) {
                String wa = StringUtils.stripAccents(v);
                valuesFinal.add(wa.toLowerCase());
            }

            Object[] mArray = valuesFinal.toArray();

            ComplexKey keys = ComplexKey.of(mArray);

            query = createQuery(fullQuery)
                    .dbPath(db.path())
                    .designDocId(stdDesignDocumentId)
                    .key(keys).includeDocs(true);

            ViewResult vr = db.queryView(query);
            List<ViewResult.Row> rows = vr.getRows();

            searchResLit = new ArrayList();

            for (ViewResult.Row row : rows) {
                //System.out.println("Key--->" + row.getKey());
                // System.out.println("Value--->" + row.getValue());
                searchResLit.add(new SearchResult(row.getKey(), ExecutionUtils.jsonStringToMapValues(row.getValue())).parse());
            }

        } catch (Exception e) {
            throw new DatabaseViewNotFound(e.getMessage());
        }

        return searchResLit;
    }

    public List<EntityDoc> findByProcessInstanceId(String executionId) {
        return queryView("by_processInstanceId", executionId);
    }

    public List<SearchResult> findByKeyWord(String entityType, String key, String like) {

        String fullQuery = "by_" + key.toLowerCase();

        List valuesFinal = new ArrayList();
        valuesFinal.add(0, entityType.toLowerCase());
        valuesFinal.add(like.replaceAll("\\p{Z}", "").toLowerCase());
        Object[] mArray = valuesFinal.toArray();
        ComplexKey complexKey = ComplexKey.of(mArray);

        ViewQuery query = createQuery(fullQuery)
                .dbPath(db.path())
                .designDocId(stdDesignDocumentId)
                .key(complexKey).includeDocs(true);

        query.setIgnoreNotFound(true);

        List<SearchResult> searchResLit = null;

        ViewResult vr = db.queryView(query);
        List<ViewResult.Row> rows = vr.getRows();

        searchResLit = new ArrayList();

        for (ViewResult.Row row : rows) {
            searchResLit.add(new SearchResult(row.getKey(), ExecutionUtils.jsonStringToMapValues(row.getValue())));
        }

        return searchResLit;
    }

    /**
     *
     * @param entityType
     * @param params
     * @param values
     * @return
     * @throws com.grew.dao.DatabaseViewNotFound
     */
    public List<SearchResult> searchByLike(String entityType, String[] params, String[] values) throws DatabaseViewNotFound {

        ViewQuery query;
        List<SearchResult> searchResLit = null;

        try {

            String fullQuery = "by_entityType_like_" + StringUtilities.parseMethodNameParams(params);

            List valuesFinal = new ArrayList();
            valuesFinal.add(0, entityType);

            for (String v : values) {
                valuesFinal.add(v.toLowerCase());
            }

            Object[] mArray = valuesFinal.toArray();

            ComplexKey keys = ComplexKey.of(mArray);

            query = createQuery(fullQuery)
                    .dbPath(db.path())
                    .designDocId(stdDesignDocumentId)
                    .key(keys).includeDocs(false);

            ViewResult vr = db.queryView(query);
            List<ViewResult.Row> rows = vr.getRows();

            searchResLit = new ArrayList();

            for (ViewResult.Row row : rows) {
                System.out.println("Key--->" + row.getKey());
                System.out.println("Value--->" + row.getValue());
                searchResLit.add(new SearchResult(row.getKey(), ExecutionUtils.jsonStringToMapValues(row.getValue())).parse());
            }

        } catch (Exception e) {
            throw new DatabaseViewNotFound(e.getMessage());
        }

        return searchResLit;
    }

    public List<EntityDoc> findByRaisonSociale(String rs) {
        return queryView("by_raisonSociale", rs);
    }
  public List<EntityDoc> findByEntityType(String rs) {
        return queryView("by_entityType", rs);
    }

    public EntityDoc findBylocalId(String localId) throws DatabaseViewNotFound{
         return queryView("by_localId", localId).isEmpty()?null:queryView("by_localId", localId).get(0);
     }
}
