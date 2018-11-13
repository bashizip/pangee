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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.dmn.instance.InputValues;
import org.ektorp.Attachment;
import org.ektorp.DocumentNotFoundException;
import com.grew.control.FileManager;
import com.grew.control.DataFlowManager;
import com.grew.control.DictonnaryManager;
import com.grew.control.EntityManager;
import com.grew.control.FragmentManager;
import com.grew.control.ProcessManager;
import com.grew.control.SearchResult;
import com.grew.control.LocalSettingsManager;
import com.grew.control.SettingsHandler;
import com.grew.model.FileDoc;
import com.grew.model.DataFlowDoc;
import com.grew.model.EntityDoc;
import com.grew.model.EntityValueWrapper;
import com.grew.model.FragmentDoc;
import com.grew.model.ProcessDoc;
import com.grew.model.SettingsDoc;
import com.grew.model.codification.DictionaryDoc;
import com.grew.process.ProcessEngineManager;
import com.grew.utils.IdGenerator;
import com.grew.utils.SettingsUtils;
import org.json.JSONObject;
import static com.grew.utils.CollectionsUtils.isList;
import static com.grew.utils.CollectionsUtils.isMap;
import com.grew.utils.DateUtils;

/**
 *
 * @author pbashizi 
 * Conduit a la creation des fragments ET de l'Entity .
 * A chaque validation un fragment est cree et l'entity est mis a jour (ou creer si elle
 * nexiste pas) a partir de ce fragment L'entity finale est la combinaison de
 * toutes les values validees des fragments Le document FLow generé à la fin du
 * process, retrace juste l'historique des fragments
 */
public class ValidationDelegate implements JavaDelegate {

    private String mainEntityTypeName;
    DelegateExecution execution;

    List<DictionaryDoc> dictionaryDocs;

    @EJB
    ProcessEngineManager processEngineManager;

    Map tmpMap = new HashMap();

    public List<DictionaryDoc> getDictionaryDocs() {
        if (dictionaryDocs == null) {
            dictionaryDocs = new DictonnaryManager().findAll();
        }
        return dictionaryDocs;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        this.execution = execution;
        DataFlowManager dfm = new DataFlowManager();
        DataFlowDoc df = dfm.findByProcessInstanceId(execution.getProcessInstanceId());

        ProcessDefinition pdef = execution.getProcessInstance().getProcessEngineServices().getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(execution.getProcessDefinitionId()).singleResult();

        String pDefKey = pdef.getKey();
        int version = pdef.getVersion();

        ProcessManager pm = new ProcessManager();
        ProcessDoc pdoc = pm.findByDefinitionKeyAndVersion(pDefKey, version);

        if (pdoc == null) {
            version = 1;
        }

        pdoc = pm.findByDefinitionKeyAndVersion(pDefKey, version);

        mainEntityTypeName = pdoc.getMainEntityKey();
        //parse main content and create all sub-fragment
        Map input = (Map) df.getPending_values();
        Map inputFv = df.getFlow_variables();

        inputFv = parse(inputFv, df);

        //here we go
        Map parsed = parse(input, df);

        //create main fragment
        FragmentDoc mainFgt = createFragmentDoc(df, mainEntityTypeName, parsed);
        EntityDoc mainEd = createOrUpdateEntityDoc(mainFgt, df, true);

        createFiles(mainEd, df);

        //update the current id
        EntityManager em = new EntityManager();
        SearchResult sr = em.searchByEntityID(mainEd.getId());

        if (sr != null) {
            
            df.setCurrent_entity((Map<Object, Object>) sr.getResult());
        }

        //delete pending values
        df.setPending_values(Collections.EMPTY_MAP);
        df.setFlow_variables(inputFv);

        dfm.edit(df);

        tmpMap.clear();
    }

