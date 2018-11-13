/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.delegate.special;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.grew.control.DataFlowManager;
import com.grew.control.SettingsHandler;
import com.grew.model.DataFlowDoc;
import com.grew.utils.DateUtils;
import com.grew.utils.SettingsUtils;
import com.grew.utils.StringUtilities;

/**
 *
 * @author pbash
 */
public class RccmGeneratorDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        //format: CD/KNG/RCCM/17-A-12345
        String country;
        String site;
        String codeName = "RCCM";
        String year;
        String rccmType;
        int index;

        SettingsHandler sh = new SettingsHandler();

        country = sh.get(SettingsUtils.country).toString();
        site = sh.get(SettingsUtils.site).toString();

        DateFormat df = new SimpleDateFormat("yy");
        year = df.format(Calendar.getInstance().getTime());

        Object typeE = execution.getVariable("formeJuridiqueN1");
        
        Map fjN1= (Map) typeE;
        
        Object code = fjN1.get("code");
//
//        if (typeE == null) {
//            return;
//        }

        rccmType = code.toString();
        Map typeEntrepriseMap = (Map) sh.get(SettingsUtils.typeEntrepriseIndex);

        index = (int) typeEntrepriseMap.get(rccmType);

        if (index == 0) {
            index = 1;
        }

        //  sm.findInstance().
        String rccmNum = country.concat("/").concat(site).concat("/").concat(codeName).concat("/")
                .concat(year).concat("-").concat(rccmType).concat("-")
                .concat(StringUtilities.getZeroFilledNumber(index, 5));

        //apply rccm to dataflowDoc
        DataFlowManager tm = new DataFlowManager();
        DataFlowDoc dflow = tm.findByProcessInstanceId(execution.getProcessInstanceId());

        Map<Object, Object> currentValues = dflow.getPending_values();

        currentValues.put("rccm", rccmNum);
        currentValues.put("dateImmatriculationRCCM", DateUtils.getNowGMT());

        execution.setVariable("rccm", rccmNum);
        execution.setVariable("dateImmatriculationRCCM", DateUtils.getNowGMT());

        dflow.setPending_values(currentValues);

        tm.edit(dflow);

        typeEntrepriseMap.put(rccmType, ++index);

        sh.updateValue(SettingsUtils.typeEntrepriseIndex, typeEntrepriseMap);

    }

}
