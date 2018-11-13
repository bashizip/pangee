package com.grew.delegate.special;

import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.grew.control.DataFlowManager;
import com.grew.control.LocalSettingsManager;
import com.grew.model.DataFlowDoc;
import com.grew.utils.DateUtils;
import com.grew.utils.SettingsUtils;
import com.grew.utils.StringUtilities;

/**
 *
 * @author bash
 */
public class IDGenDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        LocalSettingsManager sm = new LocalSettingsManager();
        Object indexObj = sm.get(SettingsUtils.businessKeyIndex);
        int index = 0;

        if (indexObj == null) {
            index = 0;
        } else {
            index = Integer.valueOf(indexObj.toString());
        }

        String noms = execution.getVariable("nomComplet").toString();
        String csdt = execution.getVariable("csdt").toString();

        String nameInitials = " ";
        String csdtInit = " ";

        for (String s : noms.split(" ")) {
            nameInitials += s.toUpperCase().charAt(0);

        }
        for (String s : csdt.split(" ")) {
            csdtInit += s.toUpperCase().charAt(0);
        }

        String numF = StringUtilities.getZeroFilledNumber(index, 4);
        String ptID = csdtInit.trim() + "/" + nameInitials.trim() + "/" + numF;

        execution.setVariable("participantId", ptID.trim());

        //apply rccm to dataflowDoc
        DataFlowManager tm = new DataFlowManager();
        DataFlowDoc dflow = tm.findByProcessInstanceId(execution.getProcessInstanceId());

        Map<Object, Object> currentValues = dflow.getPending_values();
        Map<Object, Object> currentEnt = dflow.getCurrent_entity();

        Map<Object, Object> currentEntValues = (Map<Object, Object>) currentEnt.get("values");

        currentValues.put("participantId", ptID.trim());
        currentEntValues.put("participantId", ptID.trim());

        dflow.setPending_values(currentValues);
        currentEnt.put("values", currentEntValues);
        dflow.setCurrent_entity(currentEnt);
        tm.edit(dflow);

    }

}
