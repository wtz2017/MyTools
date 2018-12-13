package com.wtz.tools.utils;

import com.google.gson.Gson;

public class JsonUtil {

    public static Object jsonStringToObject(String jsonStr, Class classOfT) {
        Object o;

        try {
            o = classOfT.newInstance();
            Gson gson = new Gson();
            o = gson.fromJson(jsonStr, classOfT);
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String objectToJsonString(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }
}
