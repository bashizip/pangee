/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.control;

/**
 *
 * @author Administrateur
 */
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import com.grew.model.DataFlowDoc;
import com.grew.model.EntityDoc;
import com.grew.model.FragmentDoc;
import com.grew.model.codification.DictionaryDoc;
import static com.grew.utils.CollectionsUtils.isList;
import static com.grew.utils.CollectionsUtils.isMap;
import com.grew.utils.IdGenerator;
import com.grew.utils.UUIDGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 *
 * @author bashizip
 */
public class CommonEntityParser {

    static String[] subEntityKeys = {"personneNaturelle", "adresseRdc"};

    static List<Map<Object, Object>> subEntities = new ArrayList<>();

    static Map parsedMap = new LinkedHashMap();
    private static int seq = 1;

    static Map sourceJsonMap;

    static DataFlowDoc dfd;
    private static final Map outJsonMap = new LinkedHashMap();
    static List<DictionaryDoc> dictionaryDocs= getDictionaryDocs();
    
       static Map tmpMap = new LinkedHashMap();

    
    
    
      private  static String myContains(Map keyMap, DictionaryDoc dic) {

        String dicId = dic.getId();
        String dicKey = dic.getKey();
        String dicName = dic.getName();
           Object keyMapIdObj = keyMap.get("_id");
  if(keyMapIdObj!=null){
        String keyMapId =keyMapIdObj.toString();
        
        boolean isKeyProp = keyMapId.contains(dicKey);

        if (isKeyProp) {
            return dicName;
        }
  }
        return null;
    }
    
    

    private static String isSubEntity(Object key) {

        if (isMap(key)) {
            Map keyMap = (Map) key;
            for (DictionaryDoc dic : getDictionaryDocs()) {
                String subEntityName = myContains(keyMap, dic);
                if (subEntityName != null) {
                      System.out.println("SE found: " + subEntityName);
                    return subEntityName;
                }
            }

        }
        return null;
    }

    public static List<DictionaryDoc> getDictionaryDocs() {
        if (dictionaryDocs == null) {
            dictionaryDocs = new DictonnaryManager().findAll();
            System.out.println(dictionaryDocs.toString());
        }
      
        return dictionaryDocs;
    }

  static  String  getProcessDictionary(String entyTyType) {
        DictonnaryManager dm = new DictonnaryManager();
        String dic = dm.findByName(entyTyType).get(0).getId();
        return dic;
    }

  
  
  
  
  
  
    public static Map parse(Map input) {

    
        for (Iterator<Map<Object, Object>> iterator = input.keySet().iterator(); iterator.hasNext();) {

            Object next = iterator.next();
            String key = next.toString();

            Object value = input.get(key);

     //   System.out.println("parsing : " + key);
              
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

                        if (!tmpMap.keySet().contains(tmpId)) {

                              System.out.println("new _id tmp found :" + tmpId);
                            //tmp, create and index
                            Map parsedM = parse(m);

                            //create sub-ent fragment
                            // ici la key n'est plus celle du parent mais du sous-fragment , a corriger !
                           // FragmentDoc fgt = createFragmentDoc(dfd, key, parsedM);

                            //create or update entity from fragment : search ent. by his id (in fgt)
                          //  EntityDoc entDoc = createOrUpdateEntityDoc(fgt, dfd, false);

                           String createdId = UUIDGenerator.nextID();
                          //  String createdId = entDoc.getId();
                            JSONObject jo = new JSONObject();

                            jo.put("ref", createdId);
                            input.replace(key, value, jo.toMap());
                           
                            tmpMap.put(tmpId, createdId);

                        } else {
                            // id already created, just attach from entity values
                            JSONObject jo = new JSONObject();
                            //remplacer la SE par une reference
                            jo.put("ref", tmpMap.get(tmpId).toString());
                            input.replace(key, value, jo.toMap());
                        }
                    }
                }
            }

            if (isMap(value)) {
                parse((Map) value);
            }

            if (isList(value)) {
                //parse each value
                List arrObj = (List) value;
                for (Object o : arrObj) {
                    if (isMap(o)) {
                        parse((Map) o);
                    }
                }

            }
   
          
        }
       
        
    return input;
    }

    private static boolean isMap(Object x) {
        if (x instanceof Map<?, ?>) {
            return true;
        }
        return false;
    }

    private static boolean isList(Object x) {
        if (x instanceof List<?>) {
            return true;
        }
        return false;
    }

    private static Map<Object, Object> toMap(String value) {
        Map<Object, Object> entityValues = new Gson().fromJson(
                value, new TypeToken<HashMap<Object, Object>>() {
        }.getType()
        );
        return entityValues;
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {

            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                System.out.println("skipping: " + test);
                return false;
            }
        }
        return true;
    }

 
    public static void main(String[] args) throws FileNotFoundException, IOException {

        InputStream is = new FileInputStream("test.json");

        String data = IOUtils.toString(is, StandardCharsets.UTF_8.name());

          Map rs=  parse(toMap(data));
          Set keys = rs.keySet();
      
          
 String result = new Gson().toJson(keys);
 
     // String result = new Gson().toJson(rs,Map.class);
      
        System.out.println(result);
    }

}
