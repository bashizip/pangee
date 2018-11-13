/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import com.grew.couchdb.CoushDB;
import com.grew.dao.EntityRepository;
import com.grew.model.EntityDoc;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.dao.DataFlowRepository;
import com.grew.dao.DatabaseViewNotFound;
import com.grew.model.EntityDoc;

/**
 *
 * @author bashizip
 */
public class EntityManager extends ICrud<EntityDoc> {

    @Override
    public CouchDbRepositorySupport<EntityDoc> getRepo() {
        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.entitiesDB());
            repo = new EntityRepository(coushDB.getDB());
        }
        return repo;

    }

    public List<EntityDoc> searchByNames(String[] ckey) {

        List<EntityDoc> list = null;

        EntityRepository sRepo = ((EntityRepository) getRepo());

        list = sRepo.searchByNames(ckey);

        return list;
    }

    @Override
    public List<EntityDoc> findAll() {

        ViewQuery q = new ViewQuery().allDocs().includeDocs(false);

        List<EntityDoc> bulkLoaded = cdb.getDB().queryView(q, EntityDoc.class);

        return bulkLoaded;
    }

    public List<EntityDoc> findByProcessInstanceId(String executionId) {

        List<EntityDoc> list = null;
        EntityRepository sRepo = ((EntityRepository) getRepo());

        list = sRepo.findByProcessInstanceId(executionId);

        return list;
    }

    public SearchResult searchByEntityID(String id) {

        SearchResult v = null;
        EntityRepository sRepo = ((EntityRepository) getRepo());
        v = sRepo.searchByEntityID(id);

        return v;
    }

    public List<SearchResult> searchEntitiesByX(String entityType, String[] complexKey, String[] v) throws DatabaseViewNotFound {

        List<SearchResult> list = null;
        EntityRepository sRepo = ((EntityRepository) getRepo());
        list = sRepo.searchByX(entityType, complexKey, v);

        return list;
    }

    public List<SearchResult> searchEntitiesByLike(String entityType, String key, String like) throws DocumentNotFoundException, DatabaseViewNotFound {

        List<SearchResult> list = new ArrayList<>();
        EntityRepository sRepo = ((EntityRepository) getRepo());

        String[] likes = like.split(" ");

        for (String s : likes) {

            List<SearchResult> currList = sRepo.findByKeyWord(entityType, key, s);

            for (SearchResult sr : currList) {
                Map values = (Map) sr.getResult();
                String _id = values.get("_id").toString();
                if (!mycontains(list, sr)) {
                    list.add(sr.parse());
                }
            }

        }

        return list;
    }

    private boolean mycontains(List<SearchResult> list, SearchResult test) {
        boolean contains = false;
        for (SearchResult sr : list) {
            Map values = (Map) sr.getResult();
            Map valuesTest = (Map) sr.getResult();
            if (values.get("_id").toString().equals(valuesTest.get("_id").toString())) {
                return true;

            }
        }
        return false;
    }

    public int findByRaisonSociale(String rs) {
        List<EntityDoc> dfs = null;
        EntityRepository dfRepo = ((EntityRepository) getRepo());

        dfs = dfRepo.findByRaisonSociale(rs);

        return dfs.size();
    }

    public List<EntityDoc> findByEntityType(String type) {

        List<EntityDoc> dfs = null;
        EntityRepository dfRepo = ((EntityRepository) getRepo());

        dfs = dfRepo.findByEntityType(type);

        return dfs;

    }

    public EntityDoc findByLocalId(String localId) throws DatabaseViewNotFound {
        EntityDoc dfs = null;
        EntityRepository dfRepo = ((EntityRepository) getRepo());
        dfs = dfRepo.findBylocalId(localId);
        return dfs;
     }

}
