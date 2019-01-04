package cn.com.ultrapower.housemapper;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/8/22.
 */
public interface CDataBaseMapper {

    //查询数据库中的所有表
    public List<String> selectTables(Map dataBaseName);
    //查询该表中的所有字段
    public List<Map> selectTableColumn(Map map);
    public List<String> selectTableColumnAttr(Map map);
    //查询所有的字段类型
    public List<String> selectColumnType();
    //查询列表
    public List<Map> queryList(Map whereMap);
    public int queryCount(Map whereMap);
    //查询该连接下所有的数据库名称
    public List<String> queryDatabaseName();
    public String queryDatabaseIsExist(Map databaseMap);
    //建表动作（创建数据库和表）
    public int createDatabaseAndTable(Map executeSql);
}
