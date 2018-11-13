/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grew.control.DictonnaryManager;
import com.grew.control.FragmentManager;
import com.grew.control.LocalSettingsManager;
import com.grew.model.FragmentDoc;
import com.grew.model.SettingsDoc;

/**
 *
 * @author pbashizi
 */
public class IdGenerator {

    // String format = "17/KNG/000001";
    static LocalSettingsManager sm = new LocalSettingsManager();

    
    
    public synchronized static String getNextBusinessKey(String processCode, int processInstances) {

        int index = -1;

        String siteCode;
        //  String format = "year/siteCode/index";
        String result;

        //  int year = Calendar.getInstance().get(Calendar.YEAR)%100;
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String year = df.format(Calendar.getInstance().getTime());

        index = processInstances + 1;

        siteCode = sm.get(SettingsUtils.site).toString();

        String numF = StringUtilities.getZeroFilledNumber(index, 6);

        String full = year + "".concat("/").concat(siteCode).concat("/").concat(processCode).concat("/").concat(numF);
        result = full.replaceAll("\\p{Z}", "");

        SettingsDoc sdoc = sm.findInstance();

        Map curr = sdoc.getValues();
        curr.put(SettingsUtils.businessKeyIndex, index);
        sdoc.setValues(curr);
        sm.edit(sdoc);

        return result;

    }

    public synchronized static String getNextEntityId(String keyType) {
        //"ent.kng.jur.45678"
        String site = sm.get(SettingsUtils.site).toString();
        String fId = "ent." + site + "." + getDicKeyByName(keyType);
        Map typeEntrepriseMapSeq = (Map) sm.get(SettingsUtils.entitySequences);

        if (typeEntrepriseMapSeq == null) {
            typeEntrepriseMapSeq = new HashMap();
            typeEntrepriseMapSeq.put(keyType, 0);
        }
        Object indexO = typeEntrepriseMapSeq.get(keyType);

        if (indexO == null) {
            indexO = 0;
        }

        int index = Integer.valueOf(indexO.toString());

        if (index == 0) {
            index = 1;
        }

        fId = fId.concat("." + index);
        String entityId = fId;

        index++;
        // Update the sequence
        SettingsDoc sdoc = sm.findInstance();
        Map curr = sdoc.getValues();

        typeEntrepriseMapSeq.put(keyType, index);

        curr.put(SettingsUtils.entitySequences, typeEntrepriseMapSeq);

        sdoc.setValues(curr);

        sm.edit(sdoc);

        String finalID = entityId.toLowerCase();

        return finalID;
    }

    public synchronized  static String getNexFlowId(String entityId, String businessKey) {
        return entityId.concat(":flw.").concat(businessKey.toLowerCase().replace("/", "."));
    }

    /**
     *
     * @param entityId
     * @param taskName
     * @param keyType
     * @param businessKey
     * @return
     */
    public synchronized static Map getNextFragemntId(String entityId, String keyType, String taskName, String businessKey) {
        //"ent.kng.jur.45678:flw.kng.17.1:frg.signature.1"

        Map idsMap = new HashMap();

        if (entityId == null) {
            entityId = getNextEntityId(keyType);
        }

        idsMap.put("entityId", entityId);

        String fId = entityId.concat(":").concat("flw.")
                .concat(businessKey.toLowerCase().replace("/", "."));

        String idBeforeSeq = fId + ":fgt." + taskName + ".";

        FragmentManager fm = new FragmentManager();
        List<FragmentDoc> fgts = fm.findByEntityId(entityId);

        //search for fgt with same signature
        List<String> ids = new ArrayList<>();
        fgts.stream().forEach((f) -> {
            ids.add(f.getId());
        });

        int defaultIndex = 1;
        int maxseq = 0;
        for (String id : ids) {
            if (id.startsWith(idBeforeSeq)) {
                //already xist
                String currentIdexSr = id.substring(id.lastIndexOf("."));
                int currentIndex = Integer.valueOf(currentIdexSr);

                if (currentIndex > maxseq) {
                    maxseq = currentIndex;
                }
            }
        }

        maxseq = maxseq + defaultIndex;

        fId = idBeforeSeq.concat(maxseq + "").toLowerCase();//.concat(UUIDGenerator.nextID()); //TODO create a sequence for fragemnts

        idsMap.put("fragmentId", fId);

        return idsMap;
    }

    static String getDicKeyByName(String name) {
        DictonnaryManager dm = new DictonnaryManager();
        String dic = dm.findByName(name).get(0).getKey();
        return dic;
    }

    public static String getFileFullName(int seq) {
        //aaaammjj_name_flwId_seq
        String year;
        String month;
        String day;
        String fname;
        String processInstanceId;

        DateFormat df = new SimpleDateFormat("yyyy");
        year = df.format(Calendar.getInstance().getTime());
        df = new SimpleDateFormat("mm");
        month = df.format(Calendar.getInstance().getTime());
        df = new SimpleDateFormat("dd");
        day = df.format(Calendar.getInstance().getTime());

        return "";
    }

    public  synchronized static String getNexFlowId(String keyType) {
        String site = sm.get(SettingsUtils.site).toString();
        String fId = "ent." + site + "." + keyType.substring(0, 2).toLowerCase();

        return null; //TODO
    }

    public static void main(String[] args) {
        String dicName = getDicKeyByName("adresseRDC");
        System.out.println(dicName);
    }

}
