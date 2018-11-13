/*
 * Copyright 2017 pbashizi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grew.delegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.grew.control.DataFlowManager;
import com.grew.control.EntityManager;
import com.grew.control.FlowManager;
import com.grew.control.FragmentManager;
import com.grew.control.ProcessManager;
import com.grew.model.DataFlowDoc;
import com.grew.model.EntityDoc;
import com.grew.model.FlowDoc;
import com.grew.model.FragmentDoc;
import com.grew.model.ProcessDoc;
import com.grew.utils.DateUtils;
import com.grew.utils.IdGenerator;
import com.grew.utils.UUIDGenerator;
import org.jfree.date.DateUtilities;

/**
 *
 * @author pbashizi
 */
public class FinalizationDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        // A ce niveau le df est sensé deja exister et avoir probablement été mis à jour
        // par des ajouts post_validation explicites
        new ValidationDelegate().execute(execution);

        createOrUpdateFlowDoc(execution);

        execution.removeVariables();

        //TODO delete DataFlowDoc of this executionId
    }

    public void createOrUpdateFlowDoc(DelegateExecution execution) {

        //find the entity
        EntityManager em = new EntityManager();
        EntityDoc found = null;

        List<EntityDoc> eds = em.findByProcessInstanceId(execution.getProcessInstanceId());

        String pDefKey = execution.getProcessInstance().getProcessEngineServices().getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(execution.getProcessDefinitionId())
                .list().get(0).getKey();

        ProcessManager pm = new ProcessManager();
        
        
        ProcessDoc pdoc = pm.findByDefinitionKey(pDefKey);

        String mainEntityTypeName = pdoc.getMainEntityKey();

        for (EntityDoc e : eds) {
            if (e.getEntityType().equals(mainEntityTypeName)) {
                found = e;
            }
        }
        if (found == null) {
            throw new RuntimeException("Trying to create a FlowDoc from non existing entity Entity");
        }

        DataFlowManager dfm = new DataFlowManager();
        DataFlowDoc dfd = dfm.findByProcessInstanceId(execution.getProcessInstanceId());

        FragmentManager fdm = new FragmentManager();

        EntityDoc ed = found;

        FlowManager flowManager = new FlowManager();

        FlowDoc flowDoc = flowManager.findByProcessInstanceId(execution.getProcessInstanceId());

        if (flowDoc == null) {
            //create flowDoc     

            flowDoc = new FlowDoc();
   
            //ent.kng.jur.45678:flw.kng.17.1
            flowDoc.setId(IdGenerator.getNexFlowId(ed.getId(),dfd.getBusinessKey()));

            flowDoc.setType(FlowDoc.class.getSimpleName());
            flowDoc.setBusinessKey(dfd.getBusinessKey());

            flowDoc.setCreated_on(DateUtils.getNowGMT());
            flowDoc.setDefinitionId(dfd.getDefinitionId());

            flowDoc.setEntityId(ed.getId());
            Map currFv = dfd.getFlow_variables();

            flowDoc.setFlow_variables(currFv);

            List<FragmentDoc> fragments = fdm.findByExecutionId(dfd.getProcessInstanceId());

            List<String> fids = new ArrayList<>();

            for (FragmentDoc fdoc : fragments) {
                if (fdoc.getEntityType().equals(mainEntityTypeName)) {
                    fids.add(fdoc.getId());
                }
            }

            flowDoc.setFragments(fids);

            flowDoc.setExecutionId(execution.getId());
            flowDoc.setProcessInstanceId(execution.getProcessInstanceId());

            flowManager.create(flowDoc);

        } else {

            Map currFv = flowDoc.getFlow_variables();
            currFv.putAll(dfd.getFlow_variables());
            flowDoc.setFlow_variables(currFv);

            List<FragmentDoc> fragments = fdm.findByExecutionId(dfd.getProcessInstanceId());

            List<String> fids = new ArrayList<>();

            for (FragmentDoc fdoc : fragments) {
                 if (fdoc.getEntityType().equals(mainEntityTypeName)) {
                    fids.add(fdoc.getId());
                }
            }

            flowDoc.setFragments(fids);
            flowManager.edit(flowDoc);

        }
    }
}
