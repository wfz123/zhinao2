package cn.com.ultrapower.groupmanage.controller;

import cn.com.ultrapower.groupmanage.service.GroupmanageService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("groupmanage")
public class GroupmanageController {
    @Autowired
    private GroupmanageService groupmanageService;
    /**
     * 添加分组
     * @param insertStr
     * @return
     */
    @RequestMapping(value = "/saveGroupmanage", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject saveGroupmanage(String insertStr){
        JSONObject jsonObject = JSONObject.fromObject(insertStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return groupmanageService.saveGroupmanage(jsonMap);
    }
    /**
     * 查询分组
     * @param queryGroupmanageName
     * @return
     */
    @RequestMapping(value = "/queryGroupmanageName", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryGroupmanageName(String queryGroupmanageName){
        JSONObject jsonObject = JSONObject.fromObject(queryGroupmanageName);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return groupmanageService.queryGroupmanageName(jsonMap);
    }
    /**
     * 根据id修改分组
     *  @param GroupmanageStr
     * @return
     */
    @RequestMapping(value = "/updateGroupnamageById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject updateGroupnamageById(String GroupmanageStr){
        JSONObject jsonObject = JSONObject.fromObject(GroupmanageStr);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return groupmanageService.updateGroupnamageById(map);
    }
    /**
     * 添加分组关系
     * @param insertStr
     * @return
     */
    @RequestMapping(value = "/saveGrouprelationhtml", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject saveGrouprelationhtml(String insertStr){
        JSONObject jsonObject = JSONObject.fromObject(insertStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return groupmanageService.saveGrouprelationhtml(jsonMap);
    }
    /**
     * 根据htmlId修改分组关系
     *  @param grouprelationByHtmlId
     * @return
     */
    @RequestMapping(value = "/updateByhtmlId", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject updateByGroupId(String grouprelationByHtmlId){
        JSONObject jsonObject = JSONObject.fromObject(grouprelationByHtmlId);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return groupmanageService.updateByhtmlId(map);
    }
    /**
     * 根据groupId删除分组
     *  @param dropGroupByGroupId
     * @return
     */
    @RequestMapping(value = "/dropGroup", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject dropGroup(String dropGroupByGroupId){
        JSONObject jsonObject = JSONObject.fromObject(dropGroupByGroupId);
        Map map = (Map) JSONObject.toBean(jsonObject,Map.class);
        return groupmanageService.dropGroup(map);
    }
    /**
     * 查询分组页面信息
     * @param queryGroupInformation
     * @return
     */
    @RequestMapping(value = "/queryGroupList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryGroupList(String  queryGroupInformation){
        JSONObject jsonObject = JSONObject.fromObject(queryGroupInformation);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return groupmanageService.queryGroupList(jsonMap);
    }
    /**
     * 查询分组页面列表
     * @param queryStr
     * @return
     */
    @RequestMapping(value = "/queryGroupHtmlList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryUnGroupHtmlList(String  queryStr){
        JSONObject jsonObject = JSONObject.fromObject(queryStr);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return groupmanageService.queryGroupHtmlList(jsonMap);
    }
    /**
     * 根据id查询分组信息
     * @param queryGroupmanageById
     * @return
     */
    @RequestMapping(value = "/queryGroupmanageById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryGroupmanageById(String queryGroupmanageById){
        JSONObject jsonObject = JSONObject.fromObject(queryGroupmanageById);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return groupmanageService.queryGroupmanageById(jsonMap);
    }

    /**
     * 分组名称重名验证
     *
     * @param queryGroupName
     * @return
     */
    @RequestMapping(value = "/queryGroupName", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject queryGroupName(String queryGroupName){
        JSONObject jsonObject = JSONObject.fromObject(queryGroupName);
        Map jsonMap = (Map) JSONObject.toBean(jsonObject, Map.class);
        return groupmanageService.queryGroupName(jsonMap);
    }
}
