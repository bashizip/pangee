package com.grew.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.ektorp.Attachment;
import org.ektorp.AttachmentInputStream;
import com.grew.couchdb.CoushDB;
import com.grew.dao.TaskRepository;
import com.grew.model.TaskDoc;
import org.ektorp.support.CouchDbRepositorySupport;
import com.grew.couchdb.DBRefs;

/**
 *
 * @author bashizip
 */
public class TaskManager extends ICrud<TaskDoc> {

    public TaskManager() {

    }

    @Override
    public CouchDbRepositorySupport<TaskDoc> getRepo() {

        if (repo == null) {
            CoushDB coushDB = new CoushDB(DBRefs.localDB());
            repo = new TaskRepository(coushDB.getDB());
        }
        return repo;

    }

    public TaskDoc findByTaskId(String pid) {

        List<TaskDoc> tasks = null;
        TaskRepository taskRepo = ((TaskRepository) getRepo());

        tasks = taskRepo.findById(pid);

        TaskDoc u = null;
        if (tasks != null && !tasks.isEmpty()) {
            u = tasks.get(0);

        }
        return u;

    }


    @Override
    public List<TaskDoc> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
