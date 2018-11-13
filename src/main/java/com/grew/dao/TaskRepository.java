package com.grew.dao;

import java.io.InputStream;
import java.util.List;
import org.ektorp.AttachmentInputStream;
import com.grew.model.TaskDoc;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

/**
 *
 * @author bashizip
 */
public class TaskRepository extends CouchDbRepositorySupport<TaskDoc> {

    public TaskRepository(CouchDbConnector db) {
        super(TaskDoc.class, db);
    }

    @GenerateView
    public List<TaskDoc> findById(String taskId) {
        return queryView("by_taskId", taskId);
    }


}
