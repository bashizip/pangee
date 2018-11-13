/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.dao;

import java.util.List;
import com.grew.model.FragmentDoc;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

/**
 *
 * @author bashizip
 */
public class FragmentRepository extends CouchDbRepositorySupport<FragmentDoc> {

    public FragmentRepository(CouchDbConnector db) {
        super(FragmentDoc.class, db);

    }

    @GenerateView
    public List<FragmentDoc> findByEntityId(String id) {
        return queryView("by_entityId", id);
    }

    @GenerateView
    public List<FragmentDoc> findByExecutionId(String processInstanceId) {
        return queryView("by_executionId", processInstanceId);
    }

}
