/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.digest.DigestUtils;
import com.grew.security.SecuritySettings;

/**
 *
 * @author bashizip
 */
public class HashTool {

    public static String get_SHA_512_SecurePassword(String passwordToHash) {
       
        String salt= SecuritySettings.getPasswordSalt();
        
        String generatedPassword = null;
        
        try {
            
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    
      public static String getMD5HashedPassword(String pwd) {
        String md5 = DigestUtils.md5Hex(pwd);
        return md5;
    }
      
      public static void main(String[] args) {
          System.out.println(get_SHA_512_SecurePassword("delphin12"));
    }
}
