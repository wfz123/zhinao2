package cn.com.ultrapower.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

public class JointJsonResult {

    /**
     * 统一返回方法
     * @param status   success/failed
     * @param str
     * @return
     */
    public static JSONObject joinJson(String status, String str){
        String json = "{\"STATUS\":\""+status+"\",\"RESULT\":\""+str+"\"}";
        return  JSONObject.fromObject(json);
    }

    /**
     * 统一返回方法
     * @param status   success/failed
     * @param obj
     * @return
     */
    public static JSONObject joinJson(String status, Object obj){
        String json = "{\"STATUS\":\""+status+"\",\"RESULT\":"+JSONObject.fromObject(obj)+"}";
        return  JSONObject.fromObject(json);
    }

    public static JSONObject joinArray(String status, Object obj){
        String json = "{\"STATUS\":\""+status+"\",\"RESULT\":"+ JSONArray.fromObject(obj)+"}";
        return  JSONObject.fromObject(json);
    }
}
