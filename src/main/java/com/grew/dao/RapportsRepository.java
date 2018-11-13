package com.grew.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ektorp.Attachment;
import org.ektorp.AttachmentInputStream;
import com.grew.model.DataFlowDoc;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import com.grew.model.ReportDoc;
import com.grew.utils.StreamUtil;

/**
 *
 * @author bashizip
 */
public class RapportsRepository extends CouchDbRepositorySupport<ReportDoc> {

    public RapportsRepository(CouchDbConnector db) {
        super(ReportDoc.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public ReportDoc findByReportKey(String reportKey) {

        List<ReportDoc> rdocs = queryView("by_reportKey", reportKey);
        ReportDoc rdoc = null;

        if (rdocs == null || rdocs.isEmpty()) {
            return new ReportDoc();
        }

        rdoc = rdocs.get(0);

        try {
            String attachementId = rdoc.getAttachments().keySet().iterator().next();

            File file = getJasperFile(rdoc.getId(), attachementId);
            rdoc.setJasperFile(file);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return rdoc;
    }

    /**
     *
     * @param docId
     * @param rapportId
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public File getJasperFile(String docId, String rapportId) throws FileNotFoundException, IOException {
        AttachmentInputStream ais = db.getAttachment(docId, rapportId); //TODO use .bin
        File rapportFile = StreamUtil.stream2file(ais, ".jrxml"); //TODO replace with .jasper
        ais.close();
        return rapportFile;
    }
}
