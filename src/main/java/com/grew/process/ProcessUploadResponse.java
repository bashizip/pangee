/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.process;

/**
 *
 * @author Administrateur
 */
public class ProcessUploadResponse {
    
    private String fileName;
    private String fileUri;

    public ProcessUploadResponse() {
    }

    public ProcessUploadResponse(String fileName, String fileUri) {
        this.fileName = fileName;
        this.fileUri = fileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
    
    
    
}
