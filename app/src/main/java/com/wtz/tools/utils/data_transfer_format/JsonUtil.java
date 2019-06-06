package com.wtz.tools.utils.data_transfer_format;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 在需要把类通过Gson转为json字符串时，类混淆后就变成abcde...之类的情形了，
 * 可以通过Gson的注解 @SerializedName 来避免混淆改变key名字，比如：
 *
 * @SerializedName("latitude") public String latitude;
 * 这样，既能避免被混淆，又能指定别名
 */
public class JsonUtil {

    public static Object jsonToObject(String jsonStr, Class classOfT) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(jsonStr, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String objectToJson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    /**
     * android 系统自带 org.json 使用示例：
     */
    private void demo() {
        //根据接收到的JSON字符串来解析字符串中所包含的数据和数据对象
        //接收到的JSON字符串
        String result = "[{\"username\": \"your name\", \"user_json\": {\"username\": \"your name\", \"nickname\": \"your nickname\"}}]";

        try {
            //根据字符串生成JSON对象
            JSONArray resultArray = new JSONArray(result);
            //获取数组元素
            //JSONObject resultObj = resultArray.getJSONObject(0);
            //相比getxxx，optxxx不会抛异常：JSONException
            JSONObject resultObj = resultArray.optJSONObject(0);

            //获取数据项
            String username = resultObj.getString("username");

            //获取数据对象
            JSONObject user = resultObj.getJSONObject("user_json");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //创建 JSONObject 对象和 JSONArray
        JSONObject json = new JSONObject();

        //向json中添加数据
        try {
            json.put("username", "wanglihong");
            json.put("height", 12.5);
            json.put("age", 24);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //创建JSONArray数组，并将json添加到数组
        JSONArray array = new JSONArray();
        array.put(json);
    }

    public class MacParams {
        @SerializedName("macs")
        public List<String> macs;
    }

    public void testList() {
        MacParams macParams = new MacParams();
        macParams.macs = new ArrayList<>();
        macParams.macs.add("abc");
        macParams.macs.add("de");
        macParams.macs.add("ao");
        Gson gson = new Gson();
        Log.d("JsonUtil", gson.toJson(macParams));
        // 结果是：{"macs":["abc","de","ao"]}
    }

}
