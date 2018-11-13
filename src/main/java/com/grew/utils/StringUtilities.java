/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author pbash
 */
public class StringUtilities {

    public static String parseMethodNameParams(String[] keys) {
        
        Arrays.sort(keys);
        String asString = Arrays.toString(keys);
        asString = asString.replace("[", "").replace("]", "").replace(",", "_").
                replaceAll("\\p{Z}", "").toLowerCase().trim();
        return asString;
    }

    public static String getZeroFilledNumber(int nb, int digitsCount) {
        
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(digitsCount);
        nf.setMaximumFractionDigits(0);
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
        char separator = dfs.getGroupingSeparator();
        String numF = nf.format(nb).replace(separator, ' ').trim();
        numF = numF.replaceAll("\\p{Z}", "");
        return numF;

    }

    public static boolean isEmpty(String values) {
     return StringUtils.isBlank(values);
    }
}