    /**
     *
     *
     * @param input
     * @param dfd
     * @return
     */
    public Map parse(Map input, DataFlowDoc dfd) {

        for (Iterator<Map<Object, Object>> iterator = input.keySet().iterator(); iterator.hasNext();) {

            Object next = iterator.next();
            String key = next.toString();

            Object value = input.get(key);

            String subEnt = isSubEntity(value);

            if (subEnt != null) {

                Map m = new LinkedHashMap((Map) value);

                if (m.containsKey("_id")) {

                    if (!m.get("_id").toString().startsWith("tmp")) {

                        //Not tmp, key just attach
                        JSONObject jo = new JSONObject();
                        //remplacer la SE par une reference
                        jo.put("ref", m.get("_id"));
                        input.replace(key, value, jo.toMap());

                    } else {

                        String tmpId = m.get("_id").toString();

                        if (!tmpMap.containsKey(tmpId)) {

                            //tmp, create and index
                            Map parsedM = parse(m, dfd);

                            //create sub-ent fragment
                            // ici la key n'est plus celle du parent mais du sous-fragment , a corriger !
                            FragmentDoc fgt = createFragmentDoc(dfd, subEnt, parsedM);

                            //create or update entity from fragment : search ent. by his id (in fgt)
                            EntityDoc entDoc = createOrUpdateEntityDoc(fgt, dfd, false);

                            String createdId = entDoc.getId();
                            JSONObject jo = new JSONObject();

                            jo.put("ref", createdId);
                            input.replace(key, value, jo.toMap());

                            tmpMap.put(tmpId, createdId);

                        } else {
                            // id already created, just attach from entity values
                            JSONObject jo = new JSONObject();
                            //remplacer la SE par une reference
                            jo.put("ref", tmpMap.get(tmpId).toString());

                            //TODO update it in database : it my come with additional infos
                            input.replace(key, value, jo.toMap());
                        }
                    }
                }
            }

            if (isMap(value)) {
                parse((Map) value, dfd);
            }

            if (isList(value)) {
                //parse each value
                List arrObj = (List) value;
                for (Object o : arrObj) {
                    if (isMap(o)) {
                        parse((Map) o, dfd);
                    }
                }

            }
        }

        return input;

    }

    private String isSubEntity(Object key) {

        if (isMap(key)) {
            Map keyMap = (Map) key;
            for (DictionaryDoc dic : getDictionaryDocs()) {
                String subEntityName = myContains(keyMap, dic);
                if (subEntityName != null) {
                    return subEntityName;
                }
            }

        }
        return null;
    }

    String getProcessDictionaryByName(String name) {
        DictonnaryManager dm = new DictonnaryManager();
        String dic = dm.findByName(name).get(0).getId();
        return dic;
    }

    /**
     *
     * @param df
     * @param keyType
     * @param entityValues
     * @return
     */
    public FragmentDoc createFragmentDoc(DataFlowDoc df, String keyType, Map<Object, Object> entityValues) {

        FragmentDoc fd = new FragmentDoc();

        FragmentManager fragmentManager = new FragmentManager();

        String taskName = execution.getCurrentActivityId();

        fd.setType(FragmentDoc.class.getSimpleName());
        fd.setEntityType(keyType);
        fd.setBusinessKey(df.getBusinessKey());
        fd.setDefinitionId(df.getDefinitionId());
        fd.setCreated_on(DateUtils.getNowGMT());
        fd.setCreated_by(df.getLast_modified_by());

        Map signature = new HashMap();
        signature.put("timeStamp", DateUtils.getNowGMT());
        signature.put("signer", df.getLast_modified_by());
        Set keys = entityValues.keySet();
        signature.put("signedValues", keys.toArray());
        fd.setSignature(signature);

        Object id = df.getCurrent_entity().get("_id");
        String currentEntityId = null;

        if (id != null) {
            currentEntityId = id.toString();
        }

        Map idsMap = IdGenerator.getNextFragemntId(currentEntityId, keyType, taskName, fd.getBusinessKey());

        fd.setId(idsMap.get("fragmentId").toString());
        fd.setEntityId(idsMap.get("entityId").toString());

        fd.setExecutionId(df.getExecutionId());
        fd.setProcessInstanceId(df.getProcessInstanceId());

        Map withoutIdField = new HashMap();
        for (Iterator iterator = entityValues.keySet().iterator(); iterator.hasNext();) {
            Object key = iterator.next();
            if (!key.equals("_id")) {
                withoutIdField.put(key, entityValues.get(key));
            }
        }
        fd.setValues(withoutIdField);

        fragmentManager.create(fd);

        return fd;

    }

