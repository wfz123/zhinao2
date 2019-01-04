package cn.com.ultrapower.database.service;

import cn.com.ultrapower.database.mapper.DataBaseMapper;
import cn.com.ultrapower.housemapper.CDataBaseMapper;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.PageUtils;
import cn.com.ultrapower.utils.PropertiesUtil;
import cn.com.ultrapower.utils.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by admin on 2018/8/22.
 */
@Service
public class DataBaseService {

    @Autowired
    private DataBaseMapper dataBaseMapper;
    @Autowired
    private CDataBaseMapper cDataBaseMapper;

    /**
     * 查询所连接的数据库中所有的表
     * @return
     */
    public JSONObject selectTables(Map map) {
        String databaseName = PropertiesUtil.initProperties("config/jdbc.properties").getProperty("house_database");
        map.put("databaseName", databaseName);
        if(map==null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        //查询数据库中所有的表
        List<String> list = null;
        Map dataMap = new HashMap();
        try {
//            list = dataBaseMapper.selectTables(map);
            list = cDataBaseMapper.selectTables(map);
            if (list != null && list.size() > 0) {
                dataMap.put("list", list);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 查询一个表中的所有的字段、字段类型、字段注释
     * @param tableNameMap
     * @return
     */
    public JSONObject selectTableColumn(Map<String,String> tableNameMap) {
        //tableNameMap中必须有前台传递的tableNmae的key value是一个JSONArray 例如：tableNames:{"tableName1":"tableName1","tableName2":"tableName2"}
        if (tableNameMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        List<Map> list = null;
        Map dataMap = new HashMap();
        Map conditMap = new HashMap();
        try {
            String databaseName = PropertiesUtil.initProperties("config/jdbc.properties").getProperty("house_database");
            conditMap.put("dataBaseName", databaseName);
            for(String tableName:tableNameMap.values()) {
                conditMap.put("tableName", tableName);
//                list = dataBaseMapper.selectTableColumn(conditMap);
                list = cDataBaseMapper.selectTableColumn(conditMap);
                //因为切换数据库 mysql库和clickhouse库不同，拼接前端需要的
                List dataList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    Map mysqlMap = new HashMap();
                    Map clickMap = list.get(i);
                    mysqlMap.put("columnName", clickMap.get("name"));
                    mysqlMap.put("columnType", clickMap.get("type"));
                    dataList.add(mysqlMap);
                }
                dataMap.put(tableName, dataList);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 查询表中字段以及属性
     * @param map
     * @return
     */
    public JSONObject selectTableColumnAttr(Map<String,String> map) {
        //tableNameMap中必须有前台传递的tableNmae的key value是一个JSONArray 例如：tableNames:{"tableName1":"tableName1","tableName2":"tableName2"}
        if (map == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        List<String> list = null;
        Map dataMap = new HashMap();
        Map conditMap = new HashMap();
        try {
            String databaseName = PropertiesUtil.initProperties("config/jdbc.properties").getProperty("house_database");
            map.put("databaseName", databaseName);
//             list = dataBaseMapper.selectTableColumnAttr(map);
             list = cDataBaseMapper.selectTableColumnAttr(map);
            dataMap.put("list",list);
        }catch (Exception ex) {
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 查询列表内容
     * @param queryMap
     * @return
     */
    public JSONObject queryInfo(Map queryMap) {
        if (queryMap == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        if(!"".equals(queryMap.get("page"))&&!"".equals(queryMap.get("pagesize"))) {
            queryMap = PageUtils.getPageInfo(queryMap);
        }
//        List<Map> list = dataBaseMapper.queryList(queryMap);
//        int num = dataBaseMapper.queryCount(queryMap);
        List<Map> list = cDataBaseMapper.queryList(queryMap);
        int num = cDataBaseMapper.queryCount(queryMap);
        Map dataMap = new HashMap();
        if (list != null && list.size() > 0) {
            dataMap.put("num", num);
            dataMap.put("list", list);
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 查询所有的库名称
     * @return
     */
    public JSONObject queryDatabaseName() {
        //查询数据库
        List<String> list = null;
        Map dataMap = new HashMap();
        try {
//            list = dataBaseMapper.queryDatabaseName();
            list = cDataBaseMapper.queryDatabaseName();
            if (list != null && list.size() > 0) {
                dataMap.put("databaseList", list);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 查询该连接下是否存在某个数据库
     * @param databaseMap
     * @return
     */
    public JSONObject queryDatabaseIsExist(Map databaseMap) {
        //查询数据库中是否存在某个数据库
        Map dataMap = new HashMap();
        boolean flag = false;
        try {
            String database= cDataBaseMapper.queryDatabaseIsExist(databaseMap);
//            String database= dataBaseMapper.queryDatabaseIsExist(databaseMap);
            if (StringUtils.isNotBlank(database)) {
                flag = true;
            }
            dataMap.put("isExist", flag);
        }catch (Exception ex){
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    /**
     * 查询某库是否存在某表
     * @param databaseMap
     * @return
     */
    public JSONObject queryTableIsExist(Map databaseMap) {
            //查询数据库中是否存在某个数据库
            Map dataMap = new HashMap();
            boolean flag = false;
            try {
                List<String> list = cDataBaseMapper.selectTables(databaseMap);
//                List<String> list = dataBaseMapper.selectTables(databaseMap);
                if (list!=null&&list.size()>0) {
                    flag = true;
                }
                dataMap.put("isExist", flag);
            }catch (Exception ex){
                ex.printStackTrace();
                return JointJsonResult.joinJson("error", "query happen exception");
            }
            return JointJsonResult.joinJson("success", dataMap);
        }

    /**
     * 建表动作
     * @param jsonObject
     * @return
     */
    public JSONObject createDatabaseAndTable(JSONObject jsonObject) {
        if (jsonObject == null) {
            return JointJsonResult.joinJson("failed", "query is null");
        }
        //是否新建数据库的标识，"1" 代表是新建得数据库，"0"代表的选择已有的数据库
        //没有新建库场景
        String flag = "0";
        StringBuffer stringBuffer = new StringBuffer();
        String databaseName = jsonObject.getString("databaseName");
        if (StringUtils.isNotBlank(flag)&&"1".equals(flag)) {
            stringBuffer.append("CREATE DATABASE " +databaseName+";");//新建数据库
            Map executeSql = new HashMap();
            executeSql.put("executeSql", stringBuffer);
//            int num = dataBaseMapper.createDatabaseAndTable(executeSql);
            int num = cDataBaseMapper.createDatabaseAndTable(executeSql);
        }
        //datahouse需要将这部分注释掉
       // stringBuffer.append("USE " + databaseName+";");
        JSONArray jsonArray = (JSONArray) jsonObject.get("tableList");
        //如果持久层是clickhouse需要
        this.analyzeTableList4ClickHouse(jsonArray, stringBuffer,databaseName,jsonObject);
        //this.analyzeTableList4Mysql(jsonArray, stringBuffer);
        Map executeSql = new HashMap();
        executeSql.put("executeSql", stringBuffer);
        int num = 0;
        try {
//             num = dataBaseMapper.createDatabaseAndTable(executeSql);
             num = cDataBaseMapper.createDatabaseAndTable(executeSql);
        }catch (Exception ex){
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        Map map = new HashMap();
        map.put("num", num);
        return JointJsonResult.joinJson("success", map);
    }

    /**
     * 查询该数据库中的所有的字段类型
     * @return
     */
    public JSONObject selectColumnType() {
        //查询数据库
        List<String> list = null;
        Map dataMap = new HashMap();
        try {
//            list = dataBaseMapper.selectColumnType();
            list = cDataBaseMapper.selectColumnType();
            if (list != null && list.size() > 0) {
                dataMap.put("columnTypeList", list);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", dataMap);
    }

    //针对clickhouse拼接sql主体
    private StringBuffer analyzeTableList4ClickHouse(JSONArray jsonArray,StringBuffer stringBuffer,String databaseName,JSONObject dataJson) {
        if (jsonArray!=null && jsonArray.size()>0) {
            String tableName = dataJson.getString("tableName");
            stringBuffer.append("CREATE TABLE "+databaseName+".`" + tableName + "` ");
            stringBuffer.append("(");
            String primaryKey = "";
            String primaryKeys = "";
            for (int i=0,length=jsonArray.size();i<length;i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String columnName = jsonObject.getString("columnName");
                String columnType = jsonObject.getString("columnType");
                stringBuffer.append("`" + columnName + "` "+columnType+", ");
                //表中最少有一个主键
                String priKey = jsonObject.getString("columnIsKey");
                if(StringUtils.isNotBlank(priKey)&&"1".equals(priKey)) {
                    primaryKey+=columnName+",";
                }
                //字段注释
                String columnAnnotation = jsonObject.getString("columnAnnotation");
                if (StringUtils.isNotBlank(columnAnnotation)) {
                    stringBuffer.append(" COMMENT '" + columnAnnotation + "' ");
                }
                primaryKeys = primaryKey.substring(0, primaryKey.length() - 1);
            }
            stringBuffer.append("createdate Date) ENGINE= ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard}/"+tableName+"','{replica}',createdate,(" + primaryKeys + "),8192)");
        }
        return stringBuffer;
    }
    //针对mysql拼接表字段主体
    private StringBuffer analyzeTableList4Mysql(JSONArray jsonArray,StringBuffer stringBuffer) {
        if (jsonArray!=null && jsonArray.size()>0) {
            stringBuffer.append("(");
            String primaryKey = "";
            for (int i=0,length=jsonArray.size();i<length;i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if(i>0) stringBuffer.append(",");
                String columnName = jsonObject.getString("columnName");
                stringBuffer.append("`" + columnName + "` ");
                Long columnLength = jsonObject.getLong("columnLength");
                columnLength = columnLength == null ? 0 : columnLength;
                String columnType = jsonObject.getString("columnType");
                if ("date".equalsIgnoreCase(columnType) || "text".equalsIgnoreCase(columnType)) {
                    columnLength = 0L;
                }
                Long columnPoint = jsonObject.getLong("columnPoint");
                columnPoint = columnPoint == null ? 0 : columnPoint;
                if (columnLength != 0 && columnPoint != 0&&("double".equalsIgnoreCase(columnType)||"float".equalsIgnoreCase(columnType))) {
                    stringBuffer.append(columnType + "(" + columnLength + "," + columnPoint + ") ");
                } else if (columnLength != 0) {
                    stringBuffer.append(columnType + "(" + columnLength + ") ");
                } else {
                    stringBuffer.append(columnType + " ");
                }
                //默认表中只有一个主键，没有考虑联合主键的情况
                String priKey = jsonObject.getString("columnIsKey");
                Long isNull = jsonObject.getLong("columnIsNull");
                isNull = isNull == null ? 0 : isNull;
                if(StringUtils.isNotBlank(priKey)&&"1".equals(priKey)) {
                    primaryKey = columnName;
                    stringBuffer.append("NOT NULL ");
                }
                //1表示不为null
                else if(isNull==1) stringBuffer.append("NOT NULL ");
                else stringBuffer.append("DEFAULT NULL");
                //注释
                String columnAnnotation = jsonObject.getString("columnAnnotation");
                if (StringUtils.isNotBlank(columnAnnotation)) {
                    stringBuffer.append(" COMMENT '" + columnAnnotation + "' ");
                }
            }
            stringBuffer.append(",PRIMARY KEY(" + primaryKey + "))");
        }
        return stringBuffer;
    }

    /**
     * 查询所有的库和所有的表(拼接前端树状结构)
     * @return
     */
    public JSONObject selectDatabaseAndTable() {
        JSONArray js = new JSONArray();
        Map map = new HashMap();
        try {
            List<String> list = cDataBaseMapper.queryDatabaseName();
//            List<String> list = dataBaseMapper.queryDatabaseName();
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObjectTable = new JSONObject();
                Map databaseMap = new HashMap();
                String databaseName = list.get(i);
                jsonObject.put("database", databaseName);
                databaseMap.put("databaseName", databaseName);
                List<String> tableList = cDataBaseMapper.selectTables(databaseMap);
//                List<String> tableList = dataBaseMapper.selectTables(databaseMap);
                for (int j = 0; j < tableList.size(); j++) {
                    jsonObjectTable.put("database", tableList.get(j));
                    jsonArray.add(jsonObjectTable);
                }
                jsonObject.put("tables", jsonArray);
                js.add(jsonObject);
            }
            map.put("data", js);
        }catch (Exception ex){
            ex.printStackTrace();
            return JointJsonResult.joinJson("error", "query happen exception");
        }
        return JointJsonResult.joinJson("success", map);
    }


}
