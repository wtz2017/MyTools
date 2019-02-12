package com.wtz.tools.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtil {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("a", "aa");
        map.put("b", "bb");
        map.put("c", "cc");

        List<String> keyList = mapKeysToList(map);
        System.out.println(keyList);

        List<String> valueList = mapValuesToList(map);
        System.out.println(valueList);

        String[] valueArray = mapValuesToArray(map, new String[0]);
        for (String v : valueArray) {
            System.out.println(v);
        }
    }

    public static <K,V> List<K> mapKeysToList(Map<K, V> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        return new ArrayList<K>(map.keySet());
    }

    public static <K,V> List<V> mapValuesToList(Map<K, V> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        return new ArrayList<V>(map.values());
    }

    public static <K,V> V[] mapValuesToArray(Map<K, V> map, V[] array) {
        if (map == null || map.size() == 0) {
            return null;
        }
        return map.values().toArray(array);
    }

}
