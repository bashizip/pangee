/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author bash
 */
public class DateUtils {

    public static String getNowGMT() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        String out = f.format(new Date());
        return out;
    }

    public static String getSQLFormatted(Date date) {
        String format = "yyy-MM-dd";
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);

    }
    
    
    public static String formatDateSimple(Date d) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.FRENCH);
        String s = dateFormat.format(d);
        return s;
    }

    public static void main(String[] args) {
        System.out.println(formatDateSimple(new Date()));
    }
}
