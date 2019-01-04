package cn.com.ultrapower.method.service.update;

import cn.com.ultrapower.utils.StringUtils;
import cn.com.ultrapower.utils.UUIDGenerator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class UpdateDataToDH {
    static String DB_DRIVER = "";
    static String DB_URL = "";
    static String DB_UNAME = "";
    static String DB_PWD = "";
    public UpdateDataToDH(){
        initPath();
    }
    //数据插入方法入口
    public Map executeAnalysis(Map paramMap){
        String type = (String) paramMap.get("whereType");
        Map resultMap = new HashMap();
        if (StringUtils.isNotBlank(type)&&"0".equals(type)) {
            StringBuffer sql = updateSQL(resultMap, paramMap);
            int num = executeUpdate(sql);
            resultMap.put("content",num);
        }
        else {
            //自定义SQL
            String sqlStr = (String) paramMap.get("content");
            String trimEmptyWhereSQL = this.trimEmptyWhere(sqlStr);
            int num = 0;
            //执行多条sql
            if(trimEmptyWhereSQL.contains("!")) {
                String[] sqlArr = trimEmptyWhereSQL.split("!");
                int i=0;
                boolean flag = true;
                while (i<sqlArr.length&&flag&&(sqlArr[i].contains("update")||sqlArr[i].contains("update"))) {
                    StringBuffer sql = new StringBuffer(sqlArr[i]);
                    num = executeUpdate(sql);
                    flag=num>0;
                    i++;
                }
            }
            //单条sql
            else {
                StringBuffer sql = new StringBuffer(sqlStr);
                num = executeUpdate(sql);
             }
            resultMap.put("content",num);
        }
        return resultMap;
    }

    /**
     * 处理where条件
     * @param sql
     * @return
     */
    public String trimEmptyWhere(String sql) {
        String sqlWhere = sql.replaceAll("(( AND )?[a-zA-Z]+(\\.)?[a-zA-Z_]+ like '%%')|" +
                "(( and )?[a-zA-Z]+(\\.)?[a-zA-Z_]+ like '%%')|" +
                "(( or )?[a-zA-Z]+(\\.)?[a-zA-Z_]+ like '%%')|" +
                "(( OR )?[a-zA-Z]+(\\.)?[a-zA-Z_]+ like '%%')|" +
                "(( AND )?[a-zA-Z]+(\\.)?[a-zA-Z_]+(=|>|>=|<|<=|!=)'')|" +
                "(( and )?[a-zA-Z]+(\\.)?[a-zA-Z_]+(=|>|>=|<|<=|!=)'')|" +
                "(( or )?[a-zA-Z]+(\\.)?[a-zA-Z_]+(=|>|>=|<|<=|!=)'')" +
                "|(( OR )?[a-zA-Z]+(\\.)?[a-zA-Z_]+(=|>|>=|<|<=|!=)'')", "")
                .replaceAll("(\\( or |\\( and|\\( OR )|\\( AND", "(")
                .replaceAll("(and\\(\\))|(or\\(\\))|(OR\\(\\)|(AND\\(\\)))", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',[a-zA-Z]+\\)\\)=1", "")
                .replaceAll("and [a-zA-Z]+(>=|>|<|<=)toRelativeSecondNum\\(toDate\\(''\\)\\)", "");
        return sqlWhere;

    }

    /**
     * 解析前端数据并返回SQL
     * @param resultMap
     * @param paramMap
     * @return
     */
    public StringBuffer updateSQL(Map resultMap,Map paramMap) {
        String tableName = String.valueOf(paramMap.get("tableName"));
        Object param = paramMap.get("content");
        if(param==null){
            resultMap.put("content","[{'ERROR':'no found parameter'}]");
        }
        String paramData =("[" +param.toString()+"]").replaceAll("<","{").replaceAll(">","}");
        JSONArray jsonArray = JSONArray.parseArray(paramData);
        StringBuffer sql = new StringBuffer("alter table "+tableName+" update ");
        int updateNum = 0;
        if(jsonArray!=null&&jsonArray.size()>0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String primaryId = "";
                List fields = Arrays.stream(obj.keySet().toArray()).collect(Collectors.toList());
                for (int j = 0; j < fields.size(); j++) {
                    if(((((String) fields.get(j))).contains("id_"))) {
                        primaryId = (String) ((String) fields.get(j)).substring(3);
                        continue;
                    }
                    if (j > 0)
                        sql.append(",");
                    sql.append(fields.get(j) + "='" + obj.get(fields.get(j)) + "'");
                }
                sql.append(" WHERE 1=1 and "+ primaryId + "='" + obj.get("id_"+primaryId) + "'");
            }
        }
        return sql;
    }

    /***
     * 更新执行
     * @param sql
     * @return
     */
    public int executeUpdate(StringBuffer sql){
        Connection conn = initConnection();
        Statement statement =null;
        int updateNum = 0;
        try {
            System.out.println("SQL:"+sql);
            statement = conn.createStatement();
            updateNum = statement.executeUpdate(sql.toString());
            statement.close();
            conn.close();
        }catch (Exception e){
            System.out.println("ERROR:插入数据异常，请检查数据情况...");
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                conn.close();
            }catch (Exception e){}
        }
        return updateNum;
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
        testMap.put("WHERE","pid=$content.pid$");
        testMap.put("content","[{'pid':'1a','name':'123456','createTime':'2018-07-05'}]");
        System.out.println(new UpdateDataToDH().executeAnalysis(testMap));
    }
}
