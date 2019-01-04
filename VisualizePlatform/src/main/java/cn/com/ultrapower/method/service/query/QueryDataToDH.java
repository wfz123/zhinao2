package cn.com.ultrapower.method.service.query;
/**
 * Created by admin on 2018/9/6.公共查询
 */
import cn.com.ultrapower.method.entity.TreeEntity;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.PageUtils;
import cn.com.ultrapower.utils.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

@Service
public class QueryDataToDH {

    static String DB_DRIVER = "";
    static String DB_URL = "";
    static String DB_UNAME = "";
    static String DB_PWD = "";

    public QueryDataToDH() {
        initPath();
    }

    /* map中需要存放的参数：
       * tableName:"test1,test2,test3"
       * columnName:"test.pid,test.name,test2.pid,test3.uid"
       * where:"test.pid=$whereContent.test.pid$,test.name=$whereContent.test.name$"
       * whereContent:"{"pid":"1","name"="张三"}"
       * assoication:[{"test":"test.pid","test3":"test3.uid"},{"test2":"test2.pid","test3":"tid"}]
       * "sortOrder":"DESC"
       * orderBy: "name,sex"
       * limit: {"page": "1","pagesize": "2"}
       * limitContent: {"page": "$limit.page$","pagesize": "$limit.pagesize$"}
       * top: "5"
       * select test.*,test2.*,test3.* from test JOIN test3 ON test.pid=test3.uid
       *  join test2 ON test2.pid=test3.tid
       * */

