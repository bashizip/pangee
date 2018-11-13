package com.grew.delegate.special;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.grew.control.DataFlowManager;
import com.grew.control.EntityManager;
import com.grew.model.DataFlowDoc;

/**
 *
 * @author bashizip
 */
public class VerifyRSDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        //log(execution);
        Map<String, Object> variables = execution.getVariables();

        Object rsO = variables.get("raisonSociale");
        boolean valid = true;

        if (rsO != null) {
            String rs = rsO.toString();

            //check in dataflow
            DataFlowManager dfm = new DataFlowManager();
            List<DataFlowDoc> list = dfm.findByRaisonSociale(rs);  //must always be <=1

            List<DataFlowDoc> filtered = new ArrayList<>();
            String pid = execution.getProcessInstanceId();

            for (DataFlowDoc dataFlowDoc : list) {
                if (dataFlowDoc.getProcessInstanceId() != null) {
                    if (!dataFlowDoc.getProcessInstanceId().equals(pid)) {
                        filtered.add(dataFlowDoc);
                    }
                }
            }

            // filtered = list.stream().filter(pidx -> !pidx.getProcessInstanceId().equals(pid)).collect(Collectors.toList());
            int dfCount = filtered.size();

            EntityManager em = new EntityManager();
            int entCount = em.findByRaisonSociale(rs);

            if (dfCount > 0 || entCount > 0) {
                valid = false;
            }

        } else {
            throw new RuntimeException("variable raisonSociale NOT FOUND !");
        }
        execution.setVariable("raisonSocialeValide", valid);
    }

    private void log(DelegateExecution execution) {

        Logger LOGGER = Logger.getLogger("VerifyRSDelegateLOG");

        LOGGER.info("\n\n ... LoggerDelegate invoked by "
                + "processDefinitionId=" + execution.getProcessDefinitionId()
                + ", activityId=" + execution.getCurrentActivityId()
                + ", activityName='" + execution.getCurrentActivityName().replaceAll("\n", " ") + "'"
                + ", processInstanceId=" + execution.getProcessInstanceId()
                + ", businessKey=" + execution.getProcessBusinessKey()
                + ", executionId=" + execution.getId()
                + ", modelName=" + execution.getBpmnModelInstance().getModel().getModelName()
                + ", elementId" + execution.getBpmnModelElementInstance().getId()
                + " \n\n");
    }

}
