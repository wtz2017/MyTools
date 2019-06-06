package com.wtz.tools.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
     * 查找第一个出现次数最多的字符
     * @param str
     * @return
     * @throws NullPointerException
     */
    public static char findFirstMostChar(String str) throws NullPointerException {
        if (str == null || str.equals("")) {
            throw new NullPointerException("parameter str can't be null or empty");
        }

        char[] chars = str.toCharArray();
        int size = chars.length;
        Map<Character, Integer> map = new HashMap<>();

        Character mostChar = null;
        int mostCount = 0;
        for (int i = 0; i < size; i++) {
            Integer count = map.get(chars[i]);
            if (count == null) {
                count = 1;
            } else {
                ++count;
            }
            map.put(chars[i], count);
            if (count > mostCount) {
                mostCount = count;
                if (mostChar == null || !mostChar.equals(chars[i])) {
                    mostChar = chars[i];
                }
            }
        }

        return mostChar;
    }

    /**
     * 查找第一个出现次数最多的汉字
     * @param str
     * @return
     * @throws NullPointerException
     */
    public static char findFirstMostChineseChar(String str) throws NullPointerException {
        if (str == null || str.equals("")) {
            throw new NullPointerException("parameter str can't be null or empty");
        }

        Map<String, Integer> map = new HashMap<>();
        String mostChar = null;
        int mostCount = 0;

        String reg = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        while (m.find()) {
            String chinese = m.group();
            Integer count = map.get(chinese);
            if (count == null) {
                count = 1;
            } else {
                ++count;
            }
            map.put(chinese, count);
            if (count > mostCount) {
                mostCount = count;
                if (mostChar == null || !mostChar.equals(chinese)) {
                    mostChar = chinese;
                }
            }
        }

        return mostChar.charAt(0);
    }

    /**
     * 查找所有出现次数最多的字符
     * @param str
     * @return
     * @throws NullPointerException
     */
    public static char[] findMostChars(String str) throws NullPointerException {
        if (str == null || str.equals("")) {
            throw new NullPointerException("parameter str can't be null or empty");
        }

        char[] chars = str.toCharArray();
        int size = chars.length;
        Map<Character, Integer> map = new LinkedHashMap<>();// 默认按插入顺序迭代

        int mostCount = 0;
        for (int i = 0; i < size; i++) {
            Integer count = map.get(chars[i]);
            if (count == null) {
                count = 1;
            } else {
                ++count;
            }
            map.put(chars[i], count);
            if (count > mostCount) {
                mostCount = count;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (entry.getValue() == mostCount)
                builder.append(entry.getKey());
        }

        return builder.toString().toCharArray();
    }

    /**
     * 查找所有出现次数最多的汉字
     * @param str
     * @return
     * @throws NullPointerException
     */
    public static char[] findMostChineseChars(String str) throws NullPointerException {
        if (str == null || str.equals("")) {
            throw new NullPointerException("parameter str can't be null or empty");
        }

        String reg = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        int mostCount = 0;
        Map<String, Integer> map = new LinkedHashMap<>();// 默认按插入顺序迭代
        while (m.find()) {
            String chinese = m.group();
            Integer count = map.get(chinese);
            if (count == null) {
                count = 1;
            } else {
                ++count;
            }
            map.put(chinese, count);
            if (count > mostCount) {
                mostCount = count;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == mostCount)
                builder.append(entry.getKey());
        }

        return builder.toString().toCharArray();
    }

    /**
     * 提取汉字
     * @param str
     * @return
     * @throws NullPointerException
     */
    public static String extractChineseCharacters(String str) throws NullPointerException {
        if (str == null || str.equals("")) {
            throw new NullPointerException("parameter str can't be null or empty");
        }

        String reg = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        StringBuilder builder = new StringBuilder();
        while (m.find()) {
            builder.append(m.group());
        }

        return builder.toString();
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
    
    public static String getErroInfoFromException(Throwable e) {
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
    
    /**
     * @param hex 基于int最大为2147483647，所以hex最大不可超过"7FFFFFFF"
     * @return
     * @throws Exception
     */
    public static int hexStringToInt(String hex) throws Exception {
        return Integer.valueOf(hex, 16);
    }

    /**
     * 把不带冒号的 MAC 字串转成带冒号的 MAC
     *
     * @param mac 不带冒号的 MAC 字串
     * @return
     */
    public static String  insertMacSeparator(String mac) {
        if (mac == null || mac.length() != 12) return mac;

        String regex = "(.{2})";
        mac = mac.replaceAll(regex, "$1:");
        return mac.substring(0, mac.length() - 1);
    }

}
