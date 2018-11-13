/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.annotation.XmlRootElement;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import com.grew.model.EntityDoc;
import com.grew.utils.CollectionsUtils;
import static com.grew.utils.CollectionsUtils.isList;
import static com.grew.utils.CollectionsUtils.isMap;

/**
 *
 * @author bash
 */
@XmlRootElement
public class SearchResult implements Serializable {

    String key;
    Object result;

    public SearchResult() {
    }

    public String getKey() {
        return key;
    }

    public SearchResult(String key, Object result) {
        this.key = key;
        this.result = result;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * Remove fragment values
     *
     * @return
     */
    public SearchResult parse() {

        SearchResult parsed = new SearchResult();
        Map parsedEntity = new TreeMap();
        Map valuesMap = new LinkedHashMap();

        if (isMap(this.result)) {
           
            Map resMap = (Map) this.result;
            
            if(resMap.isEmpty()){
                return new SearchResult(key, result);
            }
            
            String id = resMap.get("_id").toString();
            
            Object name = resMap.get("_name");
            Object entity = resMap.get("entity");

            parsedEntity.put("_id", id);
            parsedEntity.put("_name", name);

            if (isMap(entity)) {

                Map entMap = (Map) entity;

                for (Iterator iterator = entMap.keySet().iterator(); iterator.hasNext();) {

                    Object next = iterator.next();
                    String mkey = next.toString();
                    Object prop = entMap.get(next); //is also a map with (value and fgt)

                    if (isMap(prop)) {
                        //get value key
                        Map propMap = (Map) prop;

                        patch((Map) propMap);

                        Object value = propMap.get("value"); //can be a map; here wi remove the fragment part

                        valuesMap.put(mkey, value);
                    }

                }
            }
        }

        parsedEntity.put("values", valuesMap);

        parsed.setKey(this.key);
        parsed.setResult(parsedEntity);

        return parsed;
    }

    void patch(Map value) {

        EntityManager em = new EntityManager();

        // loop trouth value 
        for (Iterator iterator = value.keySet().iterator(); iterator.hasNext();) {

            Object next = iterator.next();
            String mKey = next.toString();
            Object prop = value.get(next);

            if (mKey.equals("ref")) {
                //one ref if found, replace it with entityBody

                String entityId = prop.toString();
                value.remove(mKey);
                EntityDoc ed = em.find(entityId);
                Map entBody = ed.getValues();

                for (Iterator it = entBody.keySet().iterator(); it.hasNext();) {
                    Object k = it.next();
                    Object v = entBody.get(k);//is also a map with (value and fgt)
                    value.put("_id", entityId);

                    if (isMap(v)) {
                        //get value key
                        Map propMap = (Map) v;
                        // patch((Map) propMap);
                        Object vx = propMap.get("value"); //can be a map; here we remove the fragment part
                        if (isMap(vx)) {
                            patch((Map) vx);
                        }
                        value.put(k, vx);

                    }
                }
            }

            if (CollectionsUtils.isMap(prop)) {
                patch((Map) prop);
            }

            if (isList(prop)) {
                //parse each value
                List arrObj = (List) prop;

                for (Object o : arrObj) {
                    if (isMap(o)) {
                        patch((Map) o);
                    }
                }

            }

        }

    }

    @Override
    public boolean equals(Object obj) {

        SearchResult testOb = ((SearchResult) obj);
        if (testOb.result != null | this.result !=null) {
            return testOb.getResult().equals(this.getResult());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
