/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeanfelixg
 */
public class StringTools {
    
    public static String replaceUnicodeChars(String str, char cReplace) {
        StringBuilder strResult = new StringBuilder();
        Pattern p = Pattern.compile("[a-zA-Z0-9_]");
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            Matcher m = p.matcher(String.valueOf(c));
            if (!m.find()) {
                c = cReplace;
            }
            strResult.append(c);
        }
        return strResult.toString();
    }
}
