/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.dao;

import java.util.List;
import com.grew.model.FlowDoc;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

/**
 *
 * @author bashizip
 */
public class FlowRepository extends CouchDbRepositorySupport<FlowDoc> {

    public FlowRepository(CouchDbConnector db) {
        super(FlowDoc.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public List<FlowDoc> findByProcessInstanceId(String processInstanceId) {
        return queryView("by_processInstanceId", processInstanceId);
    }

     @GenerateView
    public List<FlowDoc> findByEntityId(String entity) {
         return queryView("by_entity", entity);
    }
}
