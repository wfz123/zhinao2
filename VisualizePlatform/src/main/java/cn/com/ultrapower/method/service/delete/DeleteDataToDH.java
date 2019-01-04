package cn.com.ultrapower.method.service.delete;

import cn.com.ultrapower.method.service.query.QueryDataToDH;
import cn.com.ultrapower.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;

@Service
public class DeleteDataToDH {
    static String DB_DRIVER = "";
    static String DB_URL = "";
    static String DB_UNAME = "";
    static String DB_PWD = "";
    public DeleteDataToDH(){
        initPath();
    }
    //数据删除方法入口
    public Map executeAnalysis(Map paramMap){
        Map resultMap = new HashMap();
        String flag = (String) paramMap.get("whereType");//是否是自定义sql的标识1是0不是
        if(StringUtils.isNotBlank(flag)&&"0".equals(flag)) {
            String tableName = String.valueOf(paramMap.get("tableName"));
            StringBuffer sql = new StringBuffer("alter table " + tableName + " DELETE ");
            //StringBuffer sql = new StringBuffer("DELETE FROM "+tableName);
            dealWhere(paramMap, sql);
            if(!sql.toString().contains("WHERE")) {
                resultMap.put("content","where condition is null");
            }
            else {
                int num = executeDelete(sql);
                resultMap.put("content",num);
            }
        }else {
            //自定义SQL
            String sqlStr = (String) paramMap.get("where");
            int num = 0;
            if(sqlStr.contains(";")) {
                String[] sqlArr = sqlStr.split(";");
                int i = 0;
                boolean isDeleteFlag = true;
                while (i<sqlArr.length&&isDeleteFlag) {
                    StringBuffer sql = new StringBuffer(sqlArr[i]);
                    num = executeDelete(sql);
                    isDeleteFlag=num>0;
                    i++;
                }
            }
            else {
                StringBuffer sql = new StringBuffer(sqlStr);
                num = executeDelete(sql);
            }
            resultMap.put("content",num);
        }
        return resultMap;
    }

    /***
     * 执行sql语句
     * @param sql
     * @return
     */
    public int executeDelete(StringBuffer sql){
        Connection conn = initConnection();
        Statement statement =null;
        int delNum = 0;
        try {
            System.out.println("SQL:"+sql);
            statement = conn.createStatement();
            delNum = statement.executeUpdate(sql.toString());
            statement.close();
            conn.close();
        }catch (Exception e){
            System.out.println("ERROR:删除数据异常，请检查数据情况...");
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                conn.close();
            }catch (Exception e){}
        }
        return delNum;
    }

    /**
     * 处理WHERR条件
     * @param paramMap
     * @param sql
     * @return
     */
    public StringBuffer dealWhere(Map paramMap,StringBuffer sql) {
        QueryDataToDH queryDataToDH = new QueryDataToDH();
        queryDataToDH.dealReturnWhere(paramMap, sql);
        return sql;
    }
    //初始化数据连接
    private static Connection initConnection(){
        Connection conn = null;
        try {
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_UNAME, DB_PWD);
        }catch (Exception e){
            System.out.println("ERROR:数据库连接异常...");
            conn = null;
        }
        return conn;
    }
    public static String dealString(String where,JSONObject obj){
        StringBuffer result = new StringBuffer();
        char [] chars = where.toCharArray();
        int start = 0;
        int end = 0;
        for (int i=0;i<chars.length;i++){
            if(chars[i]=='$'){
                if(start==0) start = i;
                else end = i;
            }
            if(start==0){ result.append(chars[i]);
            }else if(start>0 && end>0){
                String tmp = where.substring(start,end+1).replace("content.","").replace("$","");
                String value = obj.get(tmp).toString();
                result.append("'"+value+"'");
                start=0; end=0;
            }
            }
            return result.toString();
        }


        //读取properties文件获取服务器地址
    public void initPath() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/config/config.properties"));
            DB_DRIVER = properties.getProperty("DB_DRIVER");
            DB_URL = properties.getProperty("DB_URL");
            DB_UNAME = properties.getProperty("DB_UNAME");
            DB_PWD = properties.getProperty("DB_PWD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String [] args){
        Map testMap = new HashMap();
        testMap.put("tableName","test");
        testMap.put("where","pid=$content.pid$");
        testMap.put("content","[{'pid':'3a'}]");
        System.out.println(new DeleteDataToDH().executeAnalysis(testMap));
    }
}