    public EntityDoc createOrUpdateEntityDoc(FragmentDoc fd, DataFlowDoc dfd, boolean mainFgt) {

        EntityManager em = new EntityManager();
        EntityDoc ed = null;

        try {
            
            String fid = fd.getEntityId();
            
            if (fid == null) {
                ed = null;
            } else {
                ed = em.find(fd.getEntityId());
            }

        } catch (DocumentNotFoundException e) {
            ed = null;
            System.err.println(e.getMessage() + "->handled");
        }

        if (mainFgt) {
            String currEntityId = dfd.getCurrent_entity().get("_id") == null ? null : dfd.getCurrent_entity().get("_id").toString();
            if (currEntityId != null) {
                ed = em.find(currEntityId);
            }

        }

        if (ed == null) {

            ed = new EntityDoc();

            ed.setId(fd.getEntityId());
            ed.setType(EntityDoc.class.getSimpleName());
            ed.setEntityType(fd.getEntityType());
            ed.setProcessInstanceId(fd.getProcessInstanceId());
            ed.setDictionary(getProcessDictionaryByName(fd.getEntityType()));
            ed.setSite(new SettingsHandler().get(SettingsUtils.site).toString());
            ed.setVersion(fd.getId());

            Map values = ed.getValues();

            fd.getValues().entrySet().stream().forEach((entry) -> {
                Object key = entry.getKey();
                values.put(key, new EntityValueWrapper(entry.getValue(), fd.getId()));
            });

            ed.setValues(values);

            em.create(ed);

        } else {
            //update values add new ones
            Map<Object, Object> currentValues = ed.getValues();

            //put all exec variables inside
            // TODO put just the new ones with theirs current fragments
            for (Map.Entry<Object, Object> entry : fd.getValues().entrySet()) {
                Object key = entry.getKey();
                if (currentValues.containsKey(key)) {
                    if (currentValues.get(key).equals(entry.getValue())) {
                        continue; //dont put a property twice : this will override his fragment and destroy the whole Universe
                    }
                }
                currentValues.put(key, new EntityValueWrapper(entry.getValue(), fd.getId()));
            }

            ed.setVersion(fd.getId());
            ed.setValues(currentValues);
            em.edit(ed);
        }
        return ed;

    }

    public String getSite() {

        LocalSettingsManager sm = new LocalSettingsManager();
        SettingsDoc sd = sm.findInstance();
        String siteId = sd.getValues().get(SettingsUtils.site).toString();
        return siteId;
    }

    private void createFiles(EntityDoc mainEd, DataFlowDoc dfd) {

        FileManager attachmentManager = new FileManager();
        Map<String, Attachment> attachments = dfd.getAttachments();

        if (attachments == null) {
            return;
        }
        for (Map.Entry<String, Attachment> map : attachments.entrySet()) {

            String key = map.getKey();
            Attachment att = map.getValue();

            String filedName = key.substring(0, key.indexOf('.'));

            FileDoc adoc = new FileDoc();

            adoc.setId(mainEd.getId().concat(".")
                    .concat(filedName)
                    .concat(".").concat(dfd.getProcessInstanceId()).concat(".").concat(getSite()));

            adoc.setBusinessKey(dfd.getBusinessKey());
            adoc.setDate(DateUtils.getNowGMT());
            adoc.setField(filedName);
            adoc.setSite(getSite());

            FileDoc test = null;
            try {
                test = attachmentManager.find(key);
            } catch (DocumentNotFoundException e) {
                //swallot it
            }

            if (test != null) {
                attachmentManager.create(adoc);
                adoc.attachDoc(dfd.getId(), key, att);
                attachmentManager.edit(adoc);
            }

        }
    }

    private static String myContains(Map keyMap, DictionaryDoc dic) {
        String dicKey = dic.getKey();
        String dicName = dic.getName();
        Object keyMapIdObj = keyMap.get("_id");

        if (keyMapIdObj != null) {
            String keyMapId = keyMapIdObj.toString();
            boolean isKeyProp = keyMapId.contains(dicKey);
            if (isKeyProp) {
                return dicName;
            }
        }
        return null;
    }

}
