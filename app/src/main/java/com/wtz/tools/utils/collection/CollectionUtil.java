package com.wtz.tools.utils.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    public static <K, V> List<K> mapKeysToList(Map<K, V> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        return new ArrayList<K>(map.keySet());
    }

    public static <K, V> List<V> mapValuesToList(Map<K, V> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        return new ArrayList<V>(map.values());
    }

    public static <K, V> V[] mapValuesToArray(Map<K, V> map, V[] array) {
        if (map == null || map.size() == 0) {
            return null;
        }
        return map.values().toArray(array);
    }

    /**
     * 同时迭代 MAP 的键和值：使用 map.entrySet
     */
    private void iterateMapKeysAndValues1() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
    }

    /**
     * 同时迭代 MAP 的键和值：使用 Iterator，可以在遍历时调用 iterator.remove() 来删除 entries；
     * 如果在for-each遍历中尝试使用删除方法，结果是不可预测的。
     */
    private void iterateMapKeysAndValues2() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        Iterator<Map.Entry<Integer, Integer>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, Integer> entry = entries.next();
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
    }

    /**
     * 只迭代 MAP 的键：使用 map.keySet
     */
    private void iterateMapKeys() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (Integer key : map.keySet()) {
            System.out.println("Key = " + key);
        }
    }

    /**
     * 只迭代 MAP 的值：使用 map.values
     */
    private void iterateMapValues() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (Integer value : map.values()) {
            System.out.println("Value = " + value);
        }
    }

}
