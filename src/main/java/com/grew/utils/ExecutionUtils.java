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
package com.grew.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author pbashizi
 */
public class ExecutionUtils {

    public final static String LAST_TASK_ID = "lastTaskId";
    public static String BUSINESS_KEY_VAR="businessKey";
    public static String BASE_URI="baseUri";
    

    
    
    /**
     * I love Java
     * @param values
     * @return 
     */
    public  static final HashMap<Object, Object> jsonStringToMapValues(String values) {
        HashMap<Object, Object> Values = null;
        try {
            Values = new ObjectMapper().readValue(values, HashMap.class);
        } catch (IOException ex) {
        }
        return Values;
    }
     
 
    
}
