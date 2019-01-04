package cn.com.ultrapower.method.controller;

import cn.com.ultrapower.method.service.analysis.AnalysisMethodService;
import cn.com.ultrapower.method.service.database.*;
import cn.com.ultrapower.method.service.delete.DeleteDataToDH;
import cn.com.ultrapower.method.service.insert.InsertDataToDH;
import cn.com.ultrapower.method.service.query.QueryDataToDH;
import cn.com.ultrapower.method.service.update.UpdateDataToDH;
import cn.com.ultrapower.utils.HttpClientUtil;
import cn.com.ultrapower.utils.JointJsonResult;
import cn.com.ultrapower.utils.PropertiesUtil;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wfz on 2018/9/10.调用公共方法的服务
 */
@Controller
@RequestMapping("common")
public class InvokeMethodController {
    @Autowired
    private QueryDataToDH queryDataToDH;
    @Autowired
    private DeleteDataToDH deleteDataToDH;
    @Autowired
    private UpdateDataToDH updateDataToDH;
    @Autowired
    private InsertDataToDH insertDataToDH;
    @Autowired
    private SelectTablesToDH selectTablesToDH;
    @Autowired
    private SelectColumnsToDH selectColumnsToDH;
    @Autowired
    private SelectColumnsToMySQL selectColumnsToMySQL;
    @Autowired
    private SelectTablesToMySQL selectTablesToMySQL;
    @Autowired
    private TreeTest treeTest;
    @Autowired
    private TestRow testRow;
    @Autowired
    private AnalysisMethodService analysisMethodService;

    /**
     * 公共查询
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/queryMethod", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject executeQueryMethod(String queryStr) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map paramMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        Map returnMap = queryDataToDH.executeAnalysis(paramMap);
        return JointJsonResult.joinJson("success",returnMap);
    }
    /**
     * 公共添加
     * @param insertStr
     * @return
     */
    @RequestMapping(value = "/insertMethod", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map executeInsertMethod(String insertStr) {
        JSONObject jsonObject = JSONObject.fromObject(insertStr);
        Map paramMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return insertDataToDH.executeAnalysis(paramMap);
    }

    /**
     * 公共更新
     * @param updateStr
     * @return
     */
    @RequestMapping(value = "/updateMethod", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map executeUpdateMethod(String updateStr) {
        JSONObject jsonObject = JSONObject.fromObject(updateStr);
        Map paramMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return updateDataToDH.executeAnalysis(paramMap);
    }

    /**
     * 公共删除
     * @param deleteStr
     * @return
     */
    @RequestMapping(value = "/deleteMethod", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map executeDeleteMethod(String deleteStr) {
        JSONObject jsonObject = JSONObject.fromObject(deleteStr);
        Map paramMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return deleteDataToDH.executeAnalysis(paramMap);
    }
    /**
     * 公共查询某个库的所有表和表引擎
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/selectTablesToDH", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map executeSelectTablesToDH(String queryStr) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map paramMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        Map returnMap = selectTablesToDH.executeAnalysis(paramMap);
        return JointJsonResult.joinJson("success",returnMap);
    }
    /**
     * 公共查询某个库的所有表和表引擎
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/selectTablesToMySQL", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map executeSelectTablesToMySQL(String queryStr) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map paramMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        Map returnMap = selectTablesToMySQL.executeAnalysis(paramMap);
        return JointJsonResult.joinJson("success",returnMap);
    }
    /**
     * 公共查询某个库某个表的所有字段信息
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/selectColumnsToDH", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map executeSelectColumnsToDH(String queryStr) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map paramMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        Map returnMap = selectColumnsToDH.executeAnalysis(paramMap);
        return JointJsonResult.joinJson("success",returnMap);
    }

    /**
     * 公共查询某个库某个表的所有字段信息
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/selectColumnsToMySQL", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map executeSelectColumnsToMySQL(String queryStr) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map paramMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        Map returnMap = selectColumnsToMySQL.executeAnalysis(paramMap);
        return JointJsonResult.joinJson("success",returnMap);
    }

    /**
     * 查询树结构
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/testTree", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map executeTestTree(String queryStr) {
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map paramMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        Map returnMap = treeTest.executeAnalysis(paramMap);
        return JointJsonResult.joinJson("success",returnMap);
    }

    /**
     * 测试总条数
     * @return
     */
    @RequestMapping(value = "/testRow", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map executeTestRow() {
        Map returnMap = testRow.executeAnalysis();
        return JointJsonResult.joinJson("success",returnMap);
    }
    /**
     * 语义分析
     * @return
     */
    @RequestMapping(value = "/analysisMethod", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject executeAnalysisMethod(String analysisStr) {
        JSONObject jsonObject = JSONObject.fromObject(analysisStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return analysisMethodService.executeAnalysis(jsonMap);
    }

}
