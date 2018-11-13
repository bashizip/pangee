/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

/**
 *
 * @author Administrateur
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommonJSONParser {

    static String[] subEntityKeys = {"personneNaturelle", "adresseRdc"};

    private static boolean isSubEntity(String key) {
        return Arrays.asList(subEntityKeys).contains(key);
    }

    public Map<String, Object> parse(String jsonStr) {

        Map<String, Object> result = null;

        if (null != jsonStr) {
            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                result = parseJSONObject(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } // if (null != jsonStr)

        return result;
    }

    private Object parseValue(Object inputObject) throws JSONException {
        Object outputObject = null;   
       // System.out.print("Parsing: " + inputObject +"\t\t");
        
        if (null != inputObject) {
            
            if (isSubEntity(inputObject.toString())) {
                System.out.println("Found SE:" + inputObject);
            }

            if (inputObject instanceof JSONArray) {
                outputObject = parseJSONArray((JSONArray) inputObject);
            } else if (inputObject instanceof JSONObject) {
                outputObject = parseJSONObject((JSONObject) inputObject);
            } else if (inputObject instanceof String || inputObject instanceof Boolean || inputObject instanceof Integer) {
                outputObject = inputObject;
            }

        }
  //System.out.println("output: " + outputObject);
        return outputObject;
    }

    private List<Object> parseJSONArray(JSONArray jsonArray) throws JSONException {

        List<Object> valueList = null;

        if (null != jsonArray) {
            valueList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                Object itemObject = jsonArray.get(i);
                if (null != itemObject) {
                    valueList.add(parseValue(itemObject));
                }
            } // for (int i = 0; i <jsonArray.length(); i++)         } // if (null != valueStr)
        }

        return valueList;
    }

    private Map<String, Object> parseJSONObject(JSONObject jsonObject) throws JSONException {
        Map<String, Object> valueObject = null;
        if (null != jsonObject) {
            valueObject = new HashMap<String, Object>();

            Iterator<String> keyIter = jsonObject.keys();
            while (keyIter.hasNext()) {
                String keyStr = keyIter.next();
                Object itemObject = jsonObject.opt(keyStr);
                if (null != itemObject) {
                    valueObject.put(keyStr, parseValue(itemObject));
                } // if (null != itemValueStr)

            } // while (keyIter.hasNext())
        } // if (null != valueStr)
        return valueObject;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("reading file ...");
        InputStream is = new FileInputStream("current_entity.json");

        String data = IOUtils.toString(is, StandardCharsets.UTF_8.name());

        new CommonJSONParser().parse(data);

        // System.out.println(subEntities.toString());
        // System.out.println("source out: " + outJsonMap.toString());
    }
}
