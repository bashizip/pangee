/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.service;

import java.io.Serializable;

/**
 *
 * @author bash
 */
public class SearchBean implements Serializable {

    private String localId;

    public SearchBean() {
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

}
