/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

import com.grew.couchdb.CoushDB;
import java.util.List;
import org.ektorp.support.CouchDbRepositorySupport;

/**
 *
 * @author bashizip
 * @param <T>
 */
public abstract class ICrud<T> {

    CouchDbRepositorySupport<T> repo;
    CoushDB cdb;

    public ICrud() {

    }

    public void create(T entity) {
        getRepo().add(entity);
    }

    public void edit(T entity) {
        getRepo().update(entity);
    }

    public void remove(T entity) {
        getRepo().remove(entity);
    }

    public T find(String id) {
        return getRepo().get(id);
    }

    public abstract List<T> findAll();

    public int deleteAll() {
        int index = 0;
        for (T ent : findAll()) {
            remove(ent);
            index++;
        }
        return index;
    }

    public abstract CouchDbRepositorySupport<T> getRepo();
}
