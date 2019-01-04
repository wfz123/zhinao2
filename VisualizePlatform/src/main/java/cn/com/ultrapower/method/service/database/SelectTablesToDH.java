package cn.com.ultrapower.method.service.database;

import cn.com.ultrapower.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
/**
 * Created by wfz on 2018/10/10.公共查询表信息方法（DH库）
 */
@Service
public class SelectTablesToDH {
    static String DB_DRIVER = "";
    static String DB_URL = "";
    static String DB_UNAME = "";
    static String DB_PWD = "";
    public SelectTablesToDH(){
        initPath();
    }
    //查询某库的所有表方法入口
    public Map executeAnalysis(Map paramMap){
        Map resultMap = new HashMap();
        if(paramMap.containsKey("databaseName")) {
            String databaseName = String.valueOf(paramMap.get("databaseName"));
            if(StringUtils.isNotBlank(databaseName)) {
                 String sql = "select name,engine from `system`.tables WHERE database = '"+databaseName+"'";
                List list = executeSelectTables(sql);
                resultMap.put("content",list);
            } else resultMap.put("content", "param is null");
        }
        return resultMap;
    }

    /***
     * 执行sql语句
     * @param sql
     * @return
     */
    public List executeSelectTables(String sql){
        Connection conn = initConnection();
        Statement statement =null;
        List list = null;
        try {
            System.out.println("SQL:"+sql);
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql.toString());
            list = convertList(resultSet);
            resultSet.close();
            statement.close();
            conn.close();
        }catch (Exception e){
            System.out.println("ERROR:查询库中所有表异常，请检查数据情况...");
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                conn.close();
            }catch (Exception e){}
        }
        return list;
    }

    private List convertList(ResultSet rs) throws SQLException {
        List list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();//获取键名
        int columnCount = md.getColumnCount();//获取行的数量
        while (rs.next()) {
            Map rowData = new HashMap();//声明Map
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));//获取键名及值
            }
            list.add(rowData);
        }

        return list;
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
        testMap.put("where","pid=$content.pid$");
        testMap.put("content","[{'pid':'3a'}]");
        System.out.println(new SelectTablesToDH().executeAnalysis(testMap));
    }
}
