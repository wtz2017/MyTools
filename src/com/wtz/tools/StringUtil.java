package com.wtz.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class StringUtil {

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        }

        return false;
    }

    /**
     * @param content e.g. "本次获得0元，累计获得1.5元"
     * @return e.g. [0, 1.5]
     */
    public static ArrayList<String> splitNumbers(String content) {
        ArrayList<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            list.add(matcher.group(0));
        }
        return list;
    }
    
    /**
     * @param url
     * @param key
     * @return value of param
     */
    public static String getValueFromUrl(String url, String key) {
        String value = "";
    
        if (isEmpty(url) || isEmpty(key) || !url.contains(key)) {
            return value;
        }
    
        int indexBegin = url.indexOf(key);
        int indexEnd = url.indexOf("&", indexBegin);
        if (indexEnd >= 0) {
            value = url.substring(indexBegin + key.length(), indexEnd);
        } else {
            value = url.substring(indexBegin + key.length());
        }
    
        return value;
    }

    /**
     * @param separator e.g. "|"
     * @param paramString e.g. a,b,c
     * @return a|b|c
     */
    public static String putStringTogether(String separator, String... paramString) {
        if (paramString == null) {
            return "";
        }
        
        StringBuffer stringBuffer = new StringBuffer();
        for (String param : paramString) {
            stringBuffer.append(TextUtils.isEmpty(param) ? "" : param).append(separator);
        }
        String paramsStr = stringBuffer.toString();
        return paramsStr.substring(0, paramsStr.length() - 1);
    }
    
    public static String getErroInfoFromException(Exception e) {
        if (e == null) {
            return "";
        }
        
        try {
            Writer stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            printWriter.close();
            return stringWriter.toString();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        
        return e.toString();
    }
}
