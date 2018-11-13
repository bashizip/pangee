package com.grew.security;

import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;

/**
 *
 * @author bashizip
 */
public class SecuritySettings {

    public static String getTokenIssuer() {
        return "guce.cd";
    }

    public static int getRefreshTokenExpTime() {
        
        String expString = ResourceBundle.getBundle("settings").getString("xAuth.tokenExpirTimeMin");
        if (expString == null) {
            return 60;
        }
        return Integer.valueOf(expString.trim());
    }

    public static byte[] getTokenSigningKey() throws UnsupportedEncodingException {
        return "t&szlig;&acute;&mdash;&trade;&agrave;%O&tilde;v+n&icirc;&hellip;SZu&macr;&micro;&euro;U&hellip;899H&times;".getBytes("UTF-8");
    }

    public static String getPasswordSalt() {
        return "t&szlig;&acute;&mdash;&trade;&agrave;%O&tilde;v+n&icirc;klrjgoirejgioereo&&&&hellip;SZu&macr;&micro;&euro;U&hellip;899H&times;";
    }
}