    /**
     * 公共查询入口
     * @param map
     * @return
     */
    public Map executeAnalysis(Map map) {
        Map resultMap = new HashMap();
        String sqlType = (String) map.get("whereType");//自定义SQL和公共模板的标识（自定义为"1",否则为"0"）
        String type = (String) map.get("resultType");//结果标识
        String mapType = "";//地图标识
        if(map.containsKey("mapType")&&StringUtils.isNotBlank((String) map.get("mapType"))) {
            mapType = (String) map.get("mapType");
        }
        //自定义SQL
        if(StringUtils.isNotBlank(sqlType)&&"1".equals(sqlType)) {
            String sqlStr = (String) map.get("where");
            String mulitStr = "";
            if(sqlStr.indexOf(" in ")!=-1||sqlStr.indexOf(" IN ")!=-1){
                mulitStr = this.dealMultipleChoice(sqlStr);
            }
            else {
                mulitStr = sqlStr;
            }
            String sqlEnd = this.dealSQLWhere(mulitStr);
            StringBuffer sql = new StringBuffer(sqlEnd);
            if (StringUtils.isNotBlank(type)) {
                if (("map".equals(type)&&"marker".equals(mapType)) //点图
                        || ("map".equals(type)&&"heatMap".equals(mapType)) //热力图
                        || ("map".equals(type)&&"clusterLabel".equals(mapType)) //汇聚图
                        || "list".equals(type) //list结果集(研判台传递的标识)
                        || "form".equals(type) //form表单
                        || "timeline".equals(type)//时间轴
                        || "tags".equals(type)//标签组
                 )
                {
                    executeQuery(resultMap, "list", sql);
                }
                //表格的自定义不需要输入limit分页条件
                if("table".equals(type)){
                    String limitStr = (String) map.get("limit");
                    Map limitMap = (Map) JSONObject.toBean(JSONObject.fromObject(limitStr), Map.class);
                    Map pageMap = PageUtils.getPageInfo(limitMap);
                    sql.append(" LIMIT " + pageMap.get("page") + "," + pageMap.get("pagesize"));
                    executeQuery(resultMap, "list", sql);
                    executeQuery(resultMap, "num", sql);
                }
                //图谱(柱图、饼图、折线图)
                else if ("bar".equals(type) || "pie".equals(type) || "line".equals(type)) {
                    executeQuery(resultMap, "graphCount", sql);
                }
                //树状
                else if("tree".equals(type)) {
                    String dtName = "";
                    if(sql.toString().toLowerCase().contains("and dtname=")) {
                        dtName = sql.toString().substring((sql.toString().toLowerCase().lastIndexOf("and dtname=")+12), sql.toString().length() - 1);
                        sql = new StringBuffer(sql.substring(0, sql.toString().toLowerCase().indexOf("and dtname=")));
                    }
                    executeQuery(resultMap, "tree", sql);
                    List list = (List) resultMap.get("list");
                    List treeList = getTreeTop(list,dtName);
                    resultMap.put("list", treeList);
                }
                //结果总条数
                else if("pagination".equals(type)){
                    executeQuery(resultMap, "num", sql);
                    JSONObject jsonObject = JSONObject.fromObject(resultMap.get("num"));
                    resultMap.put("list",jsonObject);
                    resultMap.remove("num");
                }
                //关系图
                else if("graph".equals(type)){
                    executeQuery(resultMap, "list", sql);
                    List<Map<String, Object>> list = (List) resultMap.get("list");
                    this.dealRealation(list, resultMap);
                }
            }
        }
        //拼接SQL
        else {
            if (StringUtils.isNotBlank(type)) {
                if ("list".equals(type)||"form".equals(type) //form表单
                        || "tags".equals(type) //查询标签组
                        || ("map".equals(type)&&"marker".equals(mapType)//点图
                        ||"timeline".equals(type)) //时间轴
                  )
                {
                    dealList(resultMap, map);
                }
                //table需要返回list数据，总条数以及当前页（前端传递）
                else if ("table".equals(type)) {
                    dealList(resultMap, map);
                    dealCount(resultMap,map);
                    //取出当前页
                    String limitStr = (String) map.get("limit");
                    Map limitMap = (Map) JSONObject.toBean(JSONObject.fromObject(limitStr), Map.class);
                    resultMap.put("page", limitMap.get("page"));
                }
                else if("timeline".equals(type)) {
                    dealList(resultMap, map);
                    dealCount(resultMap,map);
                }
                //图谱(柱图、饼图、折线图)
                else if ("bar".equals(type) || "pie".equals(type) || "line".equals(type)) {
                    dealBar(resultMap, map);
                }
                //树状
                else if("tree".equals(type)) {
                    dealTree(resultMap, map);
                }
                //热力图或者汇聚图
                else if("map".equals(type)&&("heatMap".equals(mapType)||"clusterLabel".equals(mapType))) {
                    dealHeatMap(resultMap, map,mapType);
                }
                //返回总条数
                else if("pagination".equals(type)){
                    dealCount(resultMap, map);
                    Object totalRowsList = resultMap.get("num");
                    JSONObject jsonObject = JSONObject.fromObject(totalRowsList);
                    resultMap.remove("num");
                    resultMap.put("list",jsonObject);
                }
                //关系图
                else if("graph".equals(type)){
                    dealList(resultMap, map);
                    List<Map<String, Object>> list = (List) resultMap.get("list");
                    this.dealRealation(list, resultMap);
                }
            }
        }
        return resultMap;
    }

    /**
     * 处理多选
     * @param dealWhereStr
     * @return
     */
    public String dealMultipleChoice(String dealWhereStr) {
        StringBuffer returnStr = new StringBuffer();
        String whereStr = dealWhereStr.substring(dealWhereStr.indexOf(" in ") + 4);
        String[] strArray = whereStr.replaceAll("'","").split(",");
        String str = "";
        for (int i = 0; i < strArray.length; i++) {
            if(i>0) str += ",";
            str += "'" + strArray[i] + "'";
        }
        returnStr.append(dealWhereStr.substring(0,dealWhereStr.indexOf(" in ")) + " in (").append(str).append(")");
        return returnStr.toString();
    }

