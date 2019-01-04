package cn.com.ultrapower.method.service.insert;

import cn.com.ultrapower.utils.StringUtils;
import cn.com.ultrapower.utils.TimeUtil;
import cn.com.ultrapower.utils.UUIDGenerator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class InsertDataToDH {
    static String DB_DRIVER = "";
    static String DB_URL = "";
    static String DB_UNAME = "";
    static String DB_PWD = "";
    public InsertDataToDH(){
        initPath();
    }
    //数据插入方法入口
    public Map executeAnalysis(Map paramMap){
        String type = (String) paramMap.get("whereType");
        Map resultMap = new HashMap();
        if (StringUtils.isNotBlank(type)&&"0".equals(type)) {
            StringBuffer sql = insertSQL(resultMap, paramMap);
            int num = executeInsert(sql);
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
                while (i<sqlArr.length&&flag&&(sqlArr[i].contains("insert")||sqlArr[i].contains("INSERT"))) {
                    String pid = UUIDGenerator.getUUIDoffSpace();
                    if(sqlArr[i].contains("uniqueID")){
                        String endSql = sqlArr[i].replaceAll("uniqueID", pid);
                        StringBuffer sql = new StringBuffer(endSql);
                        num = executeInsert(sql);
                        flag=num>0;
                        i++;
                    }
                }
            }
            //单条sql
            else {
                String pid = UUIDGenerator.getUUIDoffSpace();
                if(sqlStr.contains("uniqueID")) {
                    String endSql = sqlStr.replaceAll("uniqueID", pid);
                    StringBuffer sql = new StringBuffer(endSql);
                    num = executeInsert(sql);
                }
            }
            resultMap.put("content",num);
        }
        return resultMap;
    }


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
     * 解析添加SQL
     * @param resultMap
     * @param paramMap
     * @return
     */
    public StringBuffer insertSQL(Map resultMap,Map paramMap) {
        String tableName = String.valueOf(paramMap.get("tableName"));
        StringBuffer sql = new StringBuffer("INSERT INTO "+tableName+" (");
        Object param = paramMap.get("content");
        if(param==null){
            resultMap.put("content","[{'ERROR':'no found parameter'}]");
        }
        String paramData =("[" +param.toString()+"]").replaceAll("<","{").replaceAll(">","}");
        JSONArray jsonArray = JSONArray.parseArray(paramData);
        if (jsonArray!=null||jsonArray.size()>0) {
            JSONObject fieldObj = jsonArray.getJSONObject(0);
            fieldObj.put("pid", UUIDGenerator.getUUIDoffSpace());
            fieldObj.put("createtime", TimeUtil.formatIntToDateString(System.currentTimeMillis()/1000));
            List fieldList = new ArrayList();
            Object[] cols = fieldObj.keySet().toArray();
            fieldList = Stream.of(cols).collect(Collectors.toList());
            int colNum = fieldList.size();
            int num = jsonArray.size();
            for (int a=0;a<colNum;a++){
                if(a>0) sql.append(",");
                sql.append(fieldList.get(a));
            }
            sql.append(") VALUES ");
            for(int i=0;i<num;i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                sql.append("(");
                for(int j=0;j<colNum;j++){
                    if(j>0)
                        sql.append(",");
                    sql.append("'"+obj.get(fieldList.get(j))+"'");
                }
                sql.append(")");
                if(i==num-1)
                    sql.append(";");
                else sql.append(",");
            }
        }
        return sql;
    }
    /**
     * 执行SQL语句
     * @param sql
     * @return
     */
    public int executeInsert(StringBuffer sql){
        Connection conn = initConnection();
        Statement statement =null;
        int insertNum = 0;
        try {
            System.out.println("SQL:"+sql);
            statement = conn.createStatement();
            insertNum = statement.executeUpdate(sql.toString());
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
        return insertNum;
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
        testMap.put("content","[{'pid':'1a','name':'111','createTime':'2018-07-05'},{'pid':'2a','name':'222','createTime':'2018-07-05'},{'pid':'3a','name':'333','createTime':'2018-07-05'},{'pid':'4a','name':'444','createTime':'2018-07-15'},{'pid':'5a','name':'555','createTime':'2018-07-15'}]");
        //System.out.println(new InsertDataToDH().executeAnalysis(testMap));
        System.out.println(TimeUtil.formatIntToDateString(System.currentTimeMillis()/1000));
        String insertSql = "insert into test(pid,name,sex) values('uniqueID','111','333');insert into test2(pid,jkd,kkkd) values('uniqueID','232','dfsf')";
        String sql = "insert into bs_case_seriesshunt(pid,sp_rule,sp_describe,applicationMethod,caseHouseType,caseHappenTime,caseFeature) values('uniqueID','nihaotest','nihao','钻窗,技术开锁','别墅,商户','夜间,傍晚','撬扭挂锁')!insert into bs_case_theftandseriesshunt(pid,householdtheft_id,seriesshunt_id) values('uniqueID',ifNull((select pid from bs_case_householdtheft where 1=1 and hasAny(splitByString(',','钻窗,技术开锁'),splitByString(';',applicationMethod))=1 and hasAny(splitByString(',','别墅,商户'),splitByString(';',caseHouseType))=1 and hasAny(splitByString(',','夜间,傍晚'),splitByString(';',caseHappenTimeType))=1 and hasAny(splitByString(',','撬扭挂锁'),splitByString(';',caseFeature))=1),''),(select pid from bs_case_seriesshunt where 1=1 and sp_rule='nihaotest' and sp_describe='nihao'))";
        String[] strings = sql.split("!");
        for (int i = 0; i < strings.length; i++) {
            String sql1 = strings[i];
            String pid = UUIDGenerator.getUUIDoffSpace();
            String sql2 = sql1.replaceFirst("\\(", "(pid,").replace("values(", "values('" +pid+ "'");
            if(sql1.contains("uniqueID")) {
                String endSql = sql1.replaceAll("uniqueID", pid);
                //System.out.println(endSql);
            }
            //System.out.println(sql2);
        }
        if(sql.contains("!")) {
            String[] sqlArr = sql.split("!");
            int i=0;
            boolean flag = true;
            while (i<sqlArr.length&&flag&&(sqlArr[i].contains("insert")||sqlArr[i].contains("INSERT"))) {
                String pid = UUIDGenerator.getUUIDoffSpace();
                if(sqlArr[i].contains("uniqueID")){
                    String endSql = sqlArr[i].replaceAll("uniqueID", pid);
                    //System.out.println(endSql+"123456");
                    i++;
                }
            }
        }
        String whereSQL = "insert into bs_case_seriesshunt(pid,sp_rule,sp_describe,applicationMethod,caseHouseType,caseHappenTime,caseFeature) values('uniqueID','test','sd','钻窗','','','')!insert into bs_case_theftandseriesshunt(pid,householdtheft_id,seriesshunt_id) values('uniqueID',ifNull((select pid from bs_case_householdtheft where 1=1 and hasAny(splitByString(',','钻窗'),splitByString(';',applicationMethod))=1 and hasAny(splitByString(',',''),splitByString(';',caseHouseType))=1 and hasAny(splitByString(',',''),splitByString(';',caseHappenTimeType))=1 and hasAny(splitByString(',',''),splitByString(';',caseFeature))=1),''),(select pid from bs_case_seriesshunt where 1=1 and sp_rule='test' and sp_describe='sd'))";
        //String s = trimEmptyWhere(whereSQL);
        //System.out.println(s);
        String exStr = "nihao 192.168.1.1";
        boolean matches = exStr.matches("([0-9]{1,}\\.)+");
        System.out.println(matches);
        //String endEx = exStr.replaceAll(exStr, "\\d+\\.\\d+\\.\\d+\\.\\d+");

    }
}
