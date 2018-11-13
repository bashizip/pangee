/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.model;

/**
 *
 * @author Administrateur
 */
public class RollbackBean {

    private String startBefore;
    private String cancelFrom;
    private String reason;

    public RollbackBean() {
    }

    public String getStartBefore() {
        return startBefore;
    }

    public void setStartBefore(String startBefore) {
        this.startBefore = startBefore;
    }

    public String getCancelFrom() {
        return cancelFrom;
    }

    public void setCancelFrom(String cancelFrom) {
        this.cancelFrom = cancelFrom;
    }

   
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
