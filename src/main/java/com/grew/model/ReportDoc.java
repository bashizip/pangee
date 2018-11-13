/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.model;

import java.io.File;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author pbash
 */
public class ReportDoc extends CouchDbDocument {

    private String type;
    private String reportKey;
    private File jasperFile;
    private String pdf;
    
    public ReportDoc() {
    }

    public String getType() {
        return type;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReportKey() {
        return reportKey;
    }

    public void setReportKey(String reportKey) {
        this.reportKey = reportKey;
    }

    public File getJasperFile() {
        return jasperFile;
    }

    public void setJasperFile(File jasperFile) {
        this.jasperFile = jasperFile;
    }

   

}
