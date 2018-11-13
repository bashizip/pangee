/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

import com.grew.model.SettingsDoc;

/**
 *
 * @author bash
 */
public class SettingsHandler {

    GlobalSettingsManager globalSettings = new GlobalSettingsManager();
    LocalSettingsManager localSettings = new LocalSettingsManager();

    public Object get(Object key) {

        Object valueGlobal = globalSettings.get(key);
        if (valueGlobal == null) {
            return null;
        }
        Object valueLocal = localSettings.get(key);
        if (valueLocal == null) {
            return valueGlobal;
        } else {
            return valueLocal;
        }
    }

    public SettingsDoc getLocalnstance() {
        return localSettings.findInstance();
    }

    public SettingsDoc getGlobalnstance() {
        return globalSettings.findInstance();
    }

    public void updateValue(String key, Object newValue) {
        localSettings.update(key, newValue);
    }
}
