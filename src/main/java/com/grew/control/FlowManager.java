/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.grew.couchdb.CoushDB;
import com.grew.dao.FlowRepository;
import com.grew.model.FlowDoc;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;
import com.grew.dao.EntityRepository;
import com.grew.model.EntityDoc;

/**
 *
 * @author bashizip
 */
public class FlowManager extends ICrud<FlowDoc> {

    @Override
    public CouchDbRepositorySupport<FlowDoc> getRepo() {

        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.entitiesDB());
            repo = new FlowRepository(coushDB.getDB());
        }
        return repo;
    }

    @Override
    public List<FlowDoc> findAll() {
      return getRepo().getAll();
    }

    public FlowDoc findByProcessInstanceId(String processInstanceId) {
        List<FlowDoc> list = new ArrayList<>();
        FlowRepository sRepo = ((FlowRepository) getRepo());
        list = sRepo.findByProcessInstanceId(processInstanceId);
        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
    }
    
     public List<FlowDoc> findByEntityId(String entityId) {
        List<FlowDoc> list = new ArrayList<>();
        FlowRepository sRepo = ((FlowRepository) getRepo());
        list = sRepo.findByEntityId(entityId);
    
        return list;
    }

}
