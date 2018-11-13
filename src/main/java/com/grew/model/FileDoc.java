/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.IOException;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import org.ektorp.Attachment;
import org.ektorp.AttachmentInputStream;
import org.ektorp.support.CouchDbDocument;
import com.grew.couchdb.CoushDB;
import com.grew.couchdb.DBRefs;

/**
 *
 * @author bash
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileDoc extends CouchDbDocument {

    String entityId;
    String field;
    String date;
    String site;
    String businessKey;

    public FileDoc() {
    }

   public void attachDoc(String docId,String key, Attachment attachment) {
      
        CoushDB cdb = new CoushDB(DBRefs.localDB());     
        
        AttachmentInputStream data = cdb.getDB().getAttachment(docId, key);  
        Attachment att = null;

        try {
            
            byte[] bytes = IOUtils.toByteArray(data);
            String encoded = Base64.getEncoder().encodeToString(bytes);

            att = new Attachment(key, encoded, attachment.getContentType());
            
            addInlineAttachment(att);

            data.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
   
   
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

}