    /**
     * 处理关系图返回结果
     * @param list
     * @param resultMap
     * @return
     */
    public Map dealRealation(List<Map<String, Object>> list,Map resultMap) {
        List<Map<String, Object>> nodeList = new ArrayList<>();
        List<Map<String, Object>> linkList = new ArrayList<>();
        Set<String> nodeSet = new HashSet<String>();
        //存放特征数据
        Set<String> featurevalueSet = new HashSet<String>();
        for (Map<String, Object> dataMap : list) {
            Map<String, Object> linkMap = new HashMap<>();
            String sourceValue = (String) dataMap.get("sourceId");
           // String relationType = (String) dataMap.get("relationType");
            String relationValue = (String) dataMap.get("relationValue");
            //去掉重复数据
            if(!nodeSet.contains(sourceValue)){
                nodeSet.add(sourceValue);
                Map caseMap = new HashMap<>();
                caseMap.put("name",sourceValue);
                nodeList.add(caseMap);
            }
            //如果当前特征值没有放在nodelist中，那么将当前特征值存放到nodelist中
            if(!featurevalueSet.contains(relationValue)){
                Map caseMap = new HashMap<>();
                caseMap.put("name", relationValue);
                nodeList.add(caseMap);
            }
            featurevalueSet.add(relationValue);
            linkMap.put("source", dataMap.get("sourceId"));//图形展示时的起始节点
            if(dataMap.containsKey("relationValue")) {
                linkMap.put("target", dataMap.get("relationValue"));//图形展示时的目标节点
            }
            linkMap.put("relationType", dataMap.get("relationType"));//图形展示时，起始节点与目标节点的关系
            linkList.add(linkMap);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nodes", nodeList);
        jsonObject.put("links", linkList);
        resultMap.put("list", jsonObject);
        return resultMap;

    }
    /**
     * 查询列表
     *
     * @param resultMap
     * @param paramMap
     * @return
     */
    public Map dealList(Map resultMap, Map paramMap) {
        String flag = "list";
        String columnName = (String) paramMap.get("columnName");
        StringBuffer sql = new StringBuffer("SELECT " + columnName + " FROM ");
        montageSQL(flag, paramMap, sql);
        return executeQuery(resultMap, flag, sql);
    }

    /**
     * 查询列表数量
     *
     * @param resultMap
     * @param paramMap
     * @return
     */
    public Map dealCount(Map resultMap, Map paramMap) {
        String flag = "num";
        StringBuffer sql = new StringBuffer("SELECT COUNT(1) num FROM ");
        montageSQL(flag, paramMap, sql);
        return executeQuery(resultMap, flag, sql);
    }
    /**
     * 图表
     * @param resultMap
     * @param paramMap
     * @return
     */
    public Map dealBar(Map resultMap, Map paramMap){
        String flag = "graphCount";
        String columnName = (String) paramMap.get("columnName");
        StringBuffer sql = new StringBuffer("SELECT " + columnName + ",count(1) num FROM ");
        montageSQL(flag, paramMap, sql);
        return executeQuery(resultMap, flag, sql);
    }
    /**
     * 处理树结构
     * @param resultMap
     * @param paramMap
     * @return
     */
    public Map dealTree(Map resultMap, Map paramMap){
        StringBuffer sql = new StringBuffer("SELECT pid,parentId,dtName FROM ");
        sql.append(paramMap.get("tableName")+" ");
        dealReturnWhere(paramMap,sql);
        if(sql.toString().contains("WHERE")) {
            String dtName = "";
            if(sql.toString().toLowerCase().contains("and dtname=")) {
                dtName = sql.toString().substring((sql.toString().toLowerCase().lastIndexOf("and dtname=")+12), sql.toString().length() - 1);
                sql = new StringBuffer(sql.substring(0, sql.toString().toLowerCase().indexOf("and dtname=")));
            }
            executeQuery(resultMap, "list", sql);
            List list = (List) resultMap.get("list");
            List<TreeEntity> treeList = getTreeTop(list,dtName);
            resultMap.put("list", treeList);
        }
        else {
            resultMap.put("list", "treeData is null");
        }
        return resultMap;
    }
    /**
     * 查询第一级节点
     * @param list
     * @return
     */
    public List<TreeEntity> getTreeTop(List list,String dtName){
        ArrayList<TreeEntity> arrayList = new ArrayList<TreeEntity>();
        for (int i=0,length=list.size();i<length;i++) {
            Map dataMap = (Map) list.get(i);
            //这个是查询得省得pid
            if(StringUtils.isNotBlank(dtName)) {
                if(dataMap.get("dtName").equals(dtName)) {
                    TreeEntity entity = new TreeEntity();
                    entity.setPid((String) dataMap.get("parentId"));
                    entity.setId((String)dataMap.get("pid"));
                    entity.setLabel(dtName);
                    arrayList.add(entity);
                }
            }
            else {
                String parentId = (String) dataMap.get("parentId");
                String id = (String) dataMap.get("pid");
                if(parentId == null || "".equals(parentId)||"0".equals(parentId)) {
                    TreeEntity entity = new TreeEntity();
                    entity.setPid(parentId);
                    entity.setId(id);
                    entity.setLabel((String) dataMap.get("dtName"));
                    arrayList.add(entity);
                }
            }
        }
        for (TreeEntity entity : arrayList) {
            getTreeChildren(entity, list);
        }
        return arrayList;
    }
    /**
     * 获取单个节点下所有的子元素
     */
    public void getTreeChildren(TreeEntity entity,List list) {
        String id = entity.getId();
        for (int m=0,length=list.size();m<length;m++) {
            Map map = (Map) list.get(m);
            if(map.get("parentId") != null && !"".equals(map)) {
                String pid = (String) map.get("pid");
                if(map.get("parentId").equals(id)) {
                    List<TreeEntity> children = entity.getChildren();
                    if(children == null) {
                        children = new ArrayList<TreeEntity>();
                    }
                    TreeEntity treeEntity = new TreeEntity();
                    treeEntity.setId(pid);
                    treeEntity.setPid((String) map.get("parentId"));
                    treeEntity.setLabel((String) map.get("dtName"));
                    children.add(treeEntity);
                    if(children!=null) {
                        entity.setChildren(children);
                    }
                    getTreeChildren(treeEntity, list);
                }
            }
        }
    }

    /**
     * 处理热力图或者汇聚图
     * @param resultMap
     * @param paramMap
     * @return
     */
    public Map dealHeatMap(Map resultMap, Map paramMap,String mapType){
        String columnName = (String) paramMap.get("columnName");
        StringBuffer sql = new StringBuffer("SELECT " + columnName + ",count(1) count FROM ");
        montageSQL(mapType, paramMap, sql);
        return executeQuery(resultMap, "list", sql);
    }

    /**
     * 执行查询的主体
     * @param resultMap
     * @param flag
     * @param sql
     * @return
     */
    public Map executeQuery(Map resultMap, String flag, StringBuffer sql) {
        Connection conn = initConnection();
        Statement statement = null;
        try {
            System.out.println("SQL:" + sql);
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql.toString());
            //结果集
            List list = convertList(resultSet);
            //根据标识返回不同的数据结构
            returnData(flag, resultMap, list);
            resultSet.close();
            statement.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("ERROR:查询数据异常，请检查数据情况...");
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                conn.close();
            } catch (Exception e) {
                System.out.println("ERROR:资源释放失败，请检查资源情况...");
            }
        }
        return resultMap;
    }

    /**
     * 公用的拼接SQL片段的方法
     * @param flag
     * @param paramMap
     * @param sql
     * @return
     */
    public StringBuffer montageSQL(String flag, Map paramMap, StringBuffer sql) {
        String tableName = (String) paramMap.get("tableName");
        //表示单表
        if (tableName.indexOf(",") == -1) sql.append(tableName);
        //多表
        else dealJoin((String) paramMap.get("assoication"), sql);
        dealReturnWhere(paramMap, sql);
        //处理排序的
        if ("list".equals(flag)) {
            dealOrderBy(paramMap, sql);
            dealLimit(paramMap, sql);
        }
        //图表进行group by和热力图的count(UltraDB)
        else if("graphCount".equals(flag)||"heatMap".equals(flag)||"clusterLabel".equals(flag)) {
            sql.append(" group by " + paramMap.get("columnName"));
        }
        return sql;
    }

    /**
     * 处理最终WHERE条件
     * @param paramMap
     * @param sql
     * @return
     */
    public StringBuffer dealReturnWhere(Map paramMap,StringBuffer sql) {
        String where = (String) paramMap.get("where");
        String whereContentStr = (String) paramMap.get("content");
        if (StringUtils.isBlank(whereContentStr) && StringUtils.isNotBlank(where)) {
            if(where.contains(" in ")||where.contains(" IN ")) {
                sql.append(" WHERE 1=1 and ").append(dealMultipleChoice(where));
            }
            else{
                String endWhere = " and " + where;
                String resultWhere = trimEmptyWhere(endWhere);
                //如果返回的数据不是空就相当于有筛选条件
                if(StringUtils.isNotBlank(resultWhere)) sql.append(" WHERE 1=1").append(resultWhere);
            }
        }
        else if (StringUtils.isNotBlank(where) && StringUtils.isNotBlank(whereContentStr)) {
            JSONObject whereContent = JSONObject.fromObject(whereContentStr);
            String whereCondition = dealWhere(where, whereContent);
            //将条件为空的进行去除
            String endWhere = " and " + whereCondition;
            String resultWhere = trimEmptyWhere(endWhere);
            //如果返回的数据不是空就相当于有筛选条件
            if(StringUtils.isNotBlank(resultWhere)) sql.append(" WHERE 1=1").append(resultWhere);
        }
        return sql;
    }

    /**
     * 根据标识返回不同的数据结构
     * @param flag
     * @param resultMap
     * @param list
     * @return
     */
    private Map returnData(String flag,Map resultMap,List list) {
        if ("num".equals(flag)) resultMap.put(flag, list.get(0));//数量
        else if("graphCount".equals(flag)) {//处理图表
            List xAxisList = new ArrayList<>();
            List yAxisList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Map<String,Object> map = (Map) list.get(i);
                int j = 0;
                for(Map.Entry<String,Object> dataMap:map.entrySet()) {
                    ++j;
                    if(j==1) xAxisList.add(dataMap.getValue());
                    else yAxisList.add(dataMap.getValue());
                }
            }
            //组装前端需要的echar结构
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("xAxisList", xAxisList);
            jsonObject.put("yAxisList", yAxisList);
            resultMap.put("list", jsonObject);
        }
        else resultMap.put(flag,list);//列表
        return resultMap;
    }

    /**
     * 获取列表
     * @param rs
     * @return
     * @throws SQLException
     */
    private List convertList(ResultSet rs) throws SQLException {
        List list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();//获取键名
        int columnCount = md.getColumnCount();//获取行的数量
        while (rs.next()) {
            Map rowData = new HashMap();//声明Map
            for (int i = 1; i <= columnCount; i++) {
                if(rs.getObject(i)!=null)
                    rowData.put(md.getColumnName(i), rs.getObject(i));//获取键名及值
                else
                    rowData.put(md.getColumnName(i), "");//获取键名及值
            }
            list.add(rowData);
        }
        return list;
    }
    /**
     * 处理分页条件或者top
     *
     * @param paramMap
     * @param sql
     * @return
     */
    public StringBuffer dealLimit(Map paramMap, StringBuffer sql) {
        String top = (String) paramMap.get("top");
        if (StringUtils.isNotBlank(top)) sql.append(" LIMIT " + top);
        if(paramMap.containsKey("limit")) {
            String limitStr = (String) paramMap.get("limit");
            Map map = (Map) JSONObject.toBean(JSONObject.fromObject(limitStr), Map.class);
            Map pageMap = PageUtils.getPageInfo(map);
            if (StringUtils.isNotBlank("limit")) {
                JSONObject limit = JSONObject.fromObject(limitStr);
                sql.append(" LIMIT " + pageMap.get("page") + "," + pageMap.get("pagesize"));
            }
        }
        return sql;
    }

    /**
     * 处理排序的
     *
     * @param paramMap
     * @param sql
     * @return
     */
    public StringBuffer dealOrderBy(Map paramMap, StringBuffer sql) {
        String oderBy = (String) paramMap.get("orderBy");
        if (StringUtils.isNotBlank(oderBy)) {
            sql.append(" ORDER BY ").append(oderBy);
        }
        return sql;
    }

    /**
     * 处理where条件传递实参
     *
     * @param jsonObject
     * @param stringBuffer
     * @return
     */
    public StringBuffer dealWhereContent(JSONObject jsonObject, StringBuffer stringBuffer) {
        Map<String, String> map = (Map) JSONObject.toBean(jsonObject, LinkedHashMap.class);
        int m = 0;
        for (Map.Entry<String, String> js : map.entrySet()) {
            if (m > 0) stringBuffer.append(" AND ");
            stringBuffer.append(js.getKey() + "=" + "`" + js.getValue() + "`");
            m++;
        }
        return stringBuffer;
    }

    //初始化数据连接
    private static Connection initConnection() {
        Connection conn = null;
        try {
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_UNAME, DB_PWD);
        } catch (Exception e) {
            System.out.println("ERROR:数据库连接异常...");
            conn = null;
        }
        return conn;
    }

    /**
     * 对where条件中的空条件进行去除
     * @param where
     * @return
     */
    public String trimEmptyWhere(String where) {
        String result = where.replaceAll("(( and )?[a-zA-Z]+ like '%%')|" +
                "(( AND )?[a-zA-Z]+ like '%%')|" +
                "(( OR )?[a-zA-Z]+ like '%%')|" +
                "(( or )?[a-zA-Z]+ like '%%')|" +
                "(( and )?[a-zA-Z]+(=|>|>=|<|<=|!=)'')|" +
                "(( AND )?[a-zA-Z]+(=|>|>=|<|<=|!=)'')|" +
                "(( or )?[a-zA-Z]+(=|>|>=|<|<=|!=)''|" +
                "(( OR )?[a-zA-Z]+(=|>|>=|<|<=|!=)''))", "")
                .replaceAll("(\\( or |\\( and|\\( OR )|\\( AND", "(")
                .replaceAll("(and\\(\\))|(or\\(\\))|(OR\\(\\)|(AND\\(\\)))", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',[a-zA-Z]+\\)\\)=1", "")
                .replaceAll("and [a-zA-Z]+(>=|>|<|<=)toRelativeSecondNum\\(toDate\\(''\\)\\)", "");
        return result;
    }
    /**
     * 处理自定义sql中出现空的情况
     * @param sql
     * @return
     */
    public String dealSQLWhere(String sql) {
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
                /*.replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseHappenTimeType\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',applicationMethod\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseFeature\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseHouseType\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseHappenTimeType\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',applicationTool\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseHappenAddressType\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',suspectNumIf\\)\\)=1", "")
                .replaceAll("and calltime>toRelativeSecondNum\\(toDate\\(''\\)\\)", "")
                .replaceAll("and calltime<=toRelativeSecondNum\\(toDate\\(''\\)\\)", "");*/
        return sqlWhere;
    }


    public static void main(String[] args) {
        String sql1 = "select pid,caseId,caseType,caseSummary,caseContent,applicationMethod,caseHappenStyle,caseHappenAddress,caseHouseType,caseHappenTime,caseHappenTimeType,caseFeature,callPoliceTime,callPoilceTel,lostGoods,policeId,policeStation,saveTime,bdlongitude,bdlatitude,pgislongitude,pgislatitude,informantName,informantGender,informantAge,informantNo,householdRegister,suspectNum,suspectName,suspectGender,suspectAge,suspectFeatures,externalFeatures,monitorIf,synstatus FROM bs_case_householdtheft WHERE 1=1  and hasAny(splitByString(',',''),splitByString(';',caseHappenTimeType))=1 and hasAny(splitByString(',',''),splitByString(';',applicationMethod))=1 and hasAny(splitByString(',',''),splitByString(';',caseHouseType))=1 and hasAny(splitByString(',',''),splitByString(';',caseFeature))=1 LIMIT 0,10";
        String sql2 = "select pid,caseId,caseType,caseSummary,caseContent,applicationMethod,caseHappenAddress,caseHappenAddressType,caseHappenTime,caseHappenTimeType,caseHappenArea,callPoliceTime,callPoilceTel,lostGoods,policeId,policeStation,saveTime,bdlongitude,bdlatitude,pgislongitude,pgislatitude,informantName,informantGender,informantAge,informantNo,householdRegister,suspectNumIf,suspectSounds,suspectNum,suspectName,suspectGender,suspectWeigh,suspectCamouflage,suspectAge,suspectJacket,suspectTrousers,suspectHair,suspectHat,suspectShoes,suspectFeatures,externalFeatures,applicationTool,vehicle,vehicleBrand,vehicleType,vehicleColor,licensePlateNo,monitorIf,synstatus from bs_case_robbery where 1=1 and caseHappenAddress like '%北京市昌平区天通苑西三区%' and hasAny(splitByString(',',''),splitByString(';',caseHappenTimeType))=1 and hasAny(splitByString(',',''),splitByString(';',applicationTool))=1 and hasAny(splitByString(',',''),splitByString(';',caseHappenAddressType))=1";
        String sql3 = "select base.groupcode groupCode,count(distinct base.caseid) groupNum,(select count(distinct num_table.caseid) from (select new_num_table.caseid,new_num_table.groupcode,min(new_num_table.createDate) createDate from (select telcaseid as caseid,groupcode,createDate from bs_case_telrelationgroup union all select targetcaseid,groupcode,createDate from bs_case_telrelationgroup) new_num_table group by caseid,groupcode) num_table ANY INNER JOIN(select telcaseid as caseid,groupcode,createDate,createtime from bs_case_telrelationgroup union ALL select targetcaseid as caseid,groupcode,createDate,createtime from bs_case_telrelationgroup) base1 ON num_table.groupcode=base1.groupcode) newNum,max(base.createtime) createtime,max(base.createDate) createDate1 from (select telcaseid as caseid,groupcode,createDate,createtime from bs_case_telrelationgroup union ALL select targetcaseid as caseid,groupcode,createDate,createtime from bs_case_telrelationgroup) base where 1=1 and base.groupcode='' GROUP BY groupcode order by count(distinct base.caseid) desc,max(base.createDate) desc,base.groupcode desc";
        String str1 = "select m.pid,m.houseId,m.seriesPid,m.caseId,m.caseSummary,m.caseHappenTime,m.callPoliceTime,self.sp_rule FROM(select t.pid,\n" +
                " t.houseId,t.seriesPid,h.caseId,h.caseSummary,h.caseHappenTime,h.callPoliceTime FROM bs_case_theftandlogicseries t all INNER JOIN bs_case_householdtheft h on t.houseId=h.pid) m all inner JOIN bs_case_seriesshunt_self self ON m.seriesPid = self.pid WHERE 1=1 s.name like '%%' AND(m.caseId like '%%' OR m.sex='1')";
        String str2 = "select m.pid,m.houseId,m.seriesPid,m.caseId,m.caseSummary,m.caseHappenTime,m.callPoliceTime,self.sp_rule FROM(select t.pid,t.houseId,t.seriesPid,h.caseId,h.caseSummary,h.caseHappenTime,h.callPoliceTime FROM bs_case_theftandlogicseries t all INNER JOIN bs_case_householdtheft h on t.houseId=h.pid) m all inner JOIN bs_case_seriesshunt_self self ON m.seriesPid = self.pid WHERE 1=1 AND m.caseId=''";
        String str = "select s.pid,s.sp_rule,s.sp_describe,s.applicationMethod,s.caseHappenTime,s.caseFeature,s.caseHouseType,count(t.householdtheft_id) num from bs_case_seriesshunt s all INNER JOIN bs_case_theftandseriesshunt t on t.seriesshunt_id = s.pid WHERE 1=1 and sp_rule like '%%' GROUP BY s.pid,s.sp_rule,s.sp_describe,s.applicationMethod,s.caseHappenTime,s.caseFeature,s.caseHouseType,t.seriesshunt_id";
        String sql4 = "where 1=1 and calltime>toRelativeSecondNum(toDate('')) and calltime<=toRelativeSecondNum(toDate(''))";
        String strrr= sql2.replaceAll("(( AND )?[a-zA-Z]+(\\.)?[a-zA-Z_]+ like '%%')|" +
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
                /*.replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseHappenTimeType\\)\\)=1","")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',applicationMethod\\)\\)=1","")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseFeature\\)\\)=1","")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseHouseType\\)\\)=1","")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseHappenTimeType\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',applicationTool\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',caseHappenAddressType\\)\\)=1", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',suspectNumIf\\)\\)=1", "")
                .replaceAll("and calltime>toRelativeSecondNum\\(toDate\\(''\\)\\)", "")
                .replaceAll("and calltime<=toRelativeSecondNum\\(toDate\\(''\\)\\)", "");*/
        String result = sql2.replaceAll("(( and )?[a-zA-Z]+ like '%%')|" +
                "(( AND )?[a-zA-Z]+ like '%%')|" +
                "(( OR )?[a-zA-Z]+ like '%%')|" +
                "(( or )?[a-zA-Z]+ like '%%')|" +
                "(( and )?[a-zA-Z]+(=|>|>=|<|<=|!=)'')|" +
                "(( AND )?[a-zA-Z]+(=|>|>=|<|<=|!=)'')|" +
                "(( or )?[a-zA-Z]+(=|>|>=|<|<=|!=)''|" +
                "(( OR )?[a-zA-Z]+(=|>|>=|<|<=|!=)''))", "")
                .replaceAll("(\\( or |\\( and|\\( OR )|\\( AND", "(")
                .replaceAll("(and\\(\\))|(or\\(\\))|(OR\\(\\)|(AND\\(\\)))", "")
                .replaceAll("and hasAny\\(splitByString\\(',',''\\),splitByString\\(';',[a-zA-Z]+\\)\\)=1", "")
                .replaceAll("and [a-zA-Z]+(>=|>|<|<=)toRelativeSecondNum\\(toDate\\(''\\)\\)", "");
       // String newStr = strrrr.replaceAll("(( and )?[a-zA-Z]+(\\.)[a-zA-Z_]+(=\\'\\'))","");
        long timers = System.currentTimeMillis() / 1000;
        String format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timers);
        String format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(format1+"时间-----");
        System.out.println(format+"时间-----");
        System.out.println(strrr);
        //System.out.println(result);
        String dealEstr = "select * from test_table where pid in '11,22,333'";
        //String s = dealMultipleChoice(dealEstr);
        //System.out.println(s);
    }

    /**
     * 处理where条件
     *
     * @param whereStr
     * @param obj
     * @return
     */
    public String dealWhere(String whereStr, JSONObject obj) {
        StringBuffer returnResult = new StringBuffer();
        String[] whereArray = whereStr.split(" AND ");
        for (int m = 0; m < whereArray.length; m++) {
            String where = whereArray[m];
            char[] chars = where.toCharArray();
            int start = 0;
            int end = 0;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '$') {
                    if (start == 0) start = i;
                    else end = i;
                }
                if (start > 0 && end > 0) {
                    String tmp = where.substring(start, end + 1).replace("whereContent.", "").replace("$", "");
                    String value = obj.get(tmp).toString();
                    String newWhere = where.replace("$whereContent." + tmp + "$", "'" + value + "'");
                    start = 0;
                    end = 0;
                    returnResult.append(newWhere + " AND ");
                }
            }
        }
        String result = returnResult.substring(0, returnResult.length() - 5);
        return result;
    }

    /**
     * 处理表连接
     *
     * @param assoication
     * @param stringBuffer
     * @return
     */
    public StringBuffer dealJoin(String assoication, StringBuffer stringBuffer) {
        JSONArray jsonArray = JSONArray.fromObject(assoication);
        //[{"test":"test.pid","test3":"test3.uid"},{"test2":"test2.pid","test3":"test3.tid"}]
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = jsonArray.size(); i < length; i++) {
            JSONObject json = (JSONObject) jsonArray.get(i);
            Map<String, String> map = (Map) JSONObject.toBean(json, LinkedHashMap.class);
            int m = 0;
            for (Map.Entry<String, String> js : map.entrySet()) {
                ++m;
                if (m != 1) {
                    stringBuffer.append(" JOIN " + js.getKey() + " ON ");
                    sb.append(js.getValue());
                } else {
                    //将第一个表名追加
                    if (i == 0) stringBuffer.append(js.getKey());
                    sb.append(js.getValue() + "=");
                }
            }
            stringBuffer.append(sb);
            sb.delete(0, sb.length());
        }
        return stringBuffer;

    }

    //读取properties文件获取服务器地址
    public void initPath() {
        Properties properties = new Properties();
        try {
            properties.load(QueryDataToDH.class.getResourceAsStream("/config/config.properties"));
            DB_DRIVER = properties.getProperty("DB_DRIVER");
            DB_URL = properties.getProperty("DB_URL");
            DB_UNAME = properties.getProperty("DB_UNAME");
            DB_PWD = properties.getProperty("DB_PWD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
