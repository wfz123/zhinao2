package cn.com.ultrapower.method.service.database;

import cn.com.ultrapower.method.entity.TreeEntity;
import cn.com.ultrapower.utils.StringUtils;
import com.sun.org.apache.bcel.internal.generic.DADD;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import sun.reflect.generics.tree.Tree;

import java.sql.*;
import java.util.*;

/**
 * Created by wfz on 2018/10/10.公共查询字典数据方法（MySQL库测试）
 */
@Service
public class TreeTest {
    static String DB_DRIVER = "";
    static String DB_URL = "";
    static String DB_UNAME = "";
    static String DB_PWD = "";
    public TreeTest(){
        initPath();
    }
    //查询某库某表的字段信息方法入口
    public Map executeAnalysis(Map paramMap){
        Map resultMap = new HashMap();
        List list = recursionMethod(paramMap);
        resultMap.put("content",list);
        return resultMap;
    }

    /**
     * 树
     * @param map
     * @return
     */
    public List recursionMethod(Map map) {
        String firstSQL = "select pid id,dtName label,parentId pid from test_dic";
        List dataList = executeSelectColumns(firstSQL);
        List<TreeEntity> list  = getTreeTop(dataList);
        return list;
    }

    //获取所有父元素
    public List<TreeEntity> getTreeTop(List list){
        ArrayList<TreeEntity> arrayList = new ArrayList<TreeEntity>();
        for (int i=0,length=list.size();i<length;i++) {
            Map dataMap = (Map) list.get(i);
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
        for (TreeEntity entity : arrayList) {
            getTreeChildren(entity, list);
        }
        return arrayList;
    }
    //获取单个元素下所有的字元素
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
    /***
     * 执行sql语句
     * @param sql
     * @return
     */
    public List executeSelectColumns(String sql){
        Connection conn = initConnection();
        Statement statement =null;
        List list = null;
        try {
            System.out.println("SQL:"+sql);
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql.toString());
            list = convertList(resultSet);
            //根据标识返回不同的数据结构
            resultSet.close();
            statement.close();
            conn.close();
        }catch (Exception e){
            System.out.println("ERROR:查询数据异常，请检查数据情况...");
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

}
