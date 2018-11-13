/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

import java.util.List;
import java.util.Map;


/**
 *
 * @author bash
 */
public class CollectionsUtils {
    
    
    public static boolean isList(Object x) {
        if (x instanceof List<?>) {
            return true;
        }
        return false;
    }

    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public  static boolean isMap(Object x) {
        if (x instanceof Map<?, ?>) {
            return true;
        }
        return false;
    }
}
