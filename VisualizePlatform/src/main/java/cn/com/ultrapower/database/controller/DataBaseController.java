package cn.com.ultrapower.database.controller;

import cn.com.ultrapower.database.service.DataBaseService;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by admin on 2018/8/22.
 */
@Controller
@RequestMapping("database")
public class DataBaseController {
    @Autowired
    private DataBaseService dataBaseService;

    /**
     * 查询数据库中的所有表
     * @return
     */
    @RequestMapping(value = "/selectTables", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject selectTables(String databaseStr){
        JSONObject jsonObject = JSONObject.fromObject(databaseStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return dataBaseService.selectTables(jsonMap);
    }

    /**
     * 查询所有的库和所有的表（拼接成前端的插件得数据结构）
     * @return
     */
    @RequestMapping(value = "/selectDatabaseAndTable", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject selectDatabaseAndTable(){
        return dataBaseService.selectDatabaseAndTable();
    }

    /**
     * 查询一个表中的所有的字段、字段类型、字段注释
     * @param tableNameStr
     * @return
     */
    @RequestMapping(value = "/selectTableColumn", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject selectTableColumn(String tableNameStr){
        JSONObject jsonObject = JSONObject.fromObject(tableNameStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        JSONObject json = dataBaseService.selectTableColumn(jsonMap);
        return json;
    }

    /**
     * 查询表中字段以及属性
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/selectTableColumnAttr", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject selectTableColumnAttr(String queryStr){
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        JSONObject json = dataBaseService.selectTableColumnAttr(jsonMap);
        return json;
    }

    /**
     * 查询列表信息
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/queryInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryProInfo(@Param("queryStr") String queryStr) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        JSONObject json = dataBaseService.queryInfo(jsonMap);
        return json;
    }

    /**
     * 查询所有的数据库
     * @return
     */
    @RequestMapping(value = "/queryDatabaseName", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryDatabaseName() {
        return dataBaseService.queryDatabaseName();
    }
    /**
     * 查询数据库中所有的字段类型
     * @return
     */
    @RequestMapping(value = "/selectColumnType", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject selectColumnType() {
        return dataBaseService.selectColumnType();
    }
    /**
     * 查询是否存在该数据库
     * @return
     */
    @RequestMapping(value = "/queryDatabaseIsExist", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryDatabaseIsExist(String databaseNameStr) {
        JSONObject jsonObject = JSONObject.fromObject(databaseNameStr);
        Map databaseMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return dataBaseService.queryDatabaseIsExist(databaseMap);
    }

    /**
     * 查询该表是否存在
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/queryTableIsExist", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryTableIsExist(String queryStr) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map databaseMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return dataBaseService.queryTableIsExist(databaseMap);
    }

    /**
     * 建表动作（创建数据库和表）
     * @param databaseTableStr
     * @return
     */
    @RequestMapping(value = "/createDatabaseAndTable", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject createDatabaseAndTable(String databaseTableStr) {
        JSONObject jsonObject = JSONObject.fromObject(databaseTableStr);
        return dataBaseService.createDatabaseAndTable(jsonObject);
    }


}
