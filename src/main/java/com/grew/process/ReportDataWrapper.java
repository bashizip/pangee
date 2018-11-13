/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.process;

import java.util.Map;
import com.grew.model.ReportDoc;
import com.grew.model.SettingsDoc;
import com.grew.model.UserDoc;

/**
 *
 * @author bash
 */
public class ReportDataWrapper {

    private ReportDoc rapportDoc;
    private SettingsDoc appSettings;
    private UserDoc user;
    private Map values;

    public ReportDataWrapper() {
    }

    public ReportDoc getRapportDoc() {
        return rapportDoc;
    }

    public void setRapportDoc(ReportDoc rapportDoc) {
        this.rapportDoc = rapportDoc;
    }

    public SettingsDoc getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(SettingsDoc appSettings) {
        this.appSettings = appSettings;
    }

    public UserDoc getUser() {
        return user;
    }

    public void setUser(UserDoc user) {
        this.user = user;
    }

    public Map getValues() {
        return values;
    }

    public void setValues(Map values) {
        this.values = values;
    }

}
